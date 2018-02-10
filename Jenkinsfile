node {
    checkout scm
    def customImage = docker.build("qassim/qbert-scala:${env.BUILD_ID}")
    customImage.push()
    customImage.push('latest')
}
