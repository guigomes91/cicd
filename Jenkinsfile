pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: maven
      image: maven:3.9-eclipse-temurin-17
      command: ["cat"]
      tty: true

    - name: kaniko
      image: gcr.io/kaniko-project/executor:latest
      args:
        - --dockerfile=Dockerfile
        - --context=\$(WORKSPACE)
        - --destination=harbor.local/library/cicd-lab-app:\$(BUILD_NUMBER)
        - --skip-tls-verify
      volumeMounts:
        - name: docker-config
          mountPath: /kaniko/.docker

  volumes:
    - name: docker-config
      secret:
        secretName: harbor-cred
"""
    }
  }

  stages {

    stage('Build App') {
      steps {
        container('maven') {
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Build & Push Image') {
      steps {
        container('kaniko') {
          echo "Kaniko build & push"
        }
      }
    }
  }
}