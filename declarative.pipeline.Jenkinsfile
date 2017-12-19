
pipeline {
    agent {label 'jenkins-slave-01'}

    stages {
        stage("Checkout Repo") {
            steps {
                ansiColor("xterm") {
                    git branch: "${env.BRANCH_NAME}", credentialsId: "xxxxxx", url: "https://user@company/project.git"
                }
            }
        }

        stage("Clean and Deploy") {
            tools {
                maven "Maven-3.3.9"
                jdk "Java-8-oracle"
            }
            steps {
                buildParent()
            }
        }
    }
}


def buildParent() {
    notifyStash("INPROGRESS")

    try {
        sh 'mvn -B clean deploy -e -X'
        notifyStash('SUCCESS')
    } catch(err) {
        notifyStash('FAILED')
    }
}

def notifyStash(String state) {

    if((state == "SUCCESS") || (state == "FAILED")) {
        currentBuild.result = state
    }

    echo "Setting state in Bitbucket to ${state}"

    step([$class: "StashNotifier",
          commitSha1: "",
          credentialsId: "xxxxxx",
          disableInprogressNotification: false,
          ignoreUnverifiedSSLPeer: true,
          includeBuildNumberInKey: false,
          prependParentProjectKey: false,
          projectKey: "",
          stashServerBaseUrl: "https://bitbucket.de"])
}
