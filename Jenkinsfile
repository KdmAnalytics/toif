stage 'SCM Checkout'
node {
  checkout scm
}
stage 'Build'
node {
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B clean package"
}
stage 'Test'
node {
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B verify"
}
