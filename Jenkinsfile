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
      - cat
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
    REGISTRY = "localhost:30003"
    IMAGE = "cicd-lab/app"
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
              --context=dir://\$(pwd) \
              --dockerfile=Dockerfile \
              --destination=$REGISTRY/$IMAGE:$TAG
          """
        }
      }
    }
  }
}