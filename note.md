# Microservices with Spring Boot 3 and Spring Cloud, Third Edition

This is the code repository
for [Microservices with Spring Boot 3 and Spring Cloud, Third Edition](https://www.amazon.com/Microservices-Spring-Boot-Cloud-microservices/dp/1805128698),
published by Packt.

## 快速运行：

在 docker 中运行：

```bash
cd Chapter04
gradle build -x test && sh test-em-all.bash start stop
```

在 k8s 中运行：

```bash

cd Chapter16
gradle build -x test
eval $(minikube docker-env)
docker-compose build

unset KUBECONFIG

minikube start \
 --profile=handson-spring-boot-cloud \
 --memory=10240 \
 --cpus=4 \
 --disk-size=30g \
 --kubernetes-version=v1.26.1 \
 --driver=docker \
 --ports=8080:80 --ports=8443:443 \
 --ports=30080:30080 --ports=30443:30443

minikube profile handson-spring-boot-cloud
minikube addons enable ingress
minikube addons enable metrics-server

kubectl config set-context $(kubectl config current-context) --namespace=hands-on

for f in kubernetes/helm/components/*; do helm dep up $f; done
for f in kubernetes/helm/environments/*; do helm dep up $f; done
helm dep ls kubernetes/helm/environments/dev-env/

helm install hands-on-dev-env kubernetes/helm/environments/dev-env -n hands-on --create-namespace

kubectl wait --timeout=600s --for=condition=ready pod --all
kubectl get pods -o json | jq .items[].spec.containers[].image

PORT=30443 USE_K8S=true ./test-em-all.bash


ACCESS_TOKEN=$(curl -d grant_type=client_credentials \
 -ks https://writer:secret-writer@localhost:30443/oauth2/token \
 -d scope="product:read product:write" \
 | jq .access_token -r)
 
time curl -kH "Authorization: Bearer $ACCESS_TOKEN" \
 https://localhost:30443/product-composite/1?delay=5

siege -c5 -d2 -v -H "Authorization: Bearer $ACCESS_TOKEN" \
 https://localhost:30443/product-composite/1?delay=5
   

kubectl exec -it deploy/product -- curl localhost/actuator/health/liveness -s | jq .
kubectl exec -it deploy/product -- curl localhost/actuator/health/readiness -s | jq .
   
helm rollback hands-on-dev-env -n hands-on --wait   
minikube delete --profile handson-spring-boot-cloud
```

## Changelog

当前分支做了以下改动：

- 升级 JDK 到 21，docker 基础镜像使用 eclipse-temurin:21.0.1_12-jre-jammy（内置 curl）
- 升级 Spring Boot 到 3.3.0
- 修改 config-server 代码，方便在 IDE 中本地运行

```yaml
# 兼容 PWD 变量未定义的情况
spring.cloud.config.server.native.searchLocations: file:${PWD}/config-repo

# 以下为新增配置
spring.profiles.active: native

encrypt.key: ${CONFIG_SERVER_ENCRYPT_KEY:my-very-secure-encrypt-key}
spring.security.user.name: ${CONFIG_SERVER_USR:dev-usr}
spring.security.user.password: ${CONFIG_SERVER_PWD:dev-pwd} 
```

- 各个微服务连接 config-server 时，配置默认用户名和密码

```yaml
spring:
  cloud.config:
    uri: http://localhost:8888
    username: ${CONFIG_SERVER_USR:dev-usr}
    password: ${CONFIG_SERVER_PWD:dev-pwd}
```

- 修改 docker-compose 中 config-server 服务的逻辑卷

```yaml
  config-server:
    build: spring-cloud/config-server
    volumes:
      - ./config-repo:/config-repo
```

- 删除 openapi 部分配置

## Other Related Books

- [Learning Spring Boot 3.0 - Third Edition](https://www.packtpub.com/product/learning-spring-boot-30-third-edition/9781803233307)
- [Full Stack Development with Spring Boot 3 and React](https://www.packtpub.com/product/full-stack-development-with-spring-boot-and-react-third-edition/9781801816786)
