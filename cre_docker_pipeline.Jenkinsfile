#!/usr/bin/env groovy

    env.NEXUS_SNAPSHOT_VERSION = "${FEATURE_REF}"
    env.BUILD = "${BUILD}"
    if ("${FEATURE_REF}".contains("release/")) {
      env.BRANCH = "release"
    } else {
      env.BRANCH = "${FEATURE_REF}"
    }
    env.IMG_VERSION = "${RELEASE_VERSION}-${env.BRANCH}-${BUILD}"

pipeline {

    agent {label 'jenkins-slave-01'}

    stages {
        stage("Checkout Repo") {
            steps {
                ansiColor("xterm") {
                    deleteDir() /* clean up our workspace */
                    git branch: "cibuild", credentialsId: "xxxxxxxx", url: "https://jenkins-dev@xxxx.de/scm/ops/docker.git"
                }
            }
        }

        stage("Download Artifacts") {
            steps {
               //Snapshot Version
                sh 'echo "Nexus Snapshot Version: $NEXUS_SNAPSHOT_VERSION"'

                sh 'echo "Docker Image Version: $IMG_VERSION"'

                sh 'bash dr-download-nexus-artifacts.sh "$NEXUS_SNAPSHOT_VERSION" "$BUILD" '

                sh 'bash dr-copy-artifacts.sh'
            }
        }

        stage("Build and Push Docker Images") {
            steps {
                withDockerRegistry([credentialsId: 'docker-login', url: 'https://registry.xxxx.de']) {
                    sh 'IMG_VERSION="$IMG_VERSION" docker-compose -f docker-compose-build.yml build '

                    sh 'IMG_VERSION="$IMG_VERSION" docker-compose -f docker-compose-build.yml push'
                }

            }
        }

        stage("Cleanup Running Containers") {
            steps {
                build job: 'docker-cleanup-all-apps'
            }
        }

        stage("Deploy to Feature Machine") {
            steps {
                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@host -i ~/.ssh/jkslave01_rsa echo "login success"'

                echo 'Login success after ssh'

                sh 'scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ~/.ssh/jkslave01_rsa  docker-compose.yml application@10.10.12.11:/home/application/docker-compose.yml'

                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@host -i /home/jkslave01/.ssh/jkslave01_rsa IMG_VERSION="$IMG_VERSION" docker login -u docker-robot -p xxxx https://xxxxx.de'

                sh 'ssh -o StrictHostKeyChecking=no -p 22 application@host -i /home/jkslave01/.ssh/jkslave01_rsa IMG_VERSION="$IMG_VERSION" docker-compose -f /home/application/docker-compose.yml up -d '
            }
        }
        stage("Cleanup Images in Jenkins Slave") {
            steps {
                sh 'docker rmi $(docker images -q -f dangling=true)'
            }
        }
    }
}
