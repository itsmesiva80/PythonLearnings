#!/usr/bin/env groovy
properties([parameters([string(
        defaultValue: '',
        description: 'This is your release name, ' +
                'it must be already existing and created through the other Jenkins job.',
        name: 'RC_NAME'),
        string(
                defaultValue: '',
                description: 'The commit hash to patch the release with.',
                name: 'COMMIT_HASH'
        )]),
            pipelineTriggers([])
])

String projectName = "nekton-ui"

if (env.RC_NAME == null || env.COMMIT_HASH == null) {
    error 'Empty RC_NAME or COMMIT_HASH'
}

node('git') {
    stage('Cleanup and Checkout') {
        step([$class: 'WsCleanup'])

        git credentialsId: 'github-com-flaconi', url: 'git@github.com:Flaconi/' + projectName + '.git'

        output = shr("git branch --contains ${env.COMMIT_HASH}")
        if (output == "") {
            error 'COMMIT_HASH for the patch does not exist in the master branch'
        }

        sh 'git checkout tags/${env.RC_NAME}'

        sh 'git config user.email "jenkins@flaconi.de"'
        sh 'git config user.name "Jenkins CI"'
    }

    stage('Prepare temp. patches branch') {
        sh 'git checkout -b ${env.RC_NAME}-patches'
    }

    stage('Cherry-Pick') {
        sh 'git cherry-pick ${env.COMMIT_HASH}'
    }

    stage('Git tag & push') {
        sh 'git tag --force ${env.RC_NAME}'
        sh 'git push --force origin tags/${env.RC_NAME}'
    }

    env.TAG_NAME = env.RC_NAME
    env.GIT_REF = env.RC_NAME
    load './Jenkinsfile'
    step([$class: 'WsCleanup'])
}

def shr(String cmd) { // not my proudest hack
    String tmp = org.apache.commons.lang.RandomStringUtils.random(9, true, true)
    sh cmd + ' > ' + tmp
    return readFile(tmp).trim()
}


