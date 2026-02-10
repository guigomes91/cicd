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
        - name: git-ssh
          mountPath: /root/.ssh
          readOnly: true

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

  volumes:
    - name: maven-cache
      emptyDir: {}

    - name: git-ssh
      secret:
        secretName: jenkins-gitops-ssh
        defaultMode: 0400
"""
    }
  }

  environment {
    REGISTRY_CI = "registry:5000"
    REGISTRY_CD = "localhost:5000"
    PROJECT    = "cicd"
    IMAGE_NAME = "cicd-api"
    TAG        = "${BUILD_NUMBER}"
    MAVEN_OPTS = "-Xms256m -Xmx512m"
  }

  stages {

    stage('Build App') {
      steps {
        container('maven') {
          sh '''
            mvn clean package -DskipTests
            ls -lh target
          '''
        }
      }
    }

    stage('Build & Push Image') {
      steps {
        container('kaniko') {
          sh '''
/kaniko/executor \
  --context=${WORKSPACE} \
  --dockerfile=${WORKSPACE}/Dockerfile \
  --destination=${REGISTRY_CI}/${PROJECT}/${IMAGE_NAME}:${TAG} \
  --insecure \
  --skip-tls-verify \
  --cleanup
'''
        }
      }
    }

    stage('Update GitOps Repo') {
      steps {
        container('maven') {
          sh '''
            export GIT_SSH_COMMAND="ssh -i /root/.ssh/id_ed25519 -o StrictHostKeyChecking=yes"

            git clone git@github.com:guigomes91/gitops-repo.git
            cd gitops-repo/cicd-api/app

            sed -i "s|image: .*|image: ${REGISTRY_CD}/${PROJECT}/${IMAGE_NAME}:${TAG}|" deployment.yaml

            git config user.email "guilherme.gomes91@outlook.com"
            git config user.name "guigomes91"

            git diff --quiet || git commit -am "chore(gitops): update image ${TAG}"
            git push origin master
          '''
        }
      }
    }
  }
}