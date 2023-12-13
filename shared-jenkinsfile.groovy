pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Build applcation
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                // Run tests
                sh 'mvn test'
            }
        }

        stage('Build and push docker image to hub') {
            steps {
                script {
                    // when using docker hub, you dont need to specify the registry url in docker.withregistry
                    docker.withRegistry('', credentials) {
                        //build and push docker image
                        def imageName = params.DOCKER_IMAGE_NAME ?: 'default-image'
                        docker.build("pavle09/${imageName}:latest").push()
                    }
                    // // Login to docker hub
                    // withCredentials([usernamePassword(credentialsId: 'credentials', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]){
                    //     sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    // }

                    // sh 'docker push pavle09/spring-petclinic'
                    
                }
            }
        }
    }
}