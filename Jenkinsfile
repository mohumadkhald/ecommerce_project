pipeline
{
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        APP_NAME = "ecommerce_ci_cd"
        RELEASE = "1.0.0"
        DOCKER_USER = "mohumadkhald"
        DOCKER_PASS = 'Dockerhub'
        IMAGE_NAME = "${DOCKER_USER}" + "/" + "${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
        JENKINS_API_TOKEN = credentials("JENKINS_API_TOKEN")

    }

    stages {
          stage("Cleanup Workspace"){
              steps {
                  cleanWs()
              }
          }

          stage("Checkout from SCM"){
              steps {
                  git branch: 'main', credentialsId: 'Github', url: 'https://github.com/mohumadkhald/e2e-CI_CD'
              }
          }

          stage('build app') {
              steps {
                  script {
                      echo "building the application..."
                      sh 'mvn clean package'
                  }
              }
          }

        stage('build image') {
            steps {
                script {
                    echo "Building the Docker image..."
                    withCredentials([usernamePassword(credentialsId: 'Dockerhub', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        // Build the image
                        sh "docker build -t mohumadkhald/${IMAGE_NAME}:${IMAGE_TAG} ."

                        // Tag the image as 'latest'
                        sh "docker tag mohumadkhald/${IMAGE_NAME}:${IMAGE_TAG} mohumadkhald/${IMAGE_NAME}:latest"

                        // Log in to Docker Hub
                        sh "echo $PASS | docker login -u $USER --password-stdin"

                        // Push the 'latest' image
                        sh "docker push mohumadkhald/${IMAGE_NAME}:latest"

                        // Push the versioned image
                        sh "docker push mohumadkhald/${IMAGE_NAME}:${IMAGE_TAG}"

                    }
                }
            }
        }

        stage('clean image') {
            steps {
                script {

                        // Clean up local images and dangling (<none>:<none>) images
                        echo "Cleaning up Docker images..."
                        sh """
                            docker rmi -f mohumadkhald/${IMAGE_NAME}:${IMAGE_TAG} || true
                            docker rmi -f mohumadkhald/${IMAGE_NAME}:latest || true
                            docker image prune -f --filter dangling=true || true
                            docker logout
                        """
                    }
                }
            }
        }



        stage("Trigger CD Pipeline") {
            steps {
                script {
                    sh "curl -v -k --user jenkins:${JENKINS_API_TOKEN} -X POST -H 'cache-control: no-cache' -H 'content-type: application/x-www-form-urlencoded' --data 'IMAGE_TAG=${IMAGE_TAG}' 'http://http://13.246.30.89//job/gitops-complete-pipeline/buildWithParameters?token=gitops-token'"
                }
            }

        }

    }
}
