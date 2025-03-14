# 💻 Sparta Store
**Sparta Store 는 자사몰 플랫폼으로, 상품 판매 및 주문, 리뷰 관리, 사용자 맞춤형 알림 서비스 등의 기능을 제공하는 백엔드 프로젝트입니다**

## ☑️ Index
- [🏁 Team](#-Team)
- [🤔 Team Document](#-Team-Document)
- [📑 Commit Convention](#-Commit-Convention)
- [🛠 Technology](#-Technology)
- [🎯 Features](#-Features)
- [🔗 ERD](#-ERD)
- [🚨 Trouble Shooting](#-Trouble-Shooting)
- [🍰 Performance Comparison](#-Performance-Comparison)

<br>

## 🏁 Team
|**👑 Leader**|**Contributions**|
|:--------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/09c03daf-fed7-4bbd-adf6-8909ef950606" width="150" height="150">| <div align="left"> • 전체 개발 계획 수립 및 설계 <br> • 코드 리뷰를 통한 팀의 품질 및 생산성을 향상 <br> • Spring Boot, JPA 기반으로 상품 및 리뷰 관리, 카테고리 기능 개발 <br> • 품절 상품 재입고 시 이메일 알림 기능 구현 <br> • 비관적 Lock 을 활용해 상품 재고 감소 동시성 이슈 해결 <br> • GitHub Actions를 활용해 자동화된 빌드, 테스트, 배포 파이프라인 구축 <br> • AWS 인프라 설계 및 구축, EC2, RDS, ElastiCache, NAT Gateway, ALB 등을 활용한 서비스 운영 <br> • 블루/그린 무중단 배포를 적용하여 배포 시 서비스 다운타임 최소화 </div>|
|**김우현**|[GitHub Link](https://github.com/Developer-Groo)|

|**👑 Sub Leader**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/bc4b03e5-c31d-4144-b384-b1607a7f544b" width="150" height="150">| <div align="left"> • Spring Boot, JPA 기반으로 • 장바구니, 찜 기능 개발 </div>|
|**윤주영**|[GitHub Link](http://github.com/ju-young0)|

|**🤴🏻 Member**|**Contributions**|
|:------------:|:---------------:|
|<img src="https://github.com/user-attachments/assets/98ad61fe-84ad-4012-8294-76d23c0f1eeb" width="150" height="150">| <div align="left"> • Spring Boot, JPA 기반으로 주문 및 결제 기능 개발 </div>|
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

- Team Notion 보러가기 👉 [Team Notion](https://teamsparta.notion.site/6-1962dc3ef51480cb9e89f20d215f802c?pvs=25)
- Team Brochure 보러가기 👉 [Team Brochure](https://www.notion.so/teamsparta/6-Sparta-Store-1b62dc3ef51480bebfc3e4fb16d38ad7?pvs=4)

<br>

## 📑 Commit Convention

**`feat`** : 새로운 기능 추가

**`fix`** : bug fix

**`docs`**  : 문서 수정

**`style`** : 세미콜론 같은 코드의 사소한 스타일 변화.

**`refactor`** : 변수명 수정같은 리팩터링

**`test`** : 테스트 코드 추가 & 수정

**`chore`** : 중요하지 않은 일

<br>

## 🛠 Technology
| **분야**        | **기술** |
|--------------|--------|
| **Backend** | Java 17, Spring Boot 3.x, JPA, QueryDSL |
| **DB** | MySQL 8.0 |
| **Cache** | Spring Cache, Redis Cache |
| **Concurrency Control** | Redis Lock |
| **Testing** | JUnit5, MockMvc |
| **DevOps** | Docker |

<br>

## 🎯 Features


<br>

## 🔗 ERD

~~~ mermaid
erDiagram
    User {
        Long id PK
        String name
        String email
        String password
        Address address
        tinyint is_deleted
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
        Long id PK
        User user_id FK
        OrderStatus status
        Integer totalPrice
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    OrderItem {
        Long id PK
        Orders order_id FK 
        Item item_id FK
        Integer orderPrice
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
        Long id PK
        User user_id FK
        LocalDateTime created_at
        LocalDateTime updated_at
    }
    
    REVIEW {
        Long id PK
        User user_id FK
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
	    Long id PK
	    User user_id FK
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

    User ||--o{ Orders : "has many"
    User ||--|| Cart : "has one"
    Cart ||--o{ CartItem : "contains"
    Item ||--o{ CartItem : "is in"
    Orders ||--o{ OrderItem : "has many"
    Item ||--o{ OrderItem : "has many"
    User }|..|{ Address : "embedded"
    REVIEW }o--|| User : "has many"
    REVIEW }o--|| Item : "has many"
    Category ||--o| Category : "parent"
    Category ||--|{ Category : "children"
    Category ||--|{ Item : "has"
    User ||--o{ Likes : "likes"
    Item ||--o{ Likes : "liked by"
    SalesSummary ||--|| Item : "has one" 
~~~

<br>


## 🚨 Trouble Shooting

✅ solution 







