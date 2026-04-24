# Auth-Core Service

![Build Status](https://github.com/ByteEntropyCom/auth-core/actions/workflows/maven.yml/badge.svg)
![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot 3.4.1](https://img.shields.io/badge/Spring_Boot-3.4.1-brightgreen?style=flat-square&logo=springboot)
![Security](https://img.shields.io/badge/Security-JWT-blue?style=flat-square)
![Status](https://img.shields.io/badge/Aiven-Ready-purple?style=flat-square)

`auth-core` is a resilient Authentication Microservice built with **Spring Boot 3.4.1** and **Java 21**. It manages user identity, issues JWT tokens, and broadcasts login events via Kafka to the cloud.

## 🚀 Key Features

* **JWT Authentication**: Stateless security using `io.jsonwebtoken` (JJWT 0.12.6).
* **H2 Database**: Fast, in-memory user storage (`jdbc:h2:mem:authdb`).
* **Resilient Kafka Design**: The login flow is decoupled from Kafka. If the broker is unreachable or certificates are missing, the login still succeeds.
* **Aiven Cloud Ready**: Includes pre-configured (commented) templates for SSL-based Aiven connections.
* **Smart Configuration**: Uses Spring placeholders `${VAR:default}` to allow zero-config local starts while supporting cloud overrides.

## 🛠 Tech Stack

* **Java**: 21
* **Framework**: Spring Boot 3.4.1
* **Security**: Spring Security + JWT
* **Data**: Spring Data JPA + H2
* **Messaging**: Apache Kafka
* **Lombok**: For boilerplate-free code


## 📡 API Endpoints

```
Method	| Endpoint	| Access	| Description
--
POST	| /auth/login	| Public	| Authenticates user and returns a JWT
GET	| /h2-console	| Public	| Database management interface
ANY	| /**	| Authenticated	| All other routes require a Bearer Token
```


## ⚙️ Configuration Strategy

The application uses a **fail-fast, run-anywhere** strategy. It defaults to a local setup to ensure the app starts even without a Kafka cluster.

### Aiven Cloud Setup
To connect to Aiven, follow these steps:
1.  Place your `.p12` and `.jks` files in `src/main/resources/certs/`.
2.  Uncomment the **AIVEN CLOUD TEMPLATE** section in `application.properties`.
3.  Fill in your specific Aiven URL and Service Password.

### Environment Variable Overrides
| Variable | Default | Description |
| :--- | :--- | :--- |
| `KAFKA_URL` | `localhost:9092` | Cloud or local Kafka broker address |
| `KAFKA_PROTO` | `PLAINTEXT` | Set to `SSL` for Aiven cloud |
| `KAFKA_KEY_PASS` | (Empty) | Password for Aiven certificates |
| `JWT_SECRET` | (Hardcoded) | 256-bit secret key for JWT signing |

## 🏃 Running the Application

### Option 1: Local Development (Default)
Works immediately. If no Kafka is found, the app will log a warning but stay functional.

```bash
mvn spring-boot:run
```

### Option 2: Aiven Cloud (Secure Multi-line)

If you prefer not to edit the properties file, use environment variables:

```bash
KAFKA_URL=your-service.aivencloud.com:12345 \
KAFKA_PROTO=SSL \
KAFKA_KEY_PATH=file:$(pwd)/src/main/resources/certs/client.keystore.p12 \
KAFKA_KEY_PASS=your_password \
mvn spring-boot:run
```

## Test Login (cURL)

Use the following command to authenticate and receive a JWT:

````bash
curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "identifier": "admin@byteentropy.com",
           "secret": "password23" 
         }'
````

## Verify the Token

Copy the token string from the response and paste it into JWT.io to inspect the claims and validity. 

## 🔒 Security & Git
### Certificates: 
The .gitignore is configured to ignore *.p12, *.jks, and *.pem to prevent accidental leaks.

### Resilience: 
max.block.ms=2000 ensures that Kafka connection attempts never hang the login process for more than 2 seconds.

## 📡 Kafka Events
The service publishes login attempts to the auth-events topic. Success and Fail both.
