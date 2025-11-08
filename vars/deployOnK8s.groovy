def call(String imageName, String buildNumber) {
    echo "Deploying ${imageName}:${buildNumber} to Kubernetes..."

    sh """
        if kubectl get deployment myapp; then
            kubectl set image deployment/myapp myapp=${imageName}:${buildNumber}
        else
            kubectl create deployment myapp --image=${imageName}:${buildNumber}
            kubectl expose deployment myapp --port=80 --target-port=8080 --type=LoadBalancer
        fi
    """
}

