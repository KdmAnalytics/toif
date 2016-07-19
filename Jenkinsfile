#!groovy

stage 'SCM Checkout'
node {
  checkout scm
}
stage 'Build and Test'
node {
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B clean package"
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.zip, **/target/*.tar.gz', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
