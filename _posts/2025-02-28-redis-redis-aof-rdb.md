---
title: "[Redis] Redis의 캐시 데이터 영속화 옵션 방식(AOF, RDB)"
tags:
- redis
pin: true
math: true
mermaid: true
categories:
- spring
- Redis
description: Redis의 캐시 데이터 영구 저장 방식(AOF, RDB)에 대해서 포스팅 한 글입니다.
---

#  Redis의 캐시 데이터 영속화 옵션 방식(AOF, RDB)

---
## 💡 개요  
Redis는 인메모리 데이터베이스로, 데이터를 빠르게 처리할 수 있도록 서버의 주 메모리(RAM)에 저장합니다. 그러나 데이터 영구 저장을 위해 **AOF(Append-Only File)**와 RDB(Redis Database File) 방식을 제공하며, 각각의 방식은 데이터 복구 속도, 성능, 신뢰성 측면에서 차이가 있습니다. 본 문서에서는 Redis의 캐시 데이터 영구 저장 방식에 대해 자세히 살펴봅니다.

---

##  💡 AOF(Append-Only File) 방식

AOF(Append-Only File)는 Redis의 모든 쓰기(Write) 연산을 로그 파일에 기록하여 영구 저장하는 방식입니다. Redis는 기본적으로 메모리에 데이터를 저장하지만, AOF를 활성화하면 모든 변경 사항을 파일에 기록하여 데이터 영속성을 보장할 수 있습니다.

### 1. AOF 동작 방식
AOF는 Redis의 모든 **쓰기 명령어(SET, INCR, LPUSH 등)**를 텍스트 형태로 저장하며, 이 과정을 통해 장애 발생 시 데이터를 복구할 수 있습니다.

AOF는 3가지 기능이 있습니다.
- 명령어 기록: Redis의 모든 변경 연산은 appendonly.aof 파일에 순차적으로 저장됩니다.
- 디스크 동기화: 기록된 명령어를 일정 주기마다 디스크에 동기화하여 데이터를 보존합니다.
- 데이터 복원: Redis 재시작 시, AOF 파일의 명령어를 순차적으로 실행하여 데이터 상태를 복구합니다.****


### 2. AOF 동기화(Appendfsync) 설정

```
# AOF 설정 활성화
appendonly yes  

# AOF 동기화 옵션
appendfsync always   # 매번 명령 실행 후 디스크에 기록 (가장 안전하지만 느림)
appendfsync everysec # 1초마다 한 번씩 기록 (균형 잡힌 방식, 기본값)
appendfsync no       # OS가 알아서 디스크에 기록 (가장 빠르지만 데이터 유실 가능)
```

### 3. 확장성  
여러 개의 Redis 인스턴스를 클러스터로 구성하여 **데이터 저장 용량을 확장 가능**  
- **Sharding**: 데이터를 여러 노드에 분산하여 저장  
- **Replication**: 여러 개의 노드에 동일한 데이터를 복제하여 부하 분산  

### 4. 트랜잭션 및 Atomic 연산 지원  
여러 개의 명령을 하나의 트랜잭션으로 묶어 실행 가능  
- **MULTI, EXEC, WATCH** 명령어를 사용하여 **일관성 있는 데이터 처리** 지원  

### 5. Pub/Sub 메시징 기능  
**Publish/Subscribe 모델**을 활용한 실시간 메시지 브로커 역할 수행  

---

## 💡Redis OSS vs Redis Enterprise

| **기능** | **Redis OSS** | **Redis Enterprise** |
|--------|-------------|------------------|
| **가격** | 무료 | 유료 |
| **속도** | 고속(싱글스레드) | 성능 최적화 (멀티스레드) |
| **Sharding** | 수동 설정 | 자동 설정 |
| **Cluster 지원** | 설정 복잡 | 쉬운 설정 & 자동 확장 |
| **데이터 지속성** | AOF, RDB 지원 | Flash 기반 저장 지원 |
| **보안** | 기본 인증 | TLS 암호화, ACL 지원 |
| **고가용성(HA)** | Sentinel 지원 | Active-Active Geo-Replication 지원 |

### Redis OSS를 선택해야 하는 경우  
-  비용이 중요한 경우 (무료 사용)  
-  작은 규모의 애플리케이션  
-  기본적인 **캐싱, 세션 저장소, 간단한 데이터 처리**  
-  클러스터링이 필요 없거나 직접 관리 가능  

### Redis Enterprise를 선택해야 하는 경우  
-  대규모 트래픽을 처리해야 하는 경우  
-  **자동 확장과 고가용성이 필요한 경우**  
-  여러 지역에서 **데이터 동기화(Geo-Replication)가 필요한 경우**  
-  **보안(암호화, ACL 등)이 중요한 환경**  
-  **멀티스레드를 활용하여 더 높은 성능을 원하는 경우**  
-  **Redis를 직접 관리하기 어렵고, 전문적인 지원이 필요한 경우**  
 
---

## 💡 Active-Active Geo-Replication

Active-Active Geo-Replication는 Active-Active 아키텍처 또는 Active-Active Geo-Distributed topology 여러 클러스터에 걸쳐 있는 글로벌 데이터베이스를 사용하여 Redis Enterprise에서 **CRDT (충돌 없는 복제 데이터 유형)** 를 구현하여 달성됩니다. 이를 **"충돌 없는 복제 데이터베이스" 또는 "CRDB"** 라고 합니다.

CRDB는 다른 지리적으로 분산된 솔루션에 비해 세 가지 기본적인 이점을 제공합니다.

- 지리적으로 복제된 지역의 수나 각 지역 간의 거리에 관계없이 읽기 및 쓰기 작업에 로컬 대기 시간을 제공합니다.
- Redis 코어의 간단한 데이터 유형과 복잡한 데이터 유형에 대해 원활한 충돌 해결("충돌 없는")이 가능합니다.
- CRDB의 대부분 지리적 복제 지역(예: 5개 중 3개)이 다운되더라도 나머지 지리적 복제 지역은 중단 없이 읽기 및 쓰기 작업을 계속 처리할 수 있으므로 비즈니스 연속성이 보장됩니다.


---

##  💡 Redis Cluster vs Redis Sentinel  

###  Redis Cluster의 특징  
-  데이터가 자동으로 **Sharding되어 여러 노드에 분산 저장**  
-  여러 노드가 동시에 작동하여 **읽기/쓰기 성능 향상**  
-  **Failover(자동 장애 조치) 지원** → 노드 장애 발생 시 자동으로 다른 노드가 역할을 대신함  
-  **대규모 애플리케이션에서 고가용성과 확장성을 제공**  
-  **쓰기(write) 연산을 여러 노드에서 동시에 처리 가능**  
-  **Hash Slot 개념 사용**하여 데이터를 자동으로 분산 저장  

📌 **확장성과 고가용성이 중요할 때 사용**  

###  Redis Sentinel의 특징  
-  **Master-Slave(Primary-Replica) 복제 구조에서 자동 장애 감지 및 복구**  
-  **Master 노드가 다운되면 Sentinel이 자동으로 다른 Slave를 Master로 승격(Failover)**  
-  **애플리케이션이 Sentinel을 통해 Redis 서버의 IP 주소를 자동으로 변경하여 연결 유지**  
-  클러스터가 필요 없고, **단순한 고가용성(HA) 기능을 제공**  
-  **데이터 sharding 없음** → 모든 데이터가 하나의 Master에 저장됨  
-  **쓰기(write)는 Master에서만 가능**, Slave는 읽기 전용  

📌 **단순한 고가용성만 필요할 때 사용**  

---

##  💡 Redis Cluster vs Redis Sentinel 

| **비교 항목** | **Redis Cluster** | **Redis Sentinel** |
|-------------|------------------|------------------|
| **주요 목적** | **확장성 & 고가용성** | **고가용성(HA) 보장** |
| **데이터 Sharding** |  자동 분산 |  없음 |
| **읽기 부하 분산(Read Scaling)** | 여러 노드에서 가능 | Slave에서 가능 |
| **쓰기 부하 분산(Write Scaling)** | 여러 Master에서 가능 | 단일 Master에서만 가능 |
| **구성의 복잡도** | **복잡함 (클러스터 설정 필요)** | **간단함 (Sentinel 추가만 하면 됨)** |
| **사용 사례** | **대규모 트래픽, 글로벌 서비스** | **기본적인 HA 제공, 작은 규모 애플리케이션** |

📌 **대규모 확장이 필요할 때는 Redis Cluster, 소규모 서비스라면 Redis Sentinel을 사용**  

---

## 💡 Redis vs DBMS 차이점  

-  **Redis는 DBMS와 달리 빠른 메모리 접근 속도를 제공하지만, 복잡한 트랜잭션 처리는 어렵다.**  
-  **일반적인 데이터 저장 및 관리에는 DBMS를 사용하고, 속도 최적화가 필요한 경우 Redis를 캐싱으로 활용하는 것이 일반적이다.**  

---

##  📝 결론  

💡 **Redis는 빠른 속도와 실시간 데이터 처리가 필요할 때 유용한 인메모리 데이터베이스**  
💡 **Redis OSS는 무료 & 소규모 애플리케이션에 적합, Redis Enterprise는 대규모 서비스에 최적화**  
💡 **확장성이 필요하면 Redis Cluster, 단순한 고가용성이 필요하면 Redis Sentinel 사용**  
💡 **DBMS와 함께 사용하여 속도 최적화 + 영구 데이터 저장 조합 가능**  

---
