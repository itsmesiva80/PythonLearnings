#!/usr/bin/env groovy

    env.NEXUS_SNAPSHOT_VERSION = "${RELEASE_VERSION}-${FEATURE_REF}-SNAPSHOT"
    env.IMG_VERSION = "${RELEASE_VERSION}-${FEATURE_REF}"

pipeline {

    agent {label 'jenkins-slave-01'}

    stages {
        stage("Checkout Repo") {
            steps {
                ansiColor("xterm") {
                    git branch: "cibuild", credentialsId: "xxxxxx", url: "https://user@company/prj.git"
                }
            }
        }

        stage("Download Artifacts") {
            steps {
               //Snapshot Version
                sh 'echo "Nexus Snapshot Version: $NEXUS_SNAPSHOT_VERSION"'

                sh 'echo "Docker Image Version: $IMG_VERSION"'

                sh 'bash dr-download-nexus-artifacts.sh "$NEXUS_SNAPSHOT_VERSION" '

                sh 'bash dr-copy-artifacts.sh'
            }
        }

        stage("Build and Push Docker Images") {
            steps {
                withDockerRegistry([credentialsId: 'docker-login', url: 'https://registry.docker.de']) {
                    sh 'IMG_VERSION="$IMG_VERSION" docker-compose -f docker-compose-build.yml build '
                    sh 'IMG_VERSION="$IMG_VERSION" docker-compose -f docker-compose-build.yml push'
                }

            }
        }

        stage("Deploy to Feature Machine") {
            steps {
                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@10.10.12.11 -i ~/.ssh/jkslave01_rsa echo "login success"'

                echo 'Login success after ssh'

                sh 'scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ~/.ssh/jkslave01_rsa  docker-compose.yml application@10.10.12.11:/home/application/docker-compose.yml'

                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@10.10.12.11 -i /home/jkslave01/.ssh/jkslave01_rsa IMG_VERSION="$IMG_VERSION" docker login -u docker-robot -p SWoiw0lXq7JYwMJ71hcu https://registry.docker.de'

                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@10.10.12.11 -i /home/jkslave01/.ssh/jkslave01_rsa IMG_VERSION="$IMG_VERSION" docker-compose -f /home/application/docker-compose.yml up -d '
            }
        }
    }
}
