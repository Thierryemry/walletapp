📘 Wallet API – README

Bu proje dijital ödeme şirketleri için geliştirilmiş basit bir Cüzdan Yönetim Sistemi (Wallet API) uygulamasıdır.
Spring Boot + Spring Security (Basic Auth) + H2 Database kullanır.

Uygulama hem müşterilerin hem de çalışanların cüzdan işlemlerini yönetmesine izin verir.

🧱 Temel Özellikler

Customer → Kendi cüzdanları üzerinde işlem yapabilir

Employee → Tüm müşteriler adına her işlemi yapabilir

Roller:

CUSTOMER

EMPLOYEE

İşlemler:

Cüzdan oluşturma

Cüzdan listeleme

Para yatırma (deposit)

Para çekme (withdraw)

İşlem listeleme

İşlem onay/red (approve/deny)

Para yatırma/çekme işlemleri mantığı:

1000₺ üzeri → PENDING

1000₺ ve altı → APPROVED

APPROVED ise hem balance hem usableBalance güncellenir

PENDING ise yalnızca ilgili balance güncellenir

Tüm işlemler veritabanına kaydedilir.

🛠️ Teknolojiler
Teknoloji	Açıklama
Spring Boot 3	Ana uygulama çatısı
Spring Security	Basic Auth & Rol bazlı yetkilendirme
H2 Database	Hafif, embed DB (test & deployment)
JPA / Hibernate	ORM yapısı
Lombok	Boilerplate kod azaltma
👥 Seed Kullanıcılar

Uygulama ayağa kalktığında otomatik olarak 3 kullanıcı oluşturulur:

Username	Password	Role
customer1	customer123	CUSTOMER
customer2	customer123	CUSTOMER
employee1	employee123	EMPLOYEE

Tümü Basic Auth ile erişilebilir.

🗄️ H2 Console

Tarayıcıdan:

http://localhost:8080/h2-console


JDBC URL:

jdbc:h2:mem:testdb

📌 API Endpointleri
1️⃣ Create Wallet

POST /api/wallets

Request Body
{
  "walletName": "Main Wallet",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true
}

Açıklama:

Customer kendi adına cüzdan oluşturur

Employee herhangi bir customer için cüzdan oluşturabilir:
POST /api/wallets?customerId=2

2️⃣ List Wallets

GET /api/wallets

Customer → yalnızca kendi cüzdanları

Employee → tüm cüzdanlar

3️⃣ Deposit

POST /api/wallets/{walletId}/deposit

Request Body
{
  "amount": 750,
  "oppositePartyType": "IBAN",
  "source": "TR12000678901234567890"
}

4️⃣ Withdraw

POST /api/wallets/{walletId}/withdraw

Request Body
{
  "amount": 600,
  "oppositePartyType": "PAYMENT",
  "destination": "PAYMENT_5566"
}


Çekim kuralları:

Cüzdan activeForWithdraw = true olmalı

Kullanılabilir bakiye yeterli olmalı

5️⃣ List Transactions

GET /api/wallets/{walletId}/transactions

6️⃣ Approve / Deny Transaction

POST /api/transactions/{transactionId}/approve

Request Body
{
  "status": "APPROVED"
}

Not:

Sadece EMPLOYEE yapabilir.

🔐 Rol Bazlı Erişim Kuralları
Endpoint	CUSTOMER	EMPLOYEE
Create Wallet	✔ kendi adına	✔ tüm kullanıcılar adına
List Wallets	✔ kendi	✔ herkes
Deposit	✔ kendi	✔ herkes
Withdraw	✔ kendi	✔ herkes
List Transactions	✔ kendi	✔ herkes
Approve Transaction	❌	✔
🔧 Projeyi Çalıştırma
1. Build
mvn clean install

2. Run
mvn spring-boot:run

🧪 POSTMAN Örnekleri
Deposit örneği:
POST http://localhost:8080/api/wallets/1/deposit
Basic Auth: customer2 / customer123


Body:

{
  "amount": 750,
  "source": "TR12000678901234567890",
  "oppositePartyType": "IBAN"
}

Withdraw örneği:
POST http://localhost:8080/api/wallets/1/withdraw
Basic Auth: customer1 / customer123


Body:

{
  "amount": 500,
  "oppositePartyType": "PAYMENT",
  "destination": "PAY_44"
}

Approve örneği (EMPLOYEE):
POST http://localhost:8080/api/transactions/4/approve
Basic Auth: employee1 / employee123


Body:

{
  "status": "APPROVED"
}

🧨 Hata Yönetimi

Global Exception Handler şunları döner:

Durum	Response
Wallet bulunamadı	404 NOT FOUND
Unauthorized işlem	403 FORBIDDEN
Bakiye yetersiz	400 BAD REQUEST
Validation hatası	400 BAD REQUEST
🎯 Sonuç

Bu API müşteriler ve çalışanlar arasındaki yetki modelini destekleyen,
deposit/withdraw/approve gibi finansal iş kurallarını tam uygulayan,
deployment-ready bir Wallet Backend servisidir.
