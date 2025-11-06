def call(String imageName, String tag, String user, String pass) {
    echo "Pushing image ${imageName}:${tag} to Docker Hub"
    sh """
      echo "${pass}" | docker login -u "${user}" --password-stdin
      docker push ${imageName}:${tag}
    """
}

