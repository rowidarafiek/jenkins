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
                        // Call shared library function
                        unitTest()
                    }
                }
            }
        }
        
        stage('Build Application') {
            steps {
                dir('jenkins') {
                    script {
                        // Call shared library function
                        buildApp()
                    }
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        // Call shared library function with parameters
                        buildImage(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }
        
        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        // Call shared library function
                        scanImage(IMAGE_NAME, BUILD_NUMBER)
                    }
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    // Call shared library function
                    pushImage(IMAGE_NAME, BUILD_NUMBER, DOCKER_CREDS)
                }
            }
        }
        
        stage('Remove Local Docker Image') {
            steps {
                script {
                    // Call shared library function
                    removeImage(IMAGE_NAME, BUILD_NUMBER)
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Call shared library function
                    deployOnK8s(IMAGE_NAME, BUILD_NUMBER)
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
            // Clean up workspace if needed
            cleanWs(deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true)
        }
    }
}
