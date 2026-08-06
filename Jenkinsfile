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

        stage('Trivy FileSystem Scan'){
            steps{
                sh ```
                    mkdir -p trivy-reports
                    trivy fs . \
                    --scanners vuln \
                    --severity HIGH,CRITICAL \
                    --format json \
                    --output trivy-reports/trivy-fs.json
                ```
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

            archiveArtifacts artifacts: 'trivy-reports/*.json',
                             fingerprint: true
                             
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