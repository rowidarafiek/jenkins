@Library('shared-lib') _

pipeline {
    agent { label 'linux-docker' }

    environment {
        IMAGE_NAME = 'rowidarafiek/app'
        DOCKER_CREDS = 'dockerhub-cred'
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    echo "Cloning repository..."
                    withCredentials([usernamePassword(
                        credentialsId: 'github-cred',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS'
                    )]) {
                        sh '''
                            rm -rf jenkins
                            git clone https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/jenkins.git
                        '''
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                dir('jenkins') {
                    script {
                        UnitTest()
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                dir('jenkins') {
                    script {
                        buildApp()
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        buildImage(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        callSimple(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        pushImage(IMAGE_NAME, BUILD_NUMBER, DOCKER_CREDS)
                    }
                }
            }
        }

        stage('Remove Local Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        removeImage(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                dir('jenkins') {
                    script {
                        deployOnK8s(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
            sh 'docker image prune -f || true'
        }
        failure {
            echo "Pipeline failed!"
            sh 'docker image prune -f || true'
        }
        always {
            echo "Pipeline finished"
            cleanWs(deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true)
        }
    }
}

