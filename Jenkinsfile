pipeline {
  agent any

  environment {
    APP_NAME = 'jenkins-app'
    IMAGE_NAME = 'rowidarafiek/app'
    DEPLOYMENT_FILE = 'k8s/deployment.yaml'
  }

  stages {

    stage('Clone Repository') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'github-cred',
          usernameVariable: 'GIT_USER',
          passwordVariable: 'GIT_PASS'
        )]) {
          sh '''
            echo "Cloning repository..."
            rm -rf jenkins
            git clone https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/jenkins.git
            cd jenkins
          '''
        }
      }
    }

    stage('Run Unit Tests') {
      steps {
        dir('jenkins') {
          sh '''
            echo "Running unit tests..."
            if [ -f pom.xml ]; then
              mvn test
            else
              echo "No tests found."
            fi
          '''
        }
      }
    }

    stage('Build Application') {
      steps {
        dir('jenkins') {
          sh '''
            echo "Building application..."
            if [ -f pom.xml ]; then
              mvn clean package -DskipTests
            else
              echo "No Maven project found. Skipping build."
            fi
          '''
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        dir('jenkins') {
          sh '''
            echo "Building Docker image..."
            docker build -t $IMAGE_NAME:$BUILD_NUMBER .
          '''
        }
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'dockerhub-cred',
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          dir('jenkins') {
            sh '''
              echo "Pushing Docker image to Docker Hub..."
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push $IMAGE_NAME:$BUILD_NUMBER
            '''
          }
        }
      }
    }

    stage('Delete Local Docker Image') {
      steps {
        sh '''
          echo "Deleting local Docker image..."
          docker rmi $IMAGE_NAME:$BUILD_NUMBER || true
        '''
      }
    }

    stage('Update Deployment File') {
      steps {
        dir('jenkins/k8s') {
          sh '''
            echo "Updating deployment file..."
            sed -i "s#{{IMAGE}}#$IMAGE_NAME#g; s#{{TAG}}#$BUILD_NUMBER#g" deployment.yaml
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        withCredentials([
          string(credentialsId: 'api-cred', variable: 'K8S_API_SERVER'),
          string(credentialsId: 'sa-cred', variable: 'K8S_SA_TOKEN')
        ]) {
          dir('jenkins/k8s') {
            sh '''
              echo "Deploying to Kubernetes..."
              export KUBECONFIG=$WORKSPACE/.kubeconfig

              kubectl config set-cluster cluster \
                --server=$K8S_API_SERVER \
                --insecure-skip-tls-verify=true \
                --kubeconfig=$KUBECONFIG

              kubectl config set-credentials jenkins \
                --token=$K8S_SA_TOKEN \
                --kubeconfig=$KUBECONFIG

              kubectl config set-context ctx \
                --cluster=cluster \
                --user=jenkins \
                --namespace=default \
                --kubeconfig=$KUBECONFIG

              kubectl config use-context ctx --kubeconfig=$KUBECONFIG

              kubectl apply -f deployment.yaml --kubeconfig=$KUBECONFIG
              kubectl rollout status deploy/$APP_NAME --timeout=120s --kubeconfig=$KUBECONFIG
            '''
          }
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
