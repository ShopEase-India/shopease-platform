pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
        git 'git'
    }

    environment {
            AWS_CODE_ARTIFACT_REGION = 'ap-south-1'
            ECR_REPOSITORY_REGION = 'ap-south-2'
            CODEARTIFACT_DOMAIN = 'shopease'
            ECR_REPOSITORY = 'shopease/api-gateway'
            CODEARTIFACT_REPOSITORY = 'maven-snapshots'
            IMAGE_NAME = 'api-gateway'
            IMAGE_TAG = "${BUILD_NUMBER}"
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
                                   --region ${AWS_CODE_ARTIFACT_REGION} \
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
       stage('Build DockerImage'){
            steps{
                sh '''
                    docker build \
                    -t ${IMAGE_NAME}:${IMAGE_TAG} \
                    -f backend/api-gateway/Dockerfile .
                '''
            }
       }
       stage('ECR LOGIN'){
            steps{
                    withCredentials([
                        [$class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws-codeartifact'
                    ]]){
                        sh'''
                            aws ecr get-login-password \
                            --region ${ECR_REPOSITORY_REGION} | docker login \
                            --username AWS \
                            --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com
                        '''
                    }
            }
       }
       stage('Tagging Image'){
            steps{
                sh '''
                    docker tag \
                    ${IMAGE_NAME}:{IMAGE_TAG} \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
       }

       stage('Push Docker Image to ECR'){
            steps{
                sh '''
                    docker push \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
       }
    }

    post {
         success {
                    echo "API Gateway artifact published successfully to AWS CodeArtifact and Image was pushed to AWS ECR."
         }

         failure {
                    echo "Pipeline failed."
         }

        always {
            cleanWs()
        }
    }
}