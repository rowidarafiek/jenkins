def call(String imageName, String tag) {
    echo "Removing local image ${imageName}:${tag}"
    sh "docker rmi ${imageName}:${tag} || true"
}

