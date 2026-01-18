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
        mountPath: /kaniko/.docker

  volumes:
    - name: docker-config
      secret:
        secretName: harbor-cred
"""
    }
  }

  environment {
    REGISTRY = "harbor-registry.cicd.svc.cluster.local:5000/cicd"
    IMAGE    = "cicd-lab-app"
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
              --destination=${REGISTRY}/${IMAGE}:${TAG} \
			  --insecure \
              --skip-tls-verify
          '''
        }
      }
    }
  }
}