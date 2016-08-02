#!groovy

node {
  stage 'SCM Checkout'
    checkout scm

  stage 'Build and Test'
    def mvnHome = tool 'M3'
    sh "${mvnHome}/bin/mvn -B clean verify"

  stage 'Archive'
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.zip, **/target/*.tar.gz', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
