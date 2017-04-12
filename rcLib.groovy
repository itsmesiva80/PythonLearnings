
def rc_create(String projectName) {
    if (env.GIT_CREDENTIALS_ID == null) {
        env.GIT_CREDENTIALS_ID = 'github-com-flaconi'
    }

    env.GIT_BASE_URL = 'git@github.com:Flaconi/'
    git credentialsId: env.GIT_CREDENTIALS_ID, url: env.GIT_BASE_URL + projectName + '.git'
    checkoutAndReleaseConfirmation()
    selectedCommitHash = getCommitHashFromUser()
    createTagAndPush(selectedCommitHash)
}

def checkoutAndReleaseConfirmation() {
    sh('git tag | xargs git tag -d && git fetch --tags')//resync tags
    lastReleaseName = shr('git tag -l --sort=version:refname "release-*" | tail -n 1')
    echo lastReleaseName
    lastReleaseHash = shr('git rev-list -n 1 ' + lastReleaseName)
    echo lastReleaseHash
    lastReleaseMasterHash = getLastReleaseMasterHash(lastReleaseHash)
    commitsSinceLastRelease = shrLines('git rev-list --abbrev-commit ' + lastReleaseMasterHash + '..HEAD')
    if (commitsSinceLastRelease.length == 0) {
        error 'No commits since last release!'
    }
    filteredCommits = []
    for (i = 0; i < commitsSinceLastRelease.size(); i++) {
        commitHash = commitsSinceLastRelease[i]
        if (commitHash == "") {
            break
        }
        commitMessage = shr('git log -n 1 --pretty=format:%s ' + commitHash + ' | tail -n 1')
        isMergeCommit = commitMessage.startsWith("Merge pull request")
        echo commitHash
        if (isMergeCommit == true) {
            commitMessage = shr('git --no-pager log --format=%B -n 1 ' + commitHash + ' | tail -n 1')
        }
        filteredCommits << commitMessage + ' - ' + commitHash
    }
}

def createTagAndPush(String selectedCommitHash) {
    lastReleaseName = shr('git tag -l --sort=version:refname "release-*" | tail -n 1')
    t = lastReleaseName[8..-1]
    t2 = t.split('-')
    lastReleaseNumber = t2[0].toInteger()

    newReleaseNumber = lastReleaseNumber + 1
    newReleaseName = 'release-' + newReleaseNumber.toString() + '-' + new Date().format('yyyyMMdd')
    currentBuild.displayName = newReleaseName

    sh 'git tag ' + currentBuild.displayName + ' ' + selectedCommitHash + ' && git push --tags'
}

def getCommitHashFromUser() {
    i = input message: 'Select the last stable ticket ' +
            '[ordered from the newest to oldest down to the previous release]',
            parameters: [[$class     : 'ChoiceParameterDefinition', choices: filteredCommits.join('\n'),
                          description: '', name: 'release-commit']]
    selectedCommitHash = i[-7..-1]
    return selectedCommitHash
}
def getLastReleaseMasterHash(String lastReleaseHash) {
    t = shr('git log --pretty=format:%H $(git rev-parse HEAD) | (grep ' + lastReleaseHash + ' || true)')
    echo t
    if (t != "") {
        return lastReleaseHash
    }

    return shr('git merge-base ' + lastReleaseHash + ' master')
}

def shr(String cmd) { // not my proudest hack
    String tmp = org.apache.commons.lang.RandomStringUtils.random(9, true, true)
    sh cmd + ' > ' + tmp
    return readFile(tmp).trim()
}

def shrLines(String cmd) { // not my proudest hack
    String tmp = org.apache.commons.lang.RandomStringUtils.random(9, true, true)
    sh cmd + ' > ' + tmp
    content = readFile(tmp).trim()
    return content.split('\n')
}

return this