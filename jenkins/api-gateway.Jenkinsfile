@Library('shopease-shared-library') _
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
        //"sonarQube Scanner" 'sonar-scanner'
        //sonarRunner 'sonar-scanner'
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
            ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com"
            ECR_REPOSITORY  = "shopease/${params.SERVICE}"
            NAMESPACE = 'shopease'
            CLUSTER_NAME = 'shopease-dev'
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
                /* compileService(params.SERVICE) */
                mavenBuild(service: params.SERVICE,
                           goal: "compile")
            }
        }

      /*  stage('Test') {
           steps {
               sh 'mvn -pl backend/${SERVICE_NAME} -am test'
           }
       } */
           stage('Test'){
               steps{
                /* testService(params.SERVICE) */
                mavenBuild(service: params.SERVICE,
                            goal: "test")
               }
           }
       /* stage('Verify') {
           steps {
               sh 'mvn -pl backend/${SERVICE_NAME} -am verify'
           }
       } */

       /* stage('SonarQube Analysis') {
           steps {
               withSonarQubeEnv('shopease-sonarqube') {
                   sh '''
                   mvn \
                     -pl backend/${SERVICE_NAME} \
                     -am \
                     verify \
                     sonar:sonar \
                     -Dsonar.projectKey=shopease-${SERVICE_NAME} \
                     -Dsonar.projectName=shopease-${SERVICE_NAME}
                   '''
               }
           }
       } */

       stage('Sonarqube Analysis'){
           steps{
               sonarAnalysis(service: params.SERVICE)
           }
       }

       /* stage('Quality Gate') {
           steps {
               timeout(time: 5, unit: 'MINUTES') {
                   waitForQualityGate abortPipeline: true
               }
           }
       } */
       stage('Quality Gate'){
           steps{
               qualityGateService()
           }
       }

       stage('Package') {
           steps {
               mavenBuild(service: params.SERVICE,
                          goal: "package",
                          options: "-DskipTests")
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

       /* stage('Build Image'){
            steps{
                dockerBuild(
                    image: "${IMAGE_NAME}",
                    tag: "${IMAGE_TAG}",
                    dockerfile: "backend/${params.SERVICE}/Dockerfile"
                )
            }
       } */
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
       /* stage('Tagging Image'){
            steps{
                 *//* sh '''
                    docker tag \
                    ${IMAGE_NAME}:${IMAGE_TAG} \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}
                ''' *//*
                dockerTag(image:"${IMAGE_NAME}",tag: "${IMAGE_TAG}",registry: "${ECR_REGISTRY}",
                           repository: "${ECR_REPOSITORY}")
            }
       }

       stage('Trivy Scan') {
           steps {
               *//*  sh '''
               trivy image \
                 --format template \
                 --template "@$Home/trivy/templates/html.tpl" \
                 -o trivy-report.html \
                 ${IMAGE_NAME}:${IMAGE_TAG}
               ''' *//*
               trivyScan( image: "${IMAGE_NAME}", tag: "${IMAGE_TAG}")
           }
       }

        *//* stage('Push Docker Image to ECR'){
            steps{
                sh '''
                    docker push \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${ECR_REPOSITORY_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
       } *//*
       stage('Push Docker Image to ECR'){
                   steps{
                       dockerPush(registry: "${ECR_REGISTRY}",
                                  repository: "${ECR_REPOSITORY}",
                                  tag: "${IMAGE_TAG}")
                   }
              } */
       stage('containerPipeline'){
            steps{
                containerPipeline(image: "${IMAGE_NAME}",
                                  tag: "${IMAGE_TAG}",
                                  dockerfile: "backend/${params.SERVICE}/Dockerfile",
                                  registry: "${ECR_REGISTRY}",
                                  repository: "${ECR_REPOSITORY}")
            }
       }
       stage('Checkout Kubernetes Repo') {
           steps {
               dir('shopease-kubernetes') {
                   git(
                       branch: 'main',
                       credentialsId: 'github-k8s-readonlyx',
                       url: 'git@github.com:ShopEase-India/shopease-kubernetes.git'
                   )
               }
           }
       }
       stage('Deploy') {
            steps {
              deployService(
                serviceName: params.SERVICE,
                clusterName: "${CLUSTER_NAME}",
                region: "${ECR_REPOSITORY_REGION}",
                namespace: "${NAMESPACE}",
                image: "${ECR_REGISTRY}/${ECR_REPOSITORY}",
                tags: "${IMAGE_TAG}"
              )
           }
       }
       /* stage('Health Check') {
           steps {
               script {
                   try {
                       healthCheck(
                            serviceName: params.SERVICE,
                            namespace: "${NAMESPACE}"
                       )
                   } catch (Exception e) {
                       echo "Health check failed. Rolling back..."

                       rolloutUndo(
                           serviceName: params.SERVICE,
                           namespace: "${NAMESPACE}"
                       )

                       throw e
                   }
               }
           }
       } */
    }

    post {
         success {
                    echo "${SERVICE_NAME} artifact published successfully to CodeArtifact and image pushed to ECR."
         }

         failure {
                    echo "Pipeline failed."
         }

        always {
            /* archiveArtifacts artifacts: 'backend *//**//* target/site/jacoco *//**', fingerprint: true */
            archiveArtifacts artifacts: 'trivy-report.html', fingerprint: true
            cleanWs()
        }
    }
}