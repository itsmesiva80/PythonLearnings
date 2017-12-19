pipeline {
    agent any

    stages {
        stage("Fill the truck") {
            steps {
                ansiColor("xterm") {
                    git branch: "${env.BRANCH_NAME}", credentialsId: "xxxxxx", url: "https://user@company/project.git"
                }
            }
        }
        
        stage("Start the engine") {
            tools {
                maven "Maven-3.3.9"
                jdk "Java-8-oracle"
            }
            
            steps {
                parallel MainEngine: {
                    ansiColor("xterm") {
                        retry (4) {
                            buildParent()
                        }
                    }
                },
                Frontend: {
                    dir ("frontend") {
                        ansiColor("xterm") {
                            sh "mvn -T 4 -B clean deploy"
                        }
                    }
                },
                SERFrontend: {
                    dir ("merchantServiceArea3Frontend") {
                        withEnv(["PATH+NODE=${tool name: "NodeJS-6.9.1", type: "jenkins.plugins.nodejs.tools.NodeJSInstallation"}/bin"]) {
                            ansiColor("xterm") {
                                sh "npm install"
                                sh "bower install"
                                sh "mkdir -p build"
                                sh "ember build --environment=production --output-path=build"
                            }
                        }
                    }
                    dir ("serviceFrontend/build") {
                        ansiColor("xterm") {
                            sh "tar -cvzf ../ser.tar.gz ./*"
                        }
                    }
                },
                HostedPages: {
                    dir ("hostedPages") {
                        withEnv(["PATH+NODE=${tool name: "NodeJS-6.9.1", type: "jenkins.plugins.nodejs.tools.NodeJSInstallation"}/bin"]) {
                            ansiColor("xterm") {
                                sh "npm install"
                                sh "bower install"
                                //sh "ember test --reporter xunit"
                                //junit "**/Test-*.xml"
                                sh "mkdir -p build"
                                sh "ember build --environment=production --output-path=build"
                            }
                        }
                    }
                    dir ("hostedPages/build") {
                        ansiColor("xterm") {
                            sh "tar -cvzf ../hostedPages.tar.gz ./*"
                        }
                    }
                },
                PcocoBridge: {
                    dir ("Bridge") {
                        withEnv(["PATH+NODE=${tool name:  "NodeJS-6.9.1", type: "jenkins.plugins.nodejs.tools.NodeJSInstallation"}/bin"]) {
                            ansiColor("xterm") {
                                sh "mvn -T 4 -B clean deploy"
                            }
                        }
                    }
                }
            }
        }
        
        stage("Deliver packages") {
            steps {
                parallel Parent: {
                    archiveArtifacts "**/*.zip,**/*.war,**/*.jar"
                },
                Frontend: {
                    dir ("frontend/target") {
                        archiveArtifacts "*.tar.gz"
                    }
                },
                SERfrontend: {
                    dir ("merchantServiceArea3Frontend") {
                        archiveArtifacts "*.tar.gz"
                    }
                },
                HostedPages: {
                    dir ("hostedPages") {
                        archiveArtifacts "*.tar.gz"
                    }
                },
                PcocoBridge: {
                    dir ("Bridge/target") {
                        archiveArtifacts "*.tar.gz"
                    }
                }
            }
        }
     
    }
}

def buildParent() {
    notifyStash("INPROGRESS")

    try {
        sh "mvn -T 4 -B clean deploy"
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
          stashServerBaseUrl: "https://bitbucketurl.de"])
}
