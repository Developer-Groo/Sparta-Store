# 💻 Sparta Store
**Sparta Store 는 자사몰 플랫폼으로, 상품 판매 및 주문, 리뷰 관리, 사용자 맞춤형 알림 서비스 등의 기능을 제공하는 백엔드 프로젝트입니다**

## ☑️ Index
- [🏁 Team](#-Team)
- [🤔 Team Document](#-Team-Document)
- [🛠 Technology](#-Technology)
- [🔗 ERD](#-ERD)
- [🧬 Service Architecture](#-Service-Architecture)
- [🚨 Trouble Shooting](#-Trouble-Shooting)
- [🆚 Technical Decision](#-Technical-Decision)

<br>

## 🏁 Team
|**👑 Leader**|**Contributions**|
|:--------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/09c03daf-fed7-4bbd-adf6-8909ef950606" width="150" height="150">| <div align="left"> • 전체 개발 계획 수립 및 설계 <br> • 코드 리뷰를 통한 팀의 품질 및 생산성을 향상 <br> • Spring Boot, JPA 기반으로 상품 및 리뷰 관리, 카테고리 기능 개발 <br> • 품절 상품 재입고 시 이메일 알림 기능 구현 <br> • 비관적 Lock 을 활용해 상품 재고 감소 동시성 이슈 해결 <br> • GitHub Actions를 활용해 자동화된 빌드, 테스트, 배포 파이프라인 구축 <br> • AWS 인프라 설계 및 구축, EC2, RDS, ElastiCache, ELB 등을 활용한 서비스 운영 <br> • 블루/그린 무중단 배포를 적용하여 배포 시 서비스 다운타임 최소화 </div> |
|**김우현**|[GitHub Link](https://github.com/Developer-Groo)|

|**👑 Sub Leader**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/bc4b03e5-c31d-4144-b384-b1607a7f544b" width="150" height="150">| <div align="left"> • Spring Boot, JPA 기반으로 • 장바구니, 찜 기능 개발 <br> • 장바구니 기능 Redis 적용 <br> • 비관적 Lock 을 활용해 상품의 총 찜 횟수 동시성 이슈 해결 </div> |
|**윤주영**|[GitHub Link](http://github.com/ju-young0)|

|**🤴🏻 Member**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/98ad61fe-84ad-4012-8294-76d23c0f1eeb" width="150" height="150">| <div align="left"> • Spring Boot, JPA 기반으로 주문 및 결제 기능 개발 <br> • Redis의 Lua 스크립트를 이용한 원자성 확보로 쿠폰 중복 발급 방지 <br> • 주문 상태 변경 시 실시간 이메일 알림 기능 구현 <br> • PG사 연동을 통한 결제 승인 및 취소 <br> • 결제 오류 케이스 구분 및 에러 처리 <br> • 주문 생성 성능 개선 <br> • 선착순 쿠폰 발급 구현 및 성능 개선 </div>|
|**고수연**|[GitHub Link](https://github.com/suyeon1717)|

|**🤴🏻 Member**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/fcec6de3-df08-4fa9-85e9-35db4f0d3065" width="150" height="150">| <div align="left"> • Spring Boot, JPA 기반으로 사용자 관리 및 기준별 인기 상품 조회, 포인트 기능 개발 <br> • Redis를 활용한 글로벌 캐싱을 적용 <br> • 주요 검색어 저장 및 조회, DB 부하 감소 및 검색 성능 최적화 <br> • 일주일 간 판매량 순, 누적 좋아요 순 인기 상품 조회 기능 구현 </div>|
|**이현우**|[GitHub Link](https://github.com/eagleowlee)|

|**🤴🏻 Member**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/173540b4-ebf5-46b5-9f86-0f3be2e107b9" width="150" height="150">| <div align="left"> • Spring Boot, Spring Security, JWT 기반으로 회원가입, 로그인, 권한 관리 기능 개발 </div>|
|**민진홍**|[GitHub Link](https://github.com/wls313)|

<br>

## 🤔 Team Document

- Team Notion 보러가기 👉 [Team Notion](https://gaudy-ounce-8ec.notion.site/Sparta-Store-1c855523239d80ce81bdf35287ecd1ed)
- Team Convention 보러가기 👉 [Team Convention](https://github.com/Developer-Groo/Sparta-Store/wiki/Team-Convention)

<br>

## 🛠 Technology
| **분야**        | **기술** |
|--------------|--------|
| **Backend** | Java 17, Spring Boot 3.x, Spring security 6.x, JPA, QueryDSL 등|
| **DB** | MySQL 8.0, Redis 7.2.7 |
| **Cache** | Redis Cache |
| **Concurrency Control** | Pessimistic Lock, Optimistic Lock |
| **Testing** | JUnit5, MockMvc, Locust |
| **DevOps** | Github Actions, Docker, AWS ELB, AWS EC2, AWS RDS, AWS Code Deploy 등 |

<br>

## 🔗 ERD

~~~ mermaid
erDiagram
    Users {
        Long id PK
        String name
        String email
        String password
        Address address
        tinyint is_deleted
        UserRoleEnum role
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    Item {
        Long id PK
        String name
        String img_url
        Integer price
        String description
        Integer stockQuantity
        Category category
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    Orders {
        String order_id PK
        Users user_id FK
        IssuedCoupon issued_coupon_id FK
        OrderStatus order_status
        Long total_price
        Address address
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    OrderItem {
        Long id PK
        Orders order_id FK 
        Item item_id FK
        Integer order_price
        Integer quantity
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    Address {
        String city
        String street
        String zipcode
    }
    
    CartItem {
		    Long id PK
        Cart cart_id FK
        Item item_id FK
        Integer quantity
        LocalDateTime created_at
        LocalDateTime updated_at
    }
    
    Cart {
        Long cart_id PK
        Users user_id FK
        LocalDateTime created_at
        LocalDateTime updated_at
    }
    
    Review {
        Long id PK
        Users user_id FK
        Item item_id FK
        String content
        String img_url
        int rating
        LocalDateTime created_at
        LocalDateTime updated_at
    }
    
    Category {
		    Long category_id PK
        String name 
        Category parent_id FK "Parent Category"
    }
    
    Likes {
	    Long likes_id PK
	    Users user_id FK
	    Item item_id FK
	    LocalDateTime created_at
      LocalDateTime updated_at
    }
    
    SalesSummary {
	    Long id pk
	    Item item_id FK
	    int totalSales
	    LocalDateTime created_at
      LocalDateTime updated_at
    }
    
    Payment {
	    String payment_key PK
	    Orders order_id FK
	    LocalDateTime approved_at
	    Long amount
	    String method
	    boolean is_cancelled
	    boolean is_aborted
    }
    
    IssuedCoupon {
	    Long id PK
	    String coupon_name
	    String amount
	    Long user_id
	    boolean is_used
	    LocalDateTime expiration_date
	    LocalDateTime created_at
      LocalDateTime updated_at
    }
    
    PointSummary {
	    Long summary_id PK
	    Users user_id FK
	    Long point_amount
	    SummaryType summary_type
	    LocalDateTime created_at
    }

    Users ||--o{ Orders : "has many"
    Users ||--|| Cart : "has one"
    Cart ||--o{ CartItem : "contains"
    Item ||--o{ CartItem : "is in"
    Orders ||--o{ OrderItem : "has many"
    Item ||--o{ OrderItem : "has many"
    Users }|..|{ Address : "embedded"
    Review }o--|| Users : "has many"
    Review }o--|| Item : "has many"
    Category ||--o| Category : "parent"
    Category ||--|{ Category : "children"
    Category ||--|{ Item : "has"
    Users ||--o{ Likes : "likes"
    Item ||--o{ Likes : "liked by"
    SalesSummary ||--|| Item : "has one"
    Payment ||--|| Orders : "has one"
    PointSummary }o--|| Users : "has many"
    Orders ||--|| IssuedCoupon : "has one"
~~~

<br>

## 🧬 Service Architecture

<img width="1400" src="https://github.com/user-attachments/assets/16301b92-5ccf-4105-8597-0762d299cee7"/>

<br>

## 🚨 Trouble Shooting

<h3>🔧 동시성 문제 (재고 감소 처리)</h3>
<details>
	<summary>👉 자세히 보기</summary>

## 🧠 배경 및 문제 상황
상품 재고를 차감하는 기능에서 동시에 여러 구매 요청이 들어올 경우 재고가 음수가 되는 문제가 발생했습니다.   
초기에는 단순한 UPDATE 쿼리로 재고 수량을 감소시키는 방식이었지만 테스트 환경에서 여러 요청이 동시에 재고를 차감할 경우 실제 재고보다 많은 주문이 처리되는 문제가 발생했습니다.   
이는 실서비스에서 데이터 무결성을 해칠 수 있는 **심각한 동시성 이슈**로 판단되었습니다.

<br>

## 🔍 문제 분석 및 고민한 방향
문제 해결을 위해 먼저 고려한 방법은 **낙관적 락**과 **비관적 락**의 비교였습니다.

| **방법** | **장점** | **단점** |
|---------|---------|---------|
| 낙관적 락 | - 성능 우수 <br> - 락을 사용하지 않음| - 충돌 시 예외처리가 복잡함 <br> - 재시도 로직으로 인한 리소스 낭비|
| 비관적 락 | - 충돌 자체를 방지함 <br> - 안정성이 확보됨 | - 트랜잭션이 길어지면 락 대기 발생 가능 |

낙관적 락은 성능적으로는 유리했지만 재고 감소 로직은 정확성과 안정성이 우선되어야 했기 때문에 충돌 시 예외 처리가 복잡하고 재시도 로직이 리소스를 소모하는 낙관적 락은 적합하지 않다고 판단했습니다.

또한 분산 환경이 아닌 단일 인스턴스 기반의 프로젝트였기 때문에 Redis 기반의 분산 락은 복잡도와 오버헤드 측면에서 적합하지 않아 비교 대상에서 제외했습니다.

<br>

## ⚙️ 선택한 기술 및 구현 방식
### ✅ 비관적 락 적용

JPA 의 @Lock(LockModeType.PESSIMISTIC_WRITE) 어노테이션을 사용하여 트랜잭션 단위에서 다른 트랜잭션의 접근을 차단하는 비관적 락을 적용했습니다.

~~~ java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select i from Item i where i.id in :itemIds")
List<Item> findAllByIdWithLock(@Param("itemIds") List<Long> itemIds);
~~~

- 트랜잭션 안에서 findAllByIdWithLock 를 사용해 재고 차감 전 락을 획득
- 다른 트랜잭션은 해당 데이터에 대한 접근을 차단 
- 데이터 정합성을 보장


<br>

## ✅ 적용 결과 및 개선 효과
- 같은 상품에 여러 주문 요청이 동시에 들어와도 락을 통해 재고 차감이 순차적으로 처리됨
- 재고 수량이 음수가 되는 현상 제거
- 테스트 및 로컬 환경에서 안정적으로 동작 확인

결과적으로 **데이터 무결성과 안정성**을 확보하며 실서비스에서도 문제없이 적용 가능한 로직으로 변경하였습니다.

<br>

## 📎 참고 링크

- [Spring Data JPA - Lock Support](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html)

<br>

</details>

	• 문제: 동시 주문 요청 시 재고가 음수가 되는 동시성 문제 발생
 
	• 원인: 단순 UPDATE 쿼리 사용으로 인해 트랜잭션 간 충돌 미처리
 
	• 시도: 낙관적 락 vs 비관적 락 비교
		• 낙관적 락: 성능은 상대적으로 뛰어나지만 예외 처리 및 재시도 로직 복잡함
		• Redis 분산 락: 단일 인스턴스 환경으로 불필요한 복잡도
	• 해결:
		• JPA의 @Lock(PESSIMISTIC_WRITE) 적용
		• 트랜잭션 시작 시점에 재고 row 에 락 획득 → 충돌 차단
	• 성과:
		• 재고 음수 문제 제거, 재고 정확도 100% 확보
		• 데이터 무결성 보장
  
<br>

<h3>🧪 테스트 코드 트랜잭션 롤백 실패</h3>
<details>
	<summary>👉 자세히 보기</summary>

## 🧠 배경 및 문제 상황

ItemService 의 decreaseStock() 메서드에서 재고가 부족한 경우 CustomException 을 발생시켜 트랜잭션을 롤백하도록 구현했습니다.
이를 테스트하기 위해 아래와 같은 테스트 코드를 작성했습니다.

~~~ java
@Test
@Transactional
@DisplayName("재고 감소 실패 - 트랜잭션 롤백")
void shouldRollbackTransaction_When_StockIsInsufficient() {
        // given
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, null, item1, 1000, 3),
                new OrderItem(2L, null, item2, 1000, 2),
                new OrderItem(3L, null, item3, 1000, 20)
        );

        // when
        assertThatThrownBy(() -> itemService.decreaseStock(orderItems))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ItemErrorCode.OUT_OF_STOCK);

        // then
        assertThat(itemRepository.findById(item1.getId()).get().getStockQuantity()).isEqualTo(110);
        assertThat(itemRepository.findById(item2.getId()).get().getStockQuantity()).isEqualTo(10);
        assertThat(itemRepository.findById(item3.getId()).get().getStockQuantity()).isEqualTo(10);
}
~~~

하지만 테스트 실행 결과 재고가 롤백되어야 함에도 불구하고 **차감된 값(107)** 이 조회되면서 테스트가 실패했습니다.    
예외도 정상적으로 발생했고 로그에서도 롤백이 수행된 것으로 보였지만 테스트에서는 여전히 차감된 상태의 데이터를 조회하는 문제가 있었습니다.

<br>

## 🔍 문제 분석 및 고민한 방향

### ✅ 주요 의문점
- 왜 예외가 발생했음에도 .getStockQuantity() 값이 110 이 아닌 107 로 조회될까?
- 롤백이 정상적으로 동작하지 않은 걸까?

### ✅ 로그 분석 결과

~~~ text
[    Test worker] o.s.orm.jpa.JpaTransactionManager        : Participating transaction failed - marking existing transaction as rollback-only
[    Test worker] o.s.orm.jpa.JpaTransactionManager        : Setting JPA transaction on EntityManager [SessionImpl(413647350<open>)] rollback-only
[    Test worker] cResourceLocalTransactionCoordinatorImpl : JDBC transaction marked for rollback-only (exception provided for stack trace)
~~~

- 내부 트랜잭션에서 발생한 예외로 인해 롤백은 실제 수행된 게 아니라 rollback-only 로 마킹된 상태
- 테스트 메서드 자체에 @Transactional 이 붙어 있어 최상위 트랜잭션이 여전히 유효
- 이로 인해 영속성 컨텍스트는 여전히 변경된 엔티티 상태를 유지
- 테스트에서 조회한 값은 DB 에서 읽은 값이 아닌 1차 캐시에 있는 변경된 값이므로 예상과 다름

<br>

## ⚙️ 선택한 해결 방식

### ❌ EntityManager.flush() + clear()

~~~ java
em.flush();
em.clear();
~~~

- 1차 캐시를 비우면 해결될 것이라고 생각했지만 이미 rollback-only 인 트랜잭션 컨텍스트에서는 flush 도 제한적
- rollback-only 상태에서 flush 가 실행되지 않거나 이후 rollback 이 테스트 메서드 종료 시점에 수행되므로 무의미함

### ✅ 테스트 메서드에서 @Transactional 제거

~~~ java
@Test
@DisplayName("재고 감소 실패 - 트랜잭션 롤백")
void shouldRollbackTransaction_When_StockIsInsufficient() {
    ...
}
~~~

- 테스트 메서드를 트랜잭션 밖에서 실행하면 내부 트랜잭션에서 예외 발생 시 즉시 rollback 됨
- 테스트 메서드에서는 DB 에서 최신 상태를 바로 조회할 수 있어 정확도가 확보됨

<br>

## ✅ 적용 결과 및 개선 효과

- .getStockQuantity() 가 정상적으로 롤백된 110 으로 조회됨
- 테스트 통과
- 트랜잭션 흐름과 1차 캐시의 동작 원리에 대한 이해도 향상

<br>

</details>

	• 문제: 예외 발생 후에도 .getStockQuantity() 값이 롤백되지 않고 차감된 값으로 조회됨 → 테스트 실패
	• 원인:
		• 테스트 메서드에 @Transactional이 붙어 있어 내부 트랜잭션 rollback-only 상태로 마킹
		• 영속성 컨텍스트는 여전히 변경된 엔티티 상태를 유지
		• 조회 시 DB가 아닌 1차 캐시에서 값을 읽음 → 롤백된 상태와 불일치 발생
	• 시도한 방법:
		• em.flush() + em.clear() → rollback-only 상태에서 flush가 무의미하여 실패
	• 해결 방법:
		• 테스트 메서드에서 @Transactional 제거
		• 예외 발생 시 내부 트랜잭션이 즉시 rollback
		• 테스트는 DB로부터 실제 상태를 조회
	• 성과:
		• .getStockQuantity() 값이 정확히 롤백되어 테스트 통과
		• 트랜잭션 흐름 및 1차 캐시 동작 원리에 대한 실전 경험 확보

<br>

<h3>⚙️ Dockerfile 빌드 최적화</h3>
<details>
	<summary>👉 자세히 보기</summary>

## 🧠 배경 및 문제 상황

Docker 기반 CI/CD 환경에서 컨테이너 이미지 용량이 불필요하게 크고 빌드 시간이 오래 걸리는 문제를 확인했습니다.
또한 빌드 단계에서 매번 의존성을 다시 설치하거나 불필요한 파일까지 포함되어 배포 효율성과 유지보수성에도 영향을 줄 수 있다고 판단했습니다.

이를 해결하기 위해 이미지 용량을 줄이고 빌드 속도와 재사용성을 개선하는 최적화가 필요했습니다.

<br>

## 🔍 문제 분석 및 고민한 방향

Dockerfile 최적화를 위해 아래와 같은 항목을 중심으로 검토하였습니다

| **항목** | **선택지** | **비교** |
|---------|----------|---------|
| 베이스 이미지 | openjdk:17-jdk vs openjdk:17-jdk-slim | - 용량 차이 <br> - 기능 차이 |
| 빌드 방식 | 단일 스테이지 vs 멀티 스테이지 | - 보안성 <br> - 빌드 범위 제어 |
| 캐시 전략 | 전체 복사 vs .gradle 분리 복사 | - 캐시 재사용 <br> - 빌드 속도 향상 |

- **베이스 이미지** - openjdk:17-jdk-slim 은 일반 JDK 이미지보다 가볍고 실행에 필요한 필수 구성만 포함하여 122MB 이상 경량화 가능
- **멀티 스테이지 빌드** - 빌드 도구와 실행 환경을 분리하여 보안성과 유지보수성 향상
- **캐시 분리 전략** - build.gradle, settings.gradle 을 먼저 복사해 캐시 레이어를 효율적으로 활용함으로써 빌드 시간 대폭 단축

<br>

## ⚙️ 선택한 기술 및 구현 방식

### ✅ 베이스 이미지 최적화

~~~ dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
~~~

- 기존의 openjdk:17-jdk 에서 slim 이미지로 변경
- 약 122MB 의 이미지 크기 감소 확인

### ✅ 멀티 스테이지 빌드 적용

~~~ dockerfile
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
~~~

- 약 0.23mb 의 이미지 크기 감소 확인
- 기존 단일 스테이지의 경우에도 .jar 만 포함되므로 큰 효과는 없었음
- 하지만 보안성 및 유지보수성은 향상됨

### ✅ 빌드 캐시 레이어 분리 전략 적용

~~~ dockerfile
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
RUN gradle build --no-daemon -x test || true

COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
~~~

- 캐시 레이어를 활용해 gradle 의존성 재설치 방지
- 전체 빌드 시간 26.9초 → 1.9초로 약 93% 시간 절감

<br>

## ✅ 적용 결과 및 개선 효과

| **항목** | **Before** | **After** | **개선 효과** |
|---------|------------|-----------|-------------|
| Docker 이미지 용량 | 약 900.52MB | 약 777.6MB | 약 122.92MB 감소 |
| 빌드 시간 | 26.9 초 | 1.9 초 | 약 93% 단축 |
| 이미지 빌드 방식 | 단일 스테이지 | 멀티 스테이지 | 보안성 & 유지보성 개선 |

- CI/CD 전체 속도 향상
- 배포 및 테스트 환경에서 더 빠른 이미지 pull 가능
- 운영 환경에서 리소스 효율성 향상

<br>

## 📎 참고 이미지
| **구분** | **스크린샷** |
|---------|------------|
| 베이스 이미지 변경 전 → 후 | <img width="670" src="https://github.com/user-attachments/assets/31e8f6eb-9844-4819-b930-e7e9db8439f1"/> <br> <img width="670" src="https://github.com/user-attachments/assets/2ee74e5d-dc3b-49a7-aef0-dcd03fd31184"/> |
| 멀티 스테이지 적용 전 → 후 | <img width="670" src="https://github.com/user-attachments/assets/2ee74e5d-dc3b-49a7-aef0-dcd03fd31184"/> <br> <img width="670" src="https://github.com/user-attachments/assets/5a85b122-61c7-4850-8abb-7c9f4b8a1794"/>|
| 캐시 전략 적용 전 → 후 | <img width="340" src="https://github.com/user-attachments/assets/8634dce2-9664-4d68-bfac-6aafcba38dee"/> <br> <img width="340" src="https://github.com/user-attachments/assets/8d9ecbf4-882c-4e87-b061-f15bce17df88"/> |

<br>
</details>

	• 문제: Docker 이미지 용량 과다 및 CI 빌드 시간 지연 → 배포 효율성 저하
	• 원인:
		• 불필요한 베이스 이미지 사용 (openjdk:17-jdk)
		• 단일 스테이지 빌드 → 보안 및 용량 측면 비효율
		• 의존성 캐시 미활용 → 빌드 시마다 재설치 발생
	• 해결:
		• openjdk:17-jdk-slim 으로 베이스 이미지 변경
		• 멀티 스테이지 빌드 도입으로 빌드/런타임 분리
		• build.gradle, settings.gradle만 복사해 Gradle 캐시 재사용 전략 적용
	• 성과:
 		• 이미지 용량: 900.5MB → 777.6MB / 약 123MB 감소
   		• 빌드 시간: 26.9초 → 1.9초 / 약 93% 단축
     		• 빌드 방식: 단일 스테이지 -> 멀티 스테이지 / 보안성, 유지보수성 향상

<br>

## 🆚 Technical Decision

<h3>💰 NAT Gateway → Interface Endpoint 전환 (AWS 비용 최적화)</h3>
<details>
	<summary>👉 자세히 보기</summary>
	
## 🧠 배경 및 문제 상황

CI/CD 파이프라인을 통해 AWS ECR 에서 컨테이너 이미지를 가져올 때 **기본적으로 퍼블릭 네트워크 경로(NAT Gateway)** 를 통해 ECR 에 접근하고 있었습니다.

이 구성은 기능적으로는 문제가 없었지만 NAT Gateway 를 통해 송수신되는 데이터에 대해 트래픽 비용이 지속적으로 발생했고 특히 배포 빈도가 늘어나면서 예상보다 NAT 사용량 요금이 증가하고 있다는 문제를 인식하게 되었습니다.    

비용 최적화를 위해 더 효율적인 네트워크 구조를 고민하게 되었습니다.

<br>

## 🔍 문제 분석 및 고민한 방향

비용 최적화를 목적으로 아래 2가지 방법을 비교했습니다.

| **방법** | **장점** | **단점** |
|---------|---------|---------|
| Nat Gateway 유지 | - 구조가 단순하고 추가 설정 불필요 | - 데이터 송수신 비용이 지속적으로 발생 |
| Interface Endpoint 도입 | - AWS 내부망으로 통신 <br> - 트래픽 비용 절감 | - VPC 추가 설정 필요 |

Interface Endpoint 를 활용하면 퍼블릭 인터넷을 거치지 않고 AWS 내부망을 통해 ECR 에 직접 접근할 수 있어 NAT 트래픽 비용을 줄일 수 있다는 점이 큰 장점이었습니다.

또한 보안적인 측면에서도 ECR 접근이 Private Subnet 안에서만 이루어질 수 있다는 장점도 함께 고려했습니다.

<br>

## ✅ 적용 결과 및 개선 효과

- NAT Gateway 트래픽량 대폭 감소
- ECR Pull 시 데이터 전송 비용 절감
- Private Subnet 내에서만 통신 가능해 보안성 강화

비용 절감뿐만 아니라 보안성까지 동시에 확보하는 효과를 얻을 수 있었습니다.

<br>

## 📎 참고 링크

- [AWS 공식문서 - AWS PrivateLink concepts](https://docs.aws.amazon.com/vpc/latest/privatelink/concepts.html)
- [AWS 공식문서 - Amazon ECR interface VPC endpoints](https://docs.aws.amazon.com/AmazonECR/latest/userguide/vpc-endpoints.html)

<br>
 </details>

 	• 문제:
		• CI/CD 환경에서 ECR 이미지 Pull 시 NAT Gateway를 통해 통신
		• 배포 빈도 증가에 따라 NAT 트래픽 비용이 지속 상승
	• 해결 방법:
		• ECR용 Interface Endpoint 도입
		• 퍼블릭 경로가 아닌 AWS 내부망 을 통해 ECR 접근
		• VPC/Subnet 구성을 조정하여 Private Subnet 내에서만 통신 가능하도록 설정
	• 성과:
		• NAT 트래픽 비용 대폭 절감
		• 보안성 강화 / ECR 접근이 외부 노출 없이 내부망에서만 수행
		• CI/CD 배포 효율성 향상 및 운영 비용 최적화

<br>

<h3>📬 재입고 알림 기능 구현 (이벤트 리스너 vs MQ vs Kafka)</h3>
<details>
	<summary>👉 자세히 보기</summary>
	
## 🧠 배경 및 문제 상황

상품 재고가 품절 되었다가 재입고될 때 해당 상품을 찜한 사용자에게 이메일 알림을 발송하는 기능을 구현해야 했습니다.    
이 기능은 재고 수량이 갱신되는 시점을 트리거로 작동하며 다음과 같은 요구 조건이 있었습니다.
- 상품 재입고 시 실시간 또는 빠른 알림 발송
- 서비스 간 복잡한 연동이 필요하지 않음

이 알림 시스템을 어떤 방식으로 설계할지 고민하면서 이벤트 리스너, RabbitMQ, Kafka 세 가지 방식을 비교하게 되었습니다.

<br>

## 🔍 문제 분석 및 고민한 방향

| **방식** | **장점** | **단점** |
|---------|---------|---------|
| 이벤트 리스너 | - 간단한 내부 이벤트 흐름 <br> - 구현/테스트 용이 | - 동기/비동기 고민 필요 |
| RabbitMQ | - 메세지 기반의 비동기 처리 <br> - 재시도 가능 | - 브로커 설치 및 운영 필요 <br> - 설정 복잡함|
| Kafka | - 대용량 처리 <br> - 로그 기반 처리 가능 | - 러닝 커브 큼 <br> - 오버엔지니어링 가능성 |

<br>

## ⚙️ 선택한 기술 및 구현 방식

### ✅ 이벤트 리스너 방식 선택

재입고 알림 기능은 단순한 내부 서비스 이벤트 처리에 가깝고 별도의 대용량 메시징 처리나 시스템 간 분산 처리가 필요하지 않았습니다.    
따라서 Spring 의 내장 기능인 **@EventListener 와 ApplicationEventPublisher** 를 활용한 이벤트 기반 처리 방식을 선택했습니다.

~~~ java
// 이벤트 클래스
public record ItemRestockedEvent(
        Long itemId,
        String name,
        List<String> userEmails
) {
     ...
}

// 이벤트 발행
publisher.publishEvent(ItemRestockedEvent.toEvent(item, usersEmail));

// 이벤트 리스너
@Async
@EventListener
public void handleItemRestocked(ItemRestockedEvent event) {
    for (String email : event.userEmails()) {
        emailService.sendEmail(
               email,
               "재입고 알림",
               "안녕하세요, 고객님이 찜한 상품: " + "[" + event.itemId() + "] : "
                        + event.name() + " 상품이 재입고 되었습니다."
        );
    }
}
~~~

- 내부 도메인 이벤트를 통해 재입고 시점에 알림 로직을 분리
- 메인 로직과 알림 기능을 느슨하게 결합
- 단위 테스트와 유지보수 용이

<br>

## ✅ RabbitMQ, Kafka 적용이 적합하지 않았던 이유

### ❌ RabbitMQ
- 알림 기능 하나만을 위해 브로커를 도입하는 것은 운영 오버헤드 발생함
- 트래픽이 크지 않아 큐잉 시스템이 반드시 필요한 상황이 아님

### ❌ Kafka
- Kafka는 대규모 분산 시스템에 적합함 하지만 이 기능처럼 간단한 이벤트 처리에 사용하면 오히려 러닝 커브가 크고 오버엔지니어링 소지가 큼
- 로그 기반의 이벤트 저장이나 재처리 기능은 현재 요구사항과 맞지 않음

<br>

## ✅ 적용 결과 및 개선 효과

- 서비스 로직과 알림 기능을 명확히 분리하여 **관심사의 분리** 달성
- 로직 변경 없이 알림 대상 조건이나 방식을 확장 가능
- 테스트 시에도 ItemRestockedEvent 만 발행하면 동작 검증이 가능함 이로 인해 유지보수 용이성 향상

<br>

</details>

	• 문제:
		• 품절 상품이 재입고될 때 찜한 사용자에게 빠른 이메일 알림 필요
		• 서비스 간 복잡한 연동 없이 간단한 구조 유지가 요구됨
	• 해결 방법:
		• @EventListener + ApplicationEventPublisher 기반의 비동기 이벤트 방식 선택
		• ItemRestockedEvent 클래스 설계 및 비즈니스 로직 분리
		• @Async 리스너를 활용해 알림 처리 비동기처리 및 느슨한 결합 구현
	• 성과:
		• 서비스 로직과 알림 로직 분리 → 유지보수 및 확장성 향상
		• 외부 메시지 브로커 없이도 요구사항 충족
		• 테스트 시에도 이벤트만 발행하면 기능 검증 가능 → 테스트 용이성 확보

<br>
