pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Building..'
	sh 'mvn -f pom.xml clean install'
	}
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
