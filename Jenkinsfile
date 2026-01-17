pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
    command:
      - /busybox/sh
    tty: true
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

  environment {
    REGISTRY = "harbor.local"
    IMAGE = "library/cicd-lab-app"
    TAG = "${BUILD_NUMBER}"
  }

  stages {
    stage('Build App') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Build & Push Image') {
      steps {
        container('kaniko') {
          sh """
            /kaniko/executor \
              --context \$WORKSPACE \
              --dockerfile \$WORKSPACE/Dockerfile \
              --destination ${REGISTRY}/${IMAGE}:${TAG} \
              --skip-tls-verify
          """
        }
      }
    }
  }
}