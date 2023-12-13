pipeline {
    agent any

    parameters {
        string(name: 'PROJECT_URL', defaultValue: 'https://github.com/pavle0903', description: 'Git repository URL')
        string(name: 'DOCKER_IMAGE_NAME', defaultValue: 'default-image-name', description: 'docker image name')
    }

    stages {

        stage('Checkout') {
            steps {
                // Checkout the Maven project from the Git repository
                git credentialsId: 'github_credentials', url: params.PROJECT_URL
            }
        }

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
                    docker.withRegistry('', 'credentials') {
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