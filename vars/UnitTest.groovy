def call() {
    if (fileExists('pom.xml')) {
        sh 'mvn test'
    } else if (fileExists('build.gradle')) {
        sh 'gradle test'
    } else {
        echo 'No build file found'
    }
}

