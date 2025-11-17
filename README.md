**Wallet Management API**

This Spring Boot-based project provides a digital wallet management system authorized by customer and employee roles. The application is secured with Basic Authentication and uses the H2 in-memory database. The goal is to create multiple wallet management, role-based access, secure transaction flow, and a simple yet extensible backend infrastructure.

## 📌 Features

### 🔐 Security and Authentication

- Basic Authentication
- Role-Based Access Control (RBAC)
- CUSTOMER → for only own wallets
- EMPLOYEE → for all customers authorized to perform transactions

### 💼 Wallet Management

- Customer can create a wallet on his own behalf
- Employee can create a wallet on behalf of any customer
- A customer can have more than one wallet
- Employees can list all wallets
- Customer can deposit money, withdraw money, and list transaction history.

### 🧱 Layered Architecture

- Controller
- Service (Interface + Implementation)
- Repository
- Config
- Exception
- DTO
- Model (Entity)
- Others

### 🧪 Global Exception Handling

- Unauthorized access → 403
- Resource Not Found → 404
- Transaction error → 500

### 🌱 Application Startup Data (DataSeeder)

When the application is first opened, role-based users are automatically added.

## 🗂 Users at the beginning

| **Role** | **Username** | **Password** |
| --- | --- | --- |
| EMPLOYEE | employee1 | 12345 |
| CUSTOMER | customer1 | 12345 |
| CUSTOMER | customer2 | 12345 |

**All requests must be made with Basic Auth.**

## 🚀 Setup and Running

### Requirements

- Java 17+
- Maven 3.9+
- Any IDE (IntelliJ, Eclipse, VS Code)

### Steps

#### 1\. Download Project

Gou can download the zip from Github or clone it with git clone

#### 2\. Install Maven Dependencies

They are installed automatically when IDE is started.

If you want to run via terminal:

- mvn clean install

#### 3\. Run the application

You can run directly on IDE or from terminal:

- mvn spring-boot:run

#### 4\. Application address

- <http://localhost:8080>

## 🔑 Use of API

### Verification

### Basic Auth is required on all endpoints

### Select Postman → Authorization → Basic Auth and enter your user information

### CUSTOMER authorities

- Creating own wallets (one or more)
- Listing all wallets behalf of him/her
- Deposit money to own wallets
- Withdraw monet from own wallets
- Listing all transactions belong to him/her

### EMPLOYEE authorities

- Creating wallet on behalf of any customer
- Listing wallets on behalf of any customer
- Doing everything a customer can do on behalf of that customer
- Approving or denying transactions for pending situations

### Exepcted Error Codes

- 401 → Wrong username/password
- 403 → No role authorization
- 404 → Resource not found
- 500 → Generic server error

## 🛠 H2 Database

### Access to console

- <http://localhost:8080/h2-console>

### JDBC info

- JDBC URL: jdbc:h2:mem:testdb
- User: sa
- Password: empty (default)

## 🧪 Running Test

Unit tests are written by JUnit + Mockito.

To run:

- IDE → "Run Tests"
- or terminal:
  - mvn test

## 🧱 Main structure of architecture

- Controller layer → handles requests and returns responses
- Service layer → includes business logic
- Repository layer → DB access
- Exception layer → Custom exceptions + global exception handler
- Config → application configurations and security procedures
- DTO and Entity layer → data models

## 📦 Deployment

Project can be deployed to any environment

### Jar olarak

- mvn clean package
- java -jar target/wallet-app.jar

**Project includes application.yml and application-test.yml specified for test environment**

**ENDPOINTS AND SAMPLE DATA**

**Authentication**

- **Basic Auth is used**
- Sample Users:
  - Customer: customer1 / customer123
  - Customer: customer2 / customer123
  - Employee: employee1 / employee123

**Create Wallet By Customer**

- **URL:** /wallets/create
- **Method:** POST
- **Auth:** CUSTOMER (kendi)
- **Body (JSON):**

{

"walletName": "MyWallet",

"currency": "TRY",

"activeForShopping": true,

"activeForWithdraw": true

}

**Create Wallet By Employee**

- **URL:** /wallets/create/{customerId}
- **Method:** POST
- **Auth:** EMPLOYEE (her cüzdan için)
- **PathVariable:** customerId
- **Body (JSON):**

{

"walletName": "MyWallet",

"currency": "TRY",

"activeForShopping": true,

"activeForWithdraw": true

}

**List Wallets By Customer**

- **URL:** /wallets/list
- **Method:** GET
- **Auth:** CUSTOMER (sadece kendi)

**List Wallets By Employee**

- **URL:** /wallets/list/{customerId}
- **Method:** GET
- **Auth:** EMPLOYEE (tüm cüzdanlar)
- **PathVariable:** customerId

**Deposit**

- **URL:** /transactions/wallet/{walletId}/deposit
- **Method:** POST
- **Auth:** CUSTOMER (own wallet) / EMPLOYEE (each wallet)
- **PathVariable:** walletId
- **Body (JSON):**

{

"amount": 500.00,

"source": "TR123456789"

}

**Withdraw**

- **URL:** /transactions/wallet/{walletId}/withdraw
- **Method:** POST
- **Auth:** CUSTOMER (own wallet) / EMPLOYEE (each wallet)
- **PathVariable:** walletId
- **Body (JSON):**

{

"amount": 200.00,

"destination": "TR987654321"

}

**List Transactions**

- **URL:** /transactions/wallet/{walletId}
- **Method:** GET
- **Auth:** CUSTOMER (only own) / EMPLOYEE (all)
- **Path variable:** walletId

**Approve / Deny Transaction**

- **URL:** /transactions/approveOrDeny
- **Method:** POST
- **Auth:** EMPLOYEE
- **Body (JSON):**

{

"transactionId": 10,

"status": "APPROVED"

}

**🔹 Notes**

- **Because the H2 Database is used, the application is seeded at every restart, ensuring test users are ready.**
- **If the amount exceeds 1000, it is recorded as PENDING; if it is below 1000, it is recorded as APPROVED when doing deposit and withdraw.**

## 🎯 Result

This project offers a simple yet powerful infrastructure built with multi-wallet management, role-based security, and a layered architecture.

It is suitable for future expansion and includes the core components of a real e-wallet system.
