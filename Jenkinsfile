node {
    def customImage
    stage("Checkout") {
        checkout scm
    }
    stage("Build and push") {
        customImage = docker.build("qassim/qbert-scala:${env.BUILD_ID}")
    }
    stage("Push") {
        customImage.push()
        customImage.push('latest')
    }
    stage("Deploy") {
        sshagent(['docker-host']) {
            sh 'ssh qassim@192.168.1.242'
        }
    }
}
