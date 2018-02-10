node {
    def customImage
    stage("Checkout") {
        checkout scm
    }
    stage("Build") {
        docker.build("qassim/qbert-scala:${env.BUILD_ID}")
    }
    stage("Push") {
        customImage.push()
        customImage.push('latest')
    }
    stage("Deploy") {}
}
