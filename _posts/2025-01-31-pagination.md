---
title: "[spring boot] 페이지네이션(Pagination)"
categories:
- spring
description: spring, 페이지네이션, pagination
tags:
- spring
- pagination
pin: true
math: true
mermaid: true
---

---
# 💡 페이지네이션 (Pagination)

페이지네이션(Pagination)은 데이터를 일정한 크기의 페이지 단위로 나누어 한 번에 너무 많은 데이터를 불러오지 않고, 필요한 만큼만 조회할 수 있도록 하는 기법입니다.

페이지네이션은 크게 **Offset 기반 페이지네이션**과 **Cursor 기반 페이지네이션**으로 나뉩니다.

- **Offset 기반 페이지네이션**  
  특정 페이지의 데이터를 조회하는 방식입니다.  
  ![]({{ 'assets/img/docs/스크린샷 2025-01-31 오후 4.23.36.png' | relative_url }})

- **Cursor 기반 페이지네이션**  
  무한 스크롤과 같이 특정 컬럼 값을 기준으로 다음 페이지의 데이터를 가져오는 **No-Offset 방식**입니다.


---
## offset 기반 페이지 네이션
Offset 기반 페이지네이션은 SQL의 LIMIT과 OFFSET을 활용하여 특정 페이지의 데이터를 조회하는 방식입니다.
간단하게 말하면, OFFSET은 건너뛸 데이터의 개수이고, LIMIT은 가져올 데이터의 개수를 의미합니다.

```sql
SELECT * FROM member
ORDER BY email
LIMIT 10 OFFSET 0;
```
위의 쿼리문은 member 테이블에 email 컬럼기준으로 0번째 순서부터  10개의 데이터를 가져오라는 의미 입니다.
이러한 간단한 쿼리문과 같은 특성 때문에 offset 기반 페이지네이션은 구현이 쉽다는 장점이 있습니다.
하지만 가장 큰 단점이 있는데 바로 중복 데이터에 노출되는 문제가 발생한다는 점입니다. 

예를들어 정렬기준 데이터 칼럼이

| 1페이지 | 2페이지 | 3페이지 |
| -------- | -------- | -------- |
| a     | f     | k     |
| b     | g     | l     |
| c     | h     | m     |
| d     | i     | n     |
| e     | j     | o     |

이런식으로 처음 데이터가 정렬되어 있는 데 1페이지의 5개의 데이터를 보고 있는 유저가 2페이지로 넘어가기 직전 새로운 데이터가 추가되거나 기존 데이터가 삭제되었다고 했을때  중복 문제가 야기 될수 있습니다.

| 1페이지 | 2페이지 | 3페이지 |
| -------- | -------- | -------- |
| **가**     | d     | i     |
| **나**     | e     | j     |
| a     | f     | k     |
| b     | g     | l     |
| c     | h     | m     |

위처럼 "가" "나" 데이터가 추가 된경우 기존 1페이지에서 2페이지로 넘어간 유저는 d e 데이터를 중복적 데이터가 발생됩니다.
이와 같이 OFFSET을 사용하는 방식은 특정한 페이지로 정확히 이동하는 게 아니라 "건너뛸 개수를 기준"으로 가져오는 방식이라 데이터가 변동되면 순서가 뒤틀릴 가능성이 있습니다.

다음으로는 Offset 데이터(페이지 번호)가 커질수록 성능저하가 발생한다는 점입니다.

```sql
SELECT * FROM users
ORDER BY id ASC
LIMIT 10 OFFSET 1000000;
```
위와같이 만약 Offset이 100만개이면 10개의 데이터를 반환하기 위해 100만개의 데이터를 스캔해야 되므로 쿼리문이 매우 비효율적이게 됩니다.

이러한 문제 때문에 전체 데이터의 수가 적거나 데이터의 변동이 거의 드문 경우가 아닐경우 offset기반 페이지 네이션은 점점 사용이 드물어 지고 있습니다.


---
## cursor 기반 페이지네이션

커서 기반 페이지네이션은 OFFSET을 사용하지 않고 특정 기준 값(Cursor)을 사용하여 다음 페이지를 조회하는 방식입니다
마지막으로 본 데이터의 ID나 Timestamp를 기준으로 이후 데이터를 가져와서 offset기반 페이지네이션보다 훨씬 빠르고 효율적인 방식입니다


cursor는 일반적으로 데이터베이스에서 특정 행을 가리키는 의미로 사용 합니다. 여기서 cursor 기반이라고 하는 것은 마지막 데이터를 기준으로 다음 데이터를 가져오기 때문입니다.

sql 문으로 예를 들자면
```sql
SELECT * FROM users
WHERE id > 1000
ORDER BY id ASC
LIMIT 10;
```
이런식으로 id를 cursor로 설정하여 1000번 이후의 id에 해당하는 10개의 데이터를 차례로 가져옵니다.

### cursor 기반 페이지네이션의 장점
#### 1️⃣ **데이터가 많아져도 성능저하가 낮다**  
cursor 기반 페이지네이션은 항상 커서를 기준으로 다음 LIMIT 만큼의 데이터를 가져오기 때문에 offset기반 페이지네이션과 달리 offset을 사용할 필요가 없어 데이터가 많아져도 성능저하가 거의 없다는 장점이 있습니다.

####  2️⃣**데이터가 많아져도 성능저하가 낮다**  
데이터가 추가/삭제될 때 페이지가 변동될수도 있지만 1는 장점이 있습니다.

### cursor 기반 페이지네이션의 단점
#### 1️⃣ **기준 Key가 중복될 경우 문제 발생**  
`ID` 값이나 `Timestamp`처럼 유니크한 값을 기준으로 삼을 경우 문제없지만,  
`title`과 같이 **중복될 가능성이 있는 Key**를 커서로 사용할 경우 정확한 결과를 가져오기 어렵습니다.

#### 2️⃣ **전체 페이지 수를 알기 어렵다**  
cursor 기반 방식은 데이터를 가져올 때 **`WHERE id > last_cursor`** 와 같은 방식으로 조회하기 때문에,  
`COUNT(*)`를 활용한 전체 개수를 미리 알기 어렵습니다.

#### 3️⃣ **이전 페이지로 이동이 어렵다**
특정 페이지 번호를 계산해서 이동할 수 있는 offset 기반 방식과 달리 cursor 기반 페이지네이션은 현재 커서 이후의 데이터만 조회하기 때문에 `WHERE id < first_cursor`이러한 쿼리문을 작성해야 하지만 정렬도 다시 해야되고 복잡해지는 문제가 있습니다.


이러한 특성 때문에 cursor 기반 페이지네이션은 more과 같은 다음 페이지 이동만 지원하거나 무한 스크롤과 같은 방식을 사용할 때 적합합니다.





---
---

## cursor 기반 페이지네이션 실제 구현
이번에 진행하고 있는 ummgoban 프로젝트에서 실제로 가게 리뷰 관련 목록을 불러오는 api를 개발하는 과정에서 cursor 기반 페이지네이션을 사용 하였습니다.


```java
public Slice<ReviewPagingInfoDto> findReviewByCursorId(Long marketId, Long cursorId, Integer size) {
	List<ReviewPagingInfoDto> content =
		em.createQuery("select " +
			"new com.market.review.dto.server.ReviewPagingInfoDto" +
			"(r.id," +
			"r.member," +
			"r.content," +
			"r.rating," +
			"r.createdAt)" +
			"from Review r " +
			"where r.market.id = :marketId " +
			"and (:cursorId = 0L or r.id > :cursorId) " +
			"order by r.createdAt DESC", ReviewPagingInfoDto.class)
		.setParameter("marketId", marketId)
		.setParameter("cursorId", cursorId)
		.setMaxResults(size + 1)
		.getResultList();

	return hasNext(content, size);
}


private Slice<ReviewPagingInfoDto> hasNext(List<ReviewPagingInfoDto> content, Integer size) {

	boolean hasNext = false;

	// 다음 페이지가 있는 경우,
	if (content.size() > size) {
		hasNext = true;
		return new SliceImpl<>(new ArrayList<>(content.subList(0, size)), Pageable.ofSize(size), hasNext);
	}

	return new SliceImpl<>(content, Pageable.ofSize(size), hasNext);
}
```

		
위 코드를 살펴보면 
`SELECT new ReviewPagingInfoDto(...)` → Review 엔티티에서 필요한 데이터만 DTO로 변환하여 조회 

`WHERE r.market.id = :marketId` → 특정 가게(marketId)의 리뷰만 조회

`AND (:cursorId = 0L OR r.id > :cursorId)` → 처음 조회하는 경우(cursorId = 0L) 모든 데이터를 가져옴

이후에는 `r.id > :cursorId` 조건을 추가하여 현재 페이지 이후의 데이터만 조회

`ORDER BY r.createdAt DESC` → 최신순으로 정렬

`.setMaxResults(size + 1)` → size보다 1개 더 조회하여 다음 페이지가 있는지 확인 가능

이렇게 구성되어 있습니다.

여기서 `Slice<T>`는 Spring Data JPA에서 **페이징(Pagination)**을 처리할 때 제공되는 방식입니다.
		 Spring Data JPA에는 `Page<T>`와 `Slice<T>` 두가지 방식을 제공합니다.  `Page<T>`는 전체 개수를 조회해야 하는 페이지네이션일때 사용되고 `Slice<T>`는 현재페이지 + 다음페이지 존재여부 만 필요할 때 사용됩니다.
 
 
 
📌 **메서드의 진행 과정은 cursorId(현재 페이지의 마지막 리뷰 ID), size(가져올 리뷰 개수)를 클라이언트에게 받아와서 요청한 cursorId부터 size만큼의 리뷰 list와 다음 페이지의 여부를 같이 클라이언트에 반환하게 됩니다. 쿼리문에서도 알 수 있듯 항상 특정 cursor 이후의 데이터를 가져오기 때문에 데이터가 많아져도 성능저하가 일어나지 않는다는 것을 알수 있습니다.**
