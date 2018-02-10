node {
    stage("Checkout") {
        checkout scm
    }
    stage("Build") {
        def customImage = docker.build("qassim/qbert-scala:${env.BUILD_ID}")
    }
    stage("Push") {
        customImage.push()
        customImage.push('latest')
    }
    stage("Deploy") {}
}
