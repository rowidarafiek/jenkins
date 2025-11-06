def call() {
    echo "Building application..."

    if (fileExists('pom.xml')) {
        sh 'mvn clean package -DskipTests'
    } else if (fileExists('build.gradle')) {
        sh 'gradle clean build -x test'
    } else {
        echo "No build system detected."
    }
}

