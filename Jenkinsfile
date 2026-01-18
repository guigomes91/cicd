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
      command:
        - cat
      tty: true

    - name: kaniko
      image: gcr.io/kaniko-project/executor:latest
      command:
        - /kaniko/executor
      args:
        - "--help"
      tty: true
      volumeMounts:
        - name: docker-config
          mountPath: /kaniko/.docker

    - name: jnlp
      image: jenkins/inbound-agent:3355.v388858a_47b_33-2-jdk21

  volumes:
    - name: docker-config
      secret:
        secretName: harbor-cred
"""
    }
  }

  environment {
    REGISTRY = "harbor.local/library"
    IMAGE    = "cicd-lab-app"
    TAG      = "${BUILD_NUMBER}"
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
          sh '''
            /kaniko/executor \
              --context=$WORKSPACE \
              --dockerfile=$WORKSPACE/Dockerfile \
              --destination=${REGISTRY}/${IMAGE}:${TAG} \
              --skip-tls-verify
          '''
        }
      }
    }
  }
}