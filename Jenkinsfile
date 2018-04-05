node {
    def GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
    def customImage
    stage("Checkout") {
        checkout scm
    }
    stage("Build") {
        customImage = docker.build("qassim/qbert-scala:${GIT_COMMIT_HASH}")
    }
    if (env.BRANCH_NAME == "master"){
        stage("Push") {
            customImage.push()
            customImage.push('latest')
        }
        stage("Deploy") {
            sshagent (credentials: ['docker-host']) {
                sh 'ssh -o StrictHostKeyChecking=no qassim@docker-host.qn "cd ~/qbert-scala && ./pull-and-restart.sh"'
            }
        }
    }
}
