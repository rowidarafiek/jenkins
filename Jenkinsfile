@Library('shared-lib') _

pipeline {
    agent { label 'agent-1' }

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
                    UnitTest.run()  // Calls UnitTest.groovy
                }
            }
        }

        stage('Build Application') {
            steps {
                dir('jenkins') {
                    buildApp.run()  // Calls buildApp.groovy
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    buildImage.run(IMAGE_NAME, BUILD_NUMBER)  // Calls buildImage.groovy
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    scanImage.run(IMAGE_NAME, BUILD_NUMBER)  // Calls scanImage.groovy
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                dir('jenkins') {
                    pushImage.run(IMAGE_NAME, BUILD_NUMBER, 'dockerhub-cred')  // Calls pushImage.groovy
                }
            }
        }

        stage('Remove Local Docker Image') {
            steps {
                removeImage.run(IMAGE_NAME, BUILD_NUMBER)  // Calls removeImage.groovy
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                deployOnK8s.run(IMAGE_NAME, BUILD_NUMBER)  // Calls deployOnK8s.groovy
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f || true'
        }
    }
}

