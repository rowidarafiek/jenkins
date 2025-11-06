def call(String imageRepo, String buildNumber) {
    withCredentials([
        string(credentialsId: 'api-cred', variable: 'K8S_API_SERVER'),
        string(credentialsId: 'sa-cred', variable: 'K8S_SA_TOKEN')
    ]) {
        deployOnK8s(imageRepo, buildNumber, K8S_API_SERVER, K8S_SA_TOKEN)
    }
}

