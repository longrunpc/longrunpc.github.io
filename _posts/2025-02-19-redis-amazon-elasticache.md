---
title: "[AWS] Amazon ElastiCache"
categories:
- AWS
descriptions: AWS에서 지원하는 ElastiCache에 대해서 포스팅한 글입니다.
"\bpin": true
math: true
mermaid: true
tags:
- AWS
- redis
---

---
# 💡 Amazon ElastiCache 란?!



<img src="assets/img/docs/elasticache.jpeg" alt="aws  이미지" style="float: left; margin-right: 500px;">

**Amazon ElastiCache**는 **Valkey, Memcached 및 Redis OSS와 호환되는 완전관리형 서비스**로,  
클라우드에서 **분산 인메모리 데이터 스토어 또는 캐시 환경을 쉽게 설정, 관리 및 확장**할 수 있는 AWS 웹 서비스입니다.

Amazon ElastiCache에서는 두 가지 운영 방식을 제공합니다:

1. **Serverless 캐시** (AWS가 자동으로 관리)
2. **자체 ElastiCache 클러스터** (사용자가 직접 설정 및 운영)

---

## Serverless 캐시

<img src="assets/img/docs/elasticache1.png" alt="Serverless 이미지" style="float: left; margin-right: 400px;">
-  **AWS가 자동으로 캐시 인프라를 프로비저닝, 확장, 관리**  
-  사용자가 직접 클러스터를 설정할 필요 없음  
-  트래픽 변화에 따라 **자동 확장(Auto Scaling)**  
-  운영 부담 없이 빠르게 사용할 수 있는 방식  

📌 **즉, AWS가 모든 것을 관리하는 서버리스 환경에서 캐시를 운영할 수 있음!**  

---

## 자체 ElastiCache 클러스터

<img src="assets/img/docs/elasticache2.png" alt="elasticache cluster 이미지" style="float: left; margin-right: 400px;">
-  사용자가 직접 **클러스터를 생성하고 운영**하는 방식  
-  **노드 개수, Sharding, Replication 등 세부 설정 가능**  
-  **Redis Sentinel, Redis Cluster 지원 가능**  
-  직접 **트랜잭션 관리 & 데이터 영속성(RDB, AOF) 설정 가능**  

📌 **즉, 더 많은 제어가 필요할 경우 자체 클러스터 사용!**  





---
---

#  Amazon ElastiCache에서 지원하는 캐시 엔진

## **Memcached**
- **단순한 Key-Value 캐싱 엔진**  
- **빠르고 가벼운 인메모리 캐시 시스템**  
- **단순 Key-Value 저장만 가능**  
- **노드 추가 시 자동으로 Sharding 지원** (수평 확장 가능)  
- **트랜잭션 및 영속성(RDB, AOF) 미지원** → **휘발성 캐시 전용**  

📌 **빠르고 단순한 캐싱이 필요할 때 Memcached를 사용합니다.**  

---

## **Redis OSS (Open Source Standard)**
- **Redis의 오픈소스 버전**  
- **다양한 데이터 구조 지원** (String, List, Hash, Set, Sorted Set, Pub/Sub 등)  
- **트랜잭션 및 복제 지원** (`MULTI`, `EXEC`, `WATCH` 등)  
- **RDB(Snapshot), AOF(Append Only File) 설정 가능 → 데이터 영속성 지원**  
- **고가용성(HA) 및 확장성 기능 제공**  
   - **Redis Cluster 지원** → 자동 Sharding 가능  
   - **Redis Sentinel 지원** → 자동 장애 복구 가능  

📌 **다양한 데이터 저장 & 트랜잭션 기능이 필요할 때 Redis OSS를 사용합니다.**  

---

## **Valkey**
- **AWS가 새롭게 제공하는 Redis 호환 오픈소스 엔진**  
- **기존 Redis 애플리케이션과 완벽히 호환 가능**  
- **멀티스레드 아키텍처 도입**  
   - Redis는 명령어 실행이 기본적으로 싱글스레드지만,  
     **Valkey는 명령어 처리도 멀티스레드로 동작하여 성능 향상**  
- **자동 클러스터 장애 조치 및 확장 기능 향상**  
- **완전한 오픈소스(BSD 라이선스 유지) → 기업에서 자유롭게 사용 가능**  

📌 **Redis를 대체할 오픈소스 대안으로, 성능 최적화와 라이선스 문제 해결이 필요할 때 Valkey가 사용됩니다. **  
📌 **Redis 라이센스 변경 등으로 최근에 급격하게 Redis를 대체하고 있습니다. **  

---

# Valkey vs Redis OSS vs Memcached 비교

| **비교 항목** | **Valkey** | **Redis OSS** | **Memcached** |
|-------------|------------|--------------|--------------|
| **라이선스** | BSD (완전 오픈소스) | SSPL (라이선스 변경됨) | BSD (완전 오픈소스) |
| **데이터 구조** | String, List, Hash, Set 등 | String, List, Hash, Set 등 | 단순 Key-Value |
| **멀티스레드 지원** | ✅ (명령어 실행 포함) | ❌ (기본 싱글스레드, 일부 I/O 멀티스레드) | ✅ (기본 지원) |
| **트랜잭션 지원** | ✅ (`MULTI`, `EXEC`) | ✅ (`MULTI`, `EXEC`) | ❌ 미지원 |
| **Sharding (샤딩)** | ✅ 자동 Sharding | ✅ Redis Cluster 지원 | ✅ 기본 내장 |
| **복제 및 장애 조치** | ✅ 자동 복제 및 장애 조치 | ✅ Redis Sentinel, Redis Cluster 지원 | ❌ 미지원 |
| **데이터 영속성** | ✅ RDB, AOF 지원 | ✅ RDB, AOF 지원 | ❌ 미지원 (휘발성 캐시) |
| **성능 최적화** | ✅ 멀티스레드 최적화 | ❌ 싱글스레드로 실행 | ✅ 빠르고 가벼운 캐싱 |
| **사용 사례** | Redis 대체 엔진, 성능 최적화 필요 시 | 데이터 저장 & 고급 캐싱 | 단순 캐싱 (웹 페이지, 세션 관리) |

📌 **즉, Redis와 Valkey는 다양한 기능과 확장성을 제공하며, Memcached는 빠르고 가벼운 캐싱에 최적입니다.**  




---
---
