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
|<img src="https://github.com/user-attachments/assets/09c03daf-fed7-4bbd-adf6-8909ef950606" width="150" height="150">| <div align="left"> • 전체 개발 계획 수립 및 설계 <br> • 코드 리뷰를 통한 팀의 품질 및 생산성을 향상 <br> • Spring Boot, JPA 기반으로 상품 및 리뷰 관리, 카테고리 기능 개발 <br> • 품절 상품 재입고 시 이메일 알림 기능 구현 <br> • 비관적 Lock 을 활용해 상품 재고 감소 동시성 이슈 해결 <br> • GitHub Actions를 활용해 자동화된 빌드, 테스트, 배포 파이프라인 구축 <br> • AWS 인프라 설계 및 구축, EC2, RDS, ElastiCache, ELB 등을 활용한 서비스 운영 <br> • 블루/그린 무중단 배포를 적용하여 배포 시 서비스 다운타임 최소화 </div>|
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

- Team Notion 보러가기 👉 [Team Notion]()
- Team Brochure 보러가기 👉 [Team Brochure]()
- Team Convention 보러가기 👉 [Team Convention](https://github.com/Developer-Groo/Sparta-Store/wiki/Team-Convention)

<br>

## 🛠 Technology
| **분야**        | **기술** |
|--------------|--------|
| **Backend** | Java 17, Spring Boot 3.x, Spring security 6.x, JPA, QueryDSL 등|
| **DB** | MySQL 8.0, Redis 7.2.7 |
| **Cache** | Redis Cache |
| **Concurrency Control** | Pessimistic Lock, Optimistic Lock |
| **Testing** | JUnit5, MockMvc |
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


## 🚨 Trouble Shooting

✅ solution 







