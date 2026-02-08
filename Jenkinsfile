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
      volumeMounts:
        - name: maven-cache
          mountPath: /root/.m2

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
    - name: maven-cache
      emptyDir: {}
"""
    }
  }

  environment {
    REGISTRY = "harbor.cicd.svc.cluster.local"
    PROJECT  = "cicd"
    IMAGE_NAME = "cicd-api"
    TAG = "${BUILD_NUMBER}"
    MAVEN_OPTS = "-Xms256m -Xmx512m"
  }

  stages {

    stage('Build App') {
      when {
        branch 'master'
      }
      steps {
        container('maven') {
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Build & Push Image') {
      when {
        branch 'master'
      }
      steps {
        container('kaniko') {
          sh """
/kaniko/executor \
  --context=${WORKSPACE} \
  --dockerfile=${WORKSPACE}/Dockerfile \
  --destination=${REGISTRY}/${PROJECT}/${IMAGE_NAME}:${TAG} \
  --insecure \
  --cleanup
"""
        }
      }
    }

    stage('Update GitOps Repo') {
      when {
        branch 'master'
      }
      steps {
        sh """
          git clone git@github.com:guigomes91/gitops-repo.git
          cd gitops-repo/cicd-api/app
          sed -i 's|image: .*|image: ${REGISTRY}/${PROJECT}/${IMAGE_NAME}:${TAG}|' deployment.yaml
          git config user.email "guilherme.gomes91@outlook.com"
          git config user.name "guigomes91"
          git commit -am "chore: update image ${TAG}"
          git push origin master
        """
      }
    }
  }
}