pipeline {
  agent any

  environment {
    IMAGE_NAME = 'rowidarafiek/app'
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
              echo "No test files found"
            fi
          '''
        }
      }
    }

    stage('Build Application') {
      steps {
        dir('jenkins') {
          sh '''
            echo "Building the application..."
            if [ -f pom.xml ]; then
              mvn clean package -DskipTests
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

    stage('Push Docker Image to Registry') {
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

    stage('Delete Docker Image') {
      steps {
        sh '''
          echo "Deleting local Docker image..."
          docker rmi $IMAGE_NAME:$BUILD_NUMBER || true
        '''
      }
    }
  }

  post {
    always {
      sh 'docker image prune -f || true'
    }
  }
}
