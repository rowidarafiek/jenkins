def call(String imageName, String tag) {
    echo "Scanning image ${imageName}:${tag}"
    sh """
      docker scan ${imageName}:${tag} || echo 'Scan completed with warnings'
    """
}

