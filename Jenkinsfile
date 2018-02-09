#!/usr/bin/env groovy
pipeline {
    agent none
    stages {
        stage('Pull Code') {
            agent any
            steps {
                echo 'Pulling the code'
                checkout scm
                sh 'docker pull jachno/jarrunner:latest'
            }
        }
            stage('App Config') {
              agent any
              steps {
                  echo 'Configuring the application with name and version'
                  script {
                      readConfigValues()
                      checkVersion()
                  }
              }
          }

        stage('Unit Tests') {
            agent {
                docker {
                    image '669711333016.dkr.ecr.eu-west-1.amazonaws.com/sdg-docker-gradle'
                    args '-v /$(pwd):/usr/bin/app:rw'
                }
            }
            steps {
                echo 'Building after checkout'
                sh 'gradle test'
            }
        }
        stage('Code Quality') {
            agent {
                docker {
                    image '669711333016.dkr.ecr.eu-west-1.amazonaws.com/sdg-docker-gradle'
                    args '-v /$(pwd):/usr/bin/app:rw'
                }
            }
            when {
                branch 'master'
            }
            steps {
                echo 'Running code quality'
                sh 'gradle sonarqube'
            }
        }

        stage('Build') {
            agent {
                docker {
                    image '669711333016.dkr.ecr.eu-west-1.amazonaws.com/sdg-docker-gradle'
                    args '-v /$(pwd):/usr/bin/app:rw'
                }
            }
            steps {
                echo 'Running a Clean first'
                sh 'gradle clean'
                echo 'Building after checkout'
                sh 'gradle build'
            }
        }

        stage('Acceptance Test') {
            agent {
                docker {
                    image 'jachno/jarrunner:latest'
                    args '-v /$(pwd):/usr/bin/app:rw'
                }
            }
            steps {
                echo 'Running up the Jar and acceptance tests'
                script {
                    sh "/usr/bin/runApp.sh build/libs/${servicePackage} AcceptanceTest"
                }
                // -u sets the user context for the docker container
            }
            post {
                always {
                    // publish html
                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : false,
                            reportDir            : 'build/reports/acceptanceTest',
                            reportFiles          : 'index.html',
                            reportName           : 'AcceptanceTestReport'
                    ]
                }
            }
        }

        stage('Load Test') {
            agent {
                docker {
                    image 'jachno/jarrunner:latest'
                    args '-v /$(pwd):/usr/bin/app:rw'
                }
            }
            steps {
                echo 'Running up the Jar and Load_test'
                script {
                    sh "/usr/bin/runApp.sh build/libs/${servicePackage} loadTest"
                }
                // -u sets the user context for the docker container
            }
            post {
                always {

                    sh 'mv build/reports/loadtest/onboarding-example* build/reports/loadtest/onboarding-example-loadtest'
                    // publish html
                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : false,
                            reportDir            : 'build/reports/loadtest/onboarding-example-loadtest',
                            reportFiles          : 'index.html',
                            reportName           : 'Load_Test_Report'
                    ]
                }
            }
        }

     stage('Bake and Push') {
            agent any
            when {
                branch 'master'
            }
            steps {
                echo 'bake and push to ECR stage'
                dir('sources') { // switch to subdir
                    git url: "git@bitbucket.org:ShopDirect/docker-jar-deployer.git",  credentialsId: 'microservice'
                        }
                sh 'ls -lrt'
                sh 'pwd'
                sh 'ls deploy_utils/'
                sh "chmod +x deploy_utils/bakeAndPush.sh"
                sh "echo ${serviceName} ${servicePackage} ${serviceVersion} $pwd"
                sh "deploy_utils/bakeAndPush.sh ${serviceName} ${servicePackage} ${serviceVersion} ${teamAccountNumber}"
            }
        }


    stage('Deploy to DEV') {
           agent any
           when {
               branch 'master'
           }
           steps {
                deployApplication("DEV", DEVRoleToAssumeARN, DEVExternalId)
           }
    }

    stage('Post Deploy : Version Update'){
                agent any
                when {
                     branch 'master'
                }
                steps {
        		    sh "chmod +x ./deploy_utils/git_tag.sh"
                    sshagent (credentials: ['microservice']) {
                        sh "./deploy_utils/git_tag.sh ${serviceVersion}"
                    }
                    sh "printf ${serviceVersion} > ${serviceName}"
                    sh "aws s3 cp ${serviceName} s3://service.${teamAccountNumber}.sd.com/version/"
                }
          }
    }

    post {
        success {
            echo 'Pipeline Passed'

            bitbucketStatusNotify(
                          buildState: 'SUCCESSFUL',
                          buildKey: 'build',
                          buildName: 'Build'
                            )
                }
        failure {
          echo 'Pipeline Failed'

          bitbucketStatusNotify(
                        buildState: 'FAILED',
                        buildKey: 'build',
                        buildName: 'Build'
                          )
                }
        }

}

def readConfigValues() {

    readServiceConfigValues()
    readAWSConfigValues()
}

def readServiceConfigValues() {
    service_config_file = "src/main/resources/service-config"
    serviceVersion = sh(script: "grep ServiceVersion ${service_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    serviceName = sh(script: "grep ServiceName  ${service_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    servicePackage = serviceName + '-' + serviceVersion + '.jar'
    sharedAccountNumber = sh(script: "grep SharedAccountNumber ${service_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    teamAccountNumber = sh(script: "grep TeamAccountNumber ${service_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    sharedDockerRepository = sharedAccountNumber + '.dkr.ecr.eu-west-1.amazonaws.com/sdg-docker-gradle'
    teamDockerRepository = teamAccountNumber + '.dkr.ecr.eu-west-1.amazonaws.com'
    numberOfEcsTasks = sh(script: "grep NumberOfEcsTasks  ${service_config_file} | cut -d'=' -f2", returnStdout: true).trim()
}

def readAWSConfigValues() {
    application_config_file = "src/main/resources/application-config"
    DEVRoleToAssumeARN = sh(script: "grep DEVRoleToAssumeARN  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    UATRoleToAssumeARN = sh(script: "grep UATRoleToAssumeARN  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    NFTRoleToAssumeARN = sh(script: "grep NFTRoleToAssumeARN  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    PRDRoleToAssumeARN = sh(script: "grep PRDRoleToAssumeARN  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    DEVExternalId = sh(script: "grep DEVExternalId  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    UATExternalId = sh(script: "grep UATExternalId  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    NFTExternalId = sh(script: "grep NFTExternalId  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
    PRDExternalId = sh(script: "grep PRDExternalId  ${application_config_file} | cut -d'=' -f2", returnStdout: true).trim()
}

def checkVersion() {

    sh "aws ecr get-login --registry-id ${sharedAccountNumber} --no-include-email --region eu-west-1 | sh"
    sh "chmod +x deploy_utils/checkVersion.sh"
    sh "deploy_utils/checkVersion.sh ${serviceName} ${serviceVersion} ${teamAccountNumber}"
}

def deployApplication(environmentName, assumeRoleARN, roleExternalId) {

    echo "deploying to " + environmentName + " AWS Cluster $servicePackage"
    sh "chmod +x deploy_utils/deployToAwsCluster.sh"
    sh "chmod +x deploy_utils/postDeployVersionCheck.sh"
    sh "deploy_utils/deployToAwsCluster.sh ${serviceName} ${numberOfEcsTasks} ${teamDockerRepository}/${serviceName}:${serviceVersion} " + assumeRoleARN + " " + roleExternalId + " " + environmentName
    sh "deploy_utils/postDeployVersionCheck.sh ${serviceName} ${serviceVersion} " + assumeRoleARN + " " + roleExternalId
}