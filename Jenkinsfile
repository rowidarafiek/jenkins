pipeline {
    agent any
    
    environment {
        // Docker Hub credentials
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-cred')
        DOCKER_IMAGE_NAME = 'rowidarafiek/frontend:v2'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        
        // GitHub credentials
        GIT_CREDENTIALS = 'github-cred'
        
        // Kubernetes credentials
        K8S_API_SERVER = credentials('apicred')
        K8S_SERVICE_ACCOUNT = credentials('sa-cred')
        
        // Application settings
        GIT_REPO = 'https://github.com/Ibrahim-Adel15/Jenkins_App.git'
        K8S_NAMESPACE = 'default'
    }
    
    stages {
        
        stage('Run Unit Tests') {
            steps {
                script {
                    echo '========== Running Unit Tests =========='
                    
                    // For Node.js application
                    sh '''
                        echo "Installing dependencies..."
                        npm install
                        
                        echo "Running unit tests..."
                        npm test
                    '''
                    
                    echo 'Unit tests completed successfully!'
                }
            }
        }
        
        stage('Build Application') {
            steps {
                script {
                    echo '========== Building Application =========='
                    
                    // For Node.js application
                    sh '''
                        echo "Building application..."
                        npm run build
                    '''
                    
                    echo 'Application built successfully!'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo '========== Building Docker Image =========='
                    
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest
                    """
                    
                    echo "Docker image built: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    echo '========== Pushing Image to Docker Hub =========='
                    
                    sh """
                        echo ${DOCKER_HUB_CREDENTIALS_PSW} | docker login -u ${DOCKER_HUB_CREDENTIALS_USR} --password-stdin
                        docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        docker push ${DOCKER_IMAGE_NAME}:latest
                    """
                    
                    echo 'Image pushed to Docker Hub successfully!'
                }
            }
        }
        
        stage('Clean Local Docker Images') {
            steps {
                script {
                    echo '========== Cleaning Local Docker Images =========='
                    
                    sh """
                        docker rmi ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} || true
                        docker rmi ${DOCKER_IMAGE_NAME}:latest || true
                        docker image prune -f || true
                    """
                    
                    echo 'Local Docker images cleaned!'
                }
            }
        }
        
        stage('Update Deployment YAML') {
            steps {
                script {
                    echo '========== Updating deployment.yaml with New Image =========='
                    
                    sh """
                        sed -i 's|image:.*|image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}|g' deployment.yaml
                        cat deployment.yaml
                    """
                    
                    echo 'deployment.yaml updated with new image tag!'
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo '========== Deploying to Kubernetes Cluster =========='
                    
                    sh """
                        kubectl config set-cluster k8s-cluster \
                            --server=${K8S_API_SERVER} \
                            --insecure-skip-tls-verify=true
                        
                        kubectl config set-credentials jenkins-sa \
                            --token=${K8S_SERVICE_ACCOUNT}
                        
                        kubectl config set-context k8s-context \
                            --cluster=k8s-cluster \
                            --user=jenkins-sa \
                            --namespace=${K8S_NAMESPACE}
                        
                        kubectl config use-context k8s-context
                        
                        kubectl apply -f deployment.yaml
                        
                        kubectl rollout status deployment/jenkins-app-deployment -n ${K8S_NAMESPACE}
                        
                        kubectl get pods -n ${K8S_NAMESPACE} -l app=jenkins-app
                    """
                    
                    echo 'Application deployed to Kubernetes successfully!'
                }
            }
        }
    }
    
    post {
        always {
            echo '========== Pipeline Execution Completed =========='
            echo "Build Number: ${BUILD_NUMBER}"
            echo "Build Status: ${currentBuild.currentResult}"
            echo "Image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
            
            cleanWs()
            sh 'docker logout || true'
        }
        
        success {
            echo '========== Pipeline Succeeded! =========='
            echo "Successfully deployed ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} to Kubernetes"
            echo "Deployment: jenkins-app-deployment"
            echo "Service: jenkins-app-service"
        }
        
        failure {
            echo '========== Pipeline Failed! =========='
            echo 'Please check the logs for error details'
            echo 'Possible rollback may be required'
        }
        
        unstable {
            echo '========== Pipeline Unstable =========='
            echo 'Some tests may have failed, but build continued'
        }
        
        aborted {
            echo '========== Pipeline Aborted =========='
            echo 'Pipeline was manually stopped'
        }
    }
}
