# CI/CD Lab

## O que esse lab faz:

✅ Kind (cluster local)
✅ Jenkins rodando no Kubernetes
✅ Harbor como registry
✅ Spinnaker instalado
✅ Aplicação com Dockerfile
✅ CI configurado no Jenkins usando Kaniko

Orquestra o fluxo completo CI → CD e simula execuções reais.

## Estrutura:

GitHub
  ↓ (push)
Jenkins (CI)
  - build
  - teste
  - build image (Kaniko)
  - push Harbor
  ↓
Spinnaker (CD)
  - deploy no Kubernetes
  - versionamento
  - rollback
