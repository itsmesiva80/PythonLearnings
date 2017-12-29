#!/usr/bin/env groovy



pipeline {

    agent {label 'jenkins-slave-01'}
    
    environment {
        SERVER_CONNECT = "ssh -o StrictHostKeyChecking=no -p 22 application@10.10.12.13 -i ~/.ssh/jkslave01_rsa"
        DOCKER_REGISTRY = "https://registry.de"
        DOCKER_ID = "docker-robot"
        DOCKER_PASS = "xxxxxx"
        DOCKER_IMAGE = "registry/config-server:latest"
    }
    
    stages {
        
        stage("Stop and Remove docker container") {
            steps {
                script {
                    sh '${SERVER_CONNECT} echo "login success"'
    
                    echo 'Login success after ssh'
                    try {
                        def container_status= sh script: "${SERVER_CONNECT} docker inspect -f '{{.State.Running}}' config-server", returnStdout: true
                        echo 'container status: ${container_status}'
                        if (container_status) {
        
                            sh '${SERVER_CONNECT} docker stop config-server '
                            
                            sh '${SERVER_CONNECT} docker rm config-server '
            
                            sh '${SERVER_CONNECT} docker rmi ${DOCKER_IMAGE} '
                        }
                    } catch(all) {
                        currentBuild.result = 'SUCCESS'
                    }
                }
            }
        }
    }
}
