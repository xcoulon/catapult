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

    stage 'build and deploy'
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
  }
}
