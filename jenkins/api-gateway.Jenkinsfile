pipeline {
    agent any
    parameters {
        choice(
            name: 'SERVICE',
            choices: [
                'api-gateway',
                'product-service',
                'order-service',
                'payment-service',
                'inventory-service'
            ],
            description: 'Select the microservice to build'
        )
    }

    tools {
        jdk 'jdk21'
        maven 'maven3'
        git 'git'
        sonarQubeScanner 'sonar-scanner'
    }

    environment {
            AWS_CODE_ARTIFACT_REGION = 'ap-south-1'
            ECR_REPOSITORY_REGION = 'ap-south-2'
            CODEARTIFACT_DOMAIN = 'shopease'
         // ECR_REPOSITORY = 'shopease/api-gateway'
            CODEARTIFACT_REPOSITORY = 'maven-snapshots'
         // IMAGE_NAME = 'api-gateway'
            IMAGE_TAG = "${BUILD_NUMBER}"
            AWS_ACCOUNT_ID = '137071594277'
            SERVICE_NAME             = "${params.SERVICE}"
            IMAGE_NAME               = "${params.SERVICE}"
            ECR_REPOSITORY           = "shopease/${params.SERVICE}"
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

        stage('Compile') {
            steps {
                sh 'mvn -pl backend/${SERVICE_NAME} -am clean compile'
            }
        }

       stage('Test') {
           steps {
               sh 'mvn -pl backend/${SERVICE_NAME} -am test'
           }
       }

       stage('SonarQube Analysis') {
           steps {
               withSonarQubeEnv('shopease-sonarqube') {
                   sh '''
                   mvn \
                     -pl backend/${SERVICE_NAME} \
                     -am \
                     sonar:sonar \
                     -Dsonar.projectKey=shopease-${SERVICE_NAME} \
                     -Dsonar.projectName="ShopEase API Gateway"
                   '''
               }
           }
       }

       stage('Quality Gate') {
           steps {
               timeout(time: 5, unit: 'MINUTES') {
                   waitForQualityGate abortPipeline: true
               }
           }
       }

       stage('Package') {
           steps {
               sh 'mvn -pl backend/${SERVICE_NAME} -am package -DskipTests'
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
                                 -pl backend/${SERVICE_NAME} \
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
                    -f backend/${SERVICE_NAME}/Dockerfile .
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
                    ${IMAGE_NAME}:${IMAGE_TAG} \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
       }

       stage('Trivy Scan') {
           steps {
               sh '''
               trivy image \
                 --format template \
                 --template "@$Home/trivy/templates/html.tpl" \
                 -o trivy-report.html \
                 ${IMAGE_NAME}:${IMAGE_TAG}
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
                    echo "${SERVICE_NAME} artifact published successfully to CodeArtifact and image pushed to ECR."
         }

         failure {
                    echo "Pipeline failed."
         }

        always {
            archiveArtifacts artifacts: 'trivy-report.html', fingerprint: true
            cleanWs()
        }
    }
}