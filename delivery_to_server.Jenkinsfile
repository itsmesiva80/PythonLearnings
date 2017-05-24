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
String srcPath = "codebase/ui"
String serverName = "jenkins-f2-testing-team-ironman"
String vmPort = "10004"
String composeFileName = "./infra/docker/docker-compose.testing.yaml"

if (env.GIT_REF == null) {
    error 'GIT_REF must be set.'
} else if( (env.GIT_REF).take(7) == 'release' ) {
    GIT_REF = 'tags/' + env.GIT_REF
} else {
    GIT_REF = env.GIT_REF
}

def pipe

stage('Fetch server configuration') {
    node('git') {
        checkoutLibrary()
        pipe = load "./jenkins/deliveryLib.groovy"
        pipe.checkoutGitRef(projectName, GIT_REF, srcPath)
    }
}

stage('Get updated Docker login credentials') {
    node('aws-sdk') {
        dockerLogin = pipe.dockerLogin()
    }
}

stage('Deliver To Server') {
    node('docker') {
        pipe.deliverToServer(serverName, vmPort, projectName, composeFileName, GIT_REF, dockerLogin)
        step([$class: 'WsCleanup'])
    }
}

def checkoutLibrary(){
    git credentialsId: 'github-com-flaconi', url: 'git@github.com:Flaconi/jenkins-pipelines.git'
}

