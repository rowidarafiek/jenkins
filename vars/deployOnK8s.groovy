def callSimple(String imageName, String buildNumber) {
    echo "Deploying to Kubernetes (simple)..."
    sh """
        kubectl set image deployment/myapp myapp=${imageName}:${buildNumber} || \
        kubectl create deployment myapp --image=${imageName}:${buildNumber}
        
        kubectl expose deployment myapp --port=80 --target-port=8080 --type=LoadBalancer || true
    """
}
