pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
        git 'git'
    }

    environment {
            AWS_REGION = 'ap-south-1'
            CODEARTIFACT_DOMAIN = 'shopease'
            CODEARTIFACT_REPOSITORY = 'maven-snapshots'
            AWS_ACCOUNT_ID = '137071594277'
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
                sh 'mvn -pl backend/api-gateway -am clean compile'
            }
        }

       stage('Test API Gateway') {
           steps {
               sh 'mvn -pl backend/api-gateway -am test'
           }
       }

       stage('Package API Gateway') {
           steps {
               sh 'mvn -pl backend/api-gateway -am package -DskipTests'
           }
       }

       stage('Publish to AWS CodeArtifact') {
                   steps {
                       configFileProvider([
                           configFile(
                               fileId: 'codeartifact-maven-settings',
                               variable: 'MAVEN_SETTINGS'
                           )
                       ]) {

                           withCredentials([
                               [$class: 'AmazonWebServicesCredentialsBinding',
                               credentialsId: 'aws-codeartifact']
                           ]) {

                               sh '''
                               export CODEARTIFACT_AUTH_TOKEN=$(aws codeartifact get-authorization-token \
                                   --domain ${CODEARTIFACT_DOMAIN} \
                                   --domain-owner ${AWS_ACCOUNT_ID} \
                                   --region ${AWS_REGION} \
                                   --query authorizationToken \
                                   --output text)

                               mvn \
                                 -pl backend/api-gateway \
                                 -am \
                                 deploy \
                                 -DskipTests \
                                 --settings ${MAVEN_SETTINGS}
                               '''
                           }
                       }
                   }
       }
    }

    post {
         success {
                    echo "API Gateway published successfully to AWS CodeArtifact."
         }

         failure {
                    echo "Pipeline failed."
         }

        always {
            cleanWs()
        }
    }
}