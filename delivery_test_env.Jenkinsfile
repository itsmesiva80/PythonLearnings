#!/usr/bin/env groovy

properties([
   parameters([string(
      defaultValue: '',
      description: 'Git reference to be deployed',
      name: 'GIT_REF'
   )]),
   pipelineTriggers([])
])

String projectName = "nekton-ui"

if (env.GIT_REF == null) {
    error 'GIT_REF must be set.'
} else if( (env.GIT_REF).take(7) == 'release' ) {
    GIT_REF = 'tags/' + env.GIT_REF
} else {
    GIT_REF = env.GIT_REF
}

def dockerLogin = ''
def executeCommandRemotely = 'ssh -o StrictHostKeyChecking=no root@testing.f2.flaconi.net -p 10004'
def moveFileRemotely = 'scp -P 10004 -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'

stage('Get updated Docker login credentials') {
    node('aws-sdk') {
        withCredentials([usernamePassword(
           credentialsId: 'jenkins-docker-ecr-f2',
           passwordVariable:'AWS_SECRET_ACCESS_KEY',
           usernameVariable: 'AWS_ACCESS_KEY_ID'
        )]) {
            dockerLogin = sh returnStdout: true, script: 'aws ecr get-login --region eu-west-1'
        }
    }
}

stage('Fetch server configuration') {
    node('git') {
        git credentialsId: 'github-com-flaconi', url: 'git@github.com:Flaconi/' + projectName + '.git'
        String checkoutName = GIT_REF
        sh "git checkout ${checkoutName}"
        stash includes: "codebase/ui/**", name: 'src'
        stash includes: 'infra/**', name: 'src'
    }
}

stage('Deliver To Server') {
    node('docker') {
        unstash 'src'
        sshagent(['jenkins-f2-testing-team-ironman']) {
            sh "${executeCommandRemotely} -- ${dockerLogin}"
            sh "${moveFileRemotely} infra/docker/docker-compose.testing.yaml " +
                    "root@testing.f2.flaconi.net:/root/" + projectName + "/docker-compose.yaml"
            sh "${executeCommandRemotely} 'cd /root/" + projectName + " && GIT_REF=${env.GIT_REF} docker-compose pull'"

            withCredentials([file(
                credentialsId: projectName + '-testing.env',
                variable: 'NEKTON_UI_TESTING'
            )]) {
                sh "${moveFileRemotely} ${env.NEKTON_UI_TESTING} root@testing.f2.flaconi.net:/root/" + projectName +
                        "/.env"
            }

            sh "${executeCommandRemotely} 'cd /root/" + projectName + " && GIT_REF=${env.GIT_REF} docker-compose up -d'"
            step([$class: 'WsCleanup'])
        }
    }
}


