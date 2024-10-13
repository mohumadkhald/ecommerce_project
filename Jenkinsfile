pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        APP_NAME = "ecommerce_ci_cd"
        RELEASE = "1.0.0"
        DOCKER_USER = "mohumadkhald"
        DOCKER_PASS = 'Dockerhub'
        IMAGE_NAME = "${DOCKER_USER}/${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
        JENKINS_API_TOKEN = credentials("JENKINS_API_TOKEN")
    }

    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout from SCM") {
            steps {
                git branch: 'main',
                    credentialsId: 'Github',
                    url: 'https://github.com/mohumadkhald/e2e-CI_CD'
            }
        }

        stage('build app') {
            steps {
                script {
                    echo "Building the application..."
                    sh 'mvn clean package'
                }
            }
        }

        stage('build image') {
            steps {
                script {
                    echo "Building the Docker image..."
                    withCredentials([usernamePassword(
                        credentialsId: 'Dockerhub',
                        passwordVariable: 'PASS',
                        usernameVariable: 'USER'
                    )]) {
                        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                        sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push ${IMAGE_NAME}:latest"
                        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('clean image') {
            steps {
                script {
                    echo "Cleaning up Docker images..."
                    sh """
                        docker rmi -f ${IMAGE_NAME}:${IMAGE_TAG} || true
                        docker rmi -f ${IMAGE_NAME}:latest || true
                        docker image prune -f --filter dangling=true || true
                        docker logout
                    """
                }
            }
        }

        stage("Trigger CD Pipeline") {
            steps {
                script {
                    sh """
                        curl -v -k --user jenkins:${JENKINS_API_TOKEN} \
                        -X POST -H 'cache-control: no-cache' \
                        -H 'content-type: application/x-www-form-urlencoded' \
                        --data 'IMAGE_TAG=${IMAGE_TAG}' \
                        'http://13.246.30.89/job/gitops-complete-pipeline/buildWithParameters?token=gitops-token'
                    """
                }
            }
        }
    }
}
