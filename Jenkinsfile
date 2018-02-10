node {
    stage("Checkout") {
        checkout scm
    }
    stage("Docker build") {
        def customImage = docker.build("qassim/qbert-scala:${env.BUILD_ID}")
    }
    stage("Docker push") {
        customImage.push()
        customImage.push('latest')
    }
}
