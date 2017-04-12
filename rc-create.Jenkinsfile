#!/usr/bin/env groovy

String libRepo = 'jenkins-toolchains'
String projectName = 'nekton-ui'
//this script cannot be used in pipeline, it is just for reference.
node('git') {
    step([$class: 'WsCleanup'])
    stage('Create RC') {
        if (env.GIT_CREDENTIALS_ID == null) {
            env.GIT_CREDENTIALS_ID = 'github-com-flaconi'
        }

        env.GIT_BASE_URL = 'git@github.com:Flaconi/'
        git credentialsId: env.GIT_CREDENTIALS_ID, url: env.GIT_BASE_URL + libRepo + '.git'
        def pipe = load "./jenkins/rcLib.groovy"
        pipe.rc_create(projectName) //change your project name
    }

    env.TAG_NAME = currentBuild.displayName
    env.GIT_REF = currentBuild.displayName
    load './Jenkinsfile'
    step([$class: 'WsCleanup'])
}
