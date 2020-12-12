pipeline {
    agent any

    stages {
        stage('Copy Artifact') {
            steps {
                copyArtifacts(projectName: 'markturn');
            }
        }
    }
}
