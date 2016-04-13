#!/usr/bin/groovy
node{
  ws{

    stage 'checkout code'
        checkout scm

    stage 'build and unit test'
    
        kubernetes.pod('buildtestpod').withImage('maven')
        .withPrivileged(true)
        .withSecret('jenkins-maven-settings','/root/.m2')
        .inside {
            sh 'mvn -Dmaven.test.failure.ignore clean verify'
            step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        }

    stage 'build and install'
        kubernetes.pod('deploypod').withImage('maven')
        .withPrivileged(true)
        .withSecret('jenkins-maven-settings','/root/.m2')
        .inside {
            try {
                sh 'mvn install'
            }
            catch(e) {
                currentBuild.result = 'FAILURE'
                email_recipients = getBinding().getVariable("EMAIL")
                step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: email_recipients, sendToIndividuals: false])
                throw e
            }
        }
        
    stage 'integration test'

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'distortion-gh-test-user-pw',
        usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_PASSWORD']]) {

            withCredentials([[$class: 'StringBinding', 
            credentialsId: 'distortion-gh-test-access-token', variable: 'GITHUB_TOKEN']]) {

                withCredentials([[$class: 'StringBinding', 
                credentialsId: 'distortion-gh-test-client-id', variable: 'KONTINUITY_CATAPULT_GITHUB_APP_CLIENT_ID']]) {

                    withCredentials([[$class: 'StringBinding', 
                    credentialsId: 'distortion-gh-test-client-secret', variable: 'KONTINUITY_CATAPULT_GITHUB_APP_CLIENT_SECRET']]) {
        
                        kubernetes.pod('itpod').withImage('maven')
                        .withPrivileged(true)
                        .withSecret('jenkins-maven-settings','/root/.m2')
                        .inside {
                            try {
                                sh """
                                    export KONTINUITY_CATAPULT_OPENSHIFT_URL=${OPENSHIFT_MASTER_URL}
                                    mvn -Dmaven.test.failure.ignore clean verify -Pit
                                """

                                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

                                sh """
                                    export KONTINUITY_CATAPULT_OPENSHIFT_URL=${OPENSHIFT_MASTER_URL}
                                    mvn clean verify -Pit
                                """                                
                            }
                            catch(e) {
                                currentBuild.result = 'FAILURE'
                                email_recipients = getBinding().getVariable("EMAIL")
                                step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: email_recipients, sendToIndividuals: false])
                                throw e
                            }
                        }
                    }
                }
            
            }
        }
    }
}
