@Library('shared-lib') _

pipeline {
    agent { label 'linux docker' }

    environment {
        IMAGE_NAME = 'rowidarafiek/app'
    }

    stages {

        stage('Clone Repository') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-cred', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        echo "Cloning repository..."
                        rm -rf jenkins
                        git clone https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/jenkins.git
                    '''
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                dir('jenkins') {
                    script {
                        UnitTest.run()
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                dir('jenkins') {
                    script {
                        buildApp.run()
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        buildImage.run(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        scanImage.run(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        pushImage.run(IMAGE_NAME, BUILD_NUMBER, 'dockerhub-cred')
                    }
                }
            }
        }

        stage('Remove Local Docker Image') {
            steps {
                script {
                    removeImage.run(IMAGE_NAME, BUILD_NUMBER)
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    deployOnK8s.run(IMAGE_NAME, BUILD_NUMBER)
                }
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f || true'
        }
    }
}


