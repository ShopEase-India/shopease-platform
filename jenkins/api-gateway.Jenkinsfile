pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
        git 'git'
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

       stage('Publish to Nexus') {
           steps {
               configFileProvider([configFile(fileId: 'nexus-maven-settings', variable: 'MAVEN_SETTINGS')]) {
                   withCredentials([usernamePassword(
                       credentialsId: '06feaab8-4752-4b78-9ec9-b7ecc7f9121e',
                       usernameVariable: 'NEXUS_USERNAME',
                       passwordVariable: 'NEXUS_PASSWORD'
                   )]) {
                       sh '''
                           mvn -pl backend/api-gateway -am \
                           deploy \
                           -DskipTests \
                           --settings $MAVEN_SETTINGS
                       '''
                   }
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