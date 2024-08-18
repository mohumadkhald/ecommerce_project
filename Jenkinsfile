pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS_ID = 'mohumadkhald'  // Replace with your Docker Hub credentials ID
        //AWS_CREDENTIALS_ID = 'aws-credentials-id' // If pushing to ECR
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Maven Project') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        def image = docker.build("mohumadkhald/ecommerce:${env.BUILD_ID}")
                        image.push()
                    }
                }
            }
        }
    }
}
