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
      - /kaniko/executor
    args:
      - --dockerfile=Dockerfile
      - --context=\$(WORKSPACE)
      - --destination=harbor.local/library/cicd-lab-app:\$(BUILD_NUMBER)
      - --skip-tls-verify
    volumeMounts:
      - name: docker-config
        mountPath: /kaniko/.docker
  restartPolicy: Never
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
        sh 'mvn clean package -DskipTests'
      }
    }
  }
}