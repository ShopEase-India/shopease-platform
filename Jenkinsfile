pipeline{
    agent any
    tools{
        jdk 'jdk21'
        maven 'maven3'
    }
    options{
        skipDefaultCheckout(true)
        timestamps()
         buildDiscarder(logRotator(
                numToKeepStr: '10',
                artifactNumToKeepStr: '5'
            ))
    }
    stages{
        stage('Checkout'){
            steps{
                checkout scm
            }
        }

        stage('Compile'){
            steps{
                sh 'mvn clean compile'
            }
        }

        stage('Test'){
            steps{
                sh 'mvn test'
            }
        }

        stage('Package'){
            steps{
                sh 'mvn package -DskipTests'
            }
        }
    }
    post{
        always{
            junit '**/target/surefire-reports/*.xml'
            cleanWs()
        }

        success{
            echo 'Build Successful'
        }
        
        failure{
            echo 'Build Failed'
        }
    }
}