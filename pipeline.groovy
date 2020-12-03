pipeline {
    agent {
        docker {
            image 'drudy/ubuntu20.04'
            args '--user root'
        }
    }

    stages {
        stage('Setup') {
            steps {
                git branch: 'main', credentialsId: 'GitHub', url: 'https://github.com/UAComputerScience/markturn-dmrudy17.git'
            }
        }
        stage('CMake') {
            steps {
                cmakeBuild buildDir: 'build', cmakeArgs: '-D LINK_STATIC=OFF', generator: 'Ninja', installation: 'cmake'
            }
        }
        stage('Build') {
            steps {
                sh 'cd build; ninja'
            }
        }
    }
}
