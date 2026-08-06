pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile API Gateway') {
            steps {
                dir('backend/api-gateway') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test API Gateway') {
            steps {
                dir('backend/api-gateway') {
                    sh 'mvn test'
                }
            }
        }

        stage('Package API Gateway') {
            steps {
                dir('backend/api-gateway') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}