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
    
    Point {
	    Long point_id PK
	    Users user_id FK
	    int point_balance
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
    Point ||--|| Users : "has one"
    Orders ||--|| IssuedCoupon : "has one"
~~~

<br>

## 🧬 Service Architecture

<img width="1400" src="https://github.com/user-attachments/assets/16301b92-5ccf-4105-8597-0762d299cee7"/>

<br>

## 🚨 Trouble Shooting

### 🔧 동시성 문제 (재고 감소 처리) 👉 [자세히 보기](https://github.com/Developer-Groo/Sparta-Store/wiki/%EC%9E%AC%EA%B3%A0-%EA%B0%90%EC%86%8C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%E2%80%90-%EB%B9%84%EA%B4%80%EC%A0%81-%EB%9D%BD-%EC%A0%81%EC%9A%A9-%EC%9D%B4%EC%9C%A0%EC%99%80-%EA%B2%B0%EA%B3%BC)

- **문제: 동시에 여러 주문 요청이 들어올 경우 재고가 음수가 되는 동시성 문제 발생**    
- **해결: @Lock(PESSIMISTIC_WRITE) 적용하여 트랜잭션 단위로 락 제어**    
- **성과: 재고 정확도 100% 확보 및 데이터 무결성 보장**

### 🧪 테스트 코드 트랜잭션 롤백 실패 👉 [자세히 보기](https://github.com/Developer-Groo/Sparta-Store/wiki/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%E2%80%90-@Transactional-%EC%82%AC%EC%9A%A9-%EC%8B%9C-1%EC%B0%A8-%EC%BA%90%EC%8B%9C%EC%99%80-rollback%E2%80%90only-%EC%B2%98%EB%A6%AC)

- **문제: @Transactional 이 붙은 테스트 메서드에서 rollbac k이 안되는 것처럼 보이는 현상 발생**
- **해결: 테스트 함수 트랜잭션 제거 → 내부 트랜잭션 rollback 여부 정확히 검증 가능**
- **성과: 트랜잭션 흐름 및 rollback-only 마킹 이해도 상승**

### ⚙️ Dockerfile 빌드 최적화 👉 [자세히 보기](https://github.com/Developer-Groo/Sparta-Store/wiki/Docker-file-%EB%B9%8C%EB%93%9C-%EC%B5%9C%EC%A0%81%ED%99%94-%E2%80%90-%EB%B9%8C%EB%93%9C-%EC%86%8D%EB%8F%84-%EB%B0%8F-%EC%9A%A9%EB%9F%89-%EC%B5%9C%EC%A0%81%ED%99%94)

- **문제: 이미지 용량이 크고 빌드 시간이 길어 개발/배포 시 비효율적**
- **해결: slim 베이스 이미지 + multi-stage build + 캐시 레이어 분리**
- **성과: 이미지 용량 122MB 감소 및 빌드 속도 26.9s → 1.9s (93% 개선)**

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

- **문제: ECR Pull 시 NAT Gateway 를 경유해 불필요한 네트워크 요금 발생**    
- **해결: Interface Endpoint 구성 → AWS 내부망으로 통신 경로 전환**
- **성과: 트래픽 비용 감소 및 보안성 향상**

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

- **문제: 재입고 알림을 어떤 방식으로 처리할지 기술 선택 필요**
- **해결: 서비스 복잡도와 트래픽 규모를 고려해 Spring 이벤트 리스너 사용**
- **성과: 간결한 구현, 유지보수성 향상, 향후 비동기 확장 가능**

<br>
