# 💻 Sparta Store
**Sparta Store 는 자사몰 플랫폼으로, 상품 판매 및 주문, 리뷰 관리, 사용자 맞춤형 알림 서비스 등의 기능을 제공하는 백엔드 프로젝트입니다**

## ☑️ Index
- [🏁 Team](#-Team)
- [📑 Commit Convention](#-Commit-Convention)
- [🛠 Technology](#-Technology)
- [🎯 Features](#-Features)
- [🔗 ERD](#-ERD)
- [🚨 Trouble Shooting](#-Trouble-Shooting)
- [🍰 Performance Comparison](#-Performance-Comparison)

<br>

## 🏁 Team
|**우현**|**주영**|**수연**|**진홍**|**현우**|
|-------|-------|-------|-------|-------|
|<img src="https://github.com/Developer-Nova/Sec19-Local-Data-Persistance_ByAngela/assets/123448121/17a2ba3b-a618-4ac8-93b9-0d0e02c19c78" width="110" height="110">|||||
|[GitHub](https://github.com/Developer-Groo)|[GitHub]()|[GitHub]()|[GitHub]()|[GitHub]()|

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
        Integer price
        String description
        Integer stockQuantity
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    Orders {
        Long id PK
        User user FK
        OrderStatus status
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    OrderItem {
        Long id PK
        Orders order FK 
        Item item FK
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
        Cart cart FK
        Item item FK
        Integer quantity
        LocalDateTime created_at
        LocalDateTime updated_at
    }
    
    Cart {
        Long id PK
        User user FK
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
~~~

<br>


## 🚨 Trouble Shooting

✅ solution 







