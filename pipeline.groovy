pipeline {
    agent any

    stages {
        stage('Build Package') {
            agent {
                docker {
                    image 'drudy/markturn_ubuntu20.04'
                    args '--user root'
                }
            }

            steps {
                sh 'cat /etc/os-release'
                sh 'curl -L https://github.com/UAComputerScience/devops-f20-final-exam-dmrudy17/archive/v1.0.0.tar.gz | tar xz'
                sh 'mv devops-f20-final-exam-dmrudy17-1.0.0 /Source'
                sh 'mkdir /Build'
                sh 'cd /Build; cmake /Source -DLINK_STATIC=OFF -G Ninja'
                sh 'cd /Build; ninja'
                sh 'cd /Build; ctest'
                sh 'cd /Build; cpack -G DEB'
                sh 'cp /Build/*.deb dist/.'
                archiveArtifacts artifacts: 'dist/*.deb', followSymlinks: false
            }
        }
    }
}
