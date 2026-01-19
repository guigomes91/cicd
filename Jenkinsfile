pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
  namespace: cicd
spec:

  containers:
    - name: maven
      image: maven:3.9-eclipse-temurin-17
      command: ["cat"]
      tty: true
      resources:
        requests:
          cpu: "500m"
          memory: "512Mi"
        limits:
          cpu: "1000m"
          memory: "1Gi"

    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      command: ["cat"]
      tty: true
      resources:
        requests:
          cpu: "300m"
          memory: "512Mi"
        limits:
          cpu: "800m"
          memory: "1Gi"
      volumeMounts:
        - name: docker-config
          mountPath: /kaniko/.docker/config.json
          subPath: config.json

  volumes:
    - name: docker-config
      secret:
        secretName: kaniko-docker-config
"""
        }
    }

    environment {
        REGISTRY = "harbor.cicd.svc.cluster.local"
        IMAGE_NAME = "cicd-api"
        PROJECT  = "cicd"
        TAG      = "${BUILD_NUMBER}"
        MAVEN_OPTS = "-Xms256m -Xmx512m"
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
  --destination=${REGISTRY}/${PROJECT}/${IMAGE_NAME}:${TAG} \
  --insecure
'''
                }
            }
        }
    }
}