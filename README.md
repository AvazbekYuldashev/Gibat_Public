# GIBAT – Secure User Registration & Profile Management API

**GIBAT** is a backend REST API built with Spring Boot that provides secure user registration, email verification, JWT-based authentication, and profile management. The project also includes error handling, language internationalization (i18n), and a modular code structure suitable for real-world applications.

---

## 🔧 Technologies Used

- Java 17
- Spring Boot
- Spring Security with JWT
- Maven
- PostgreSQL
- Swagger (OpenAPI 3)
- Lombok
- SMTP Email Integration
- Internationalization (i18n)

---

## 📁 Project Structure

src/main/java/api/gibat/uz/
├── app/ # Configuration, DTOs, utilities
├── email/ # Email sending, history logging
├── exception/ # Custom exceptions and global handler
├── jwt/ # JWT generation, validation, filter
├── post/ # Sample Post entity and API
├── profile/ # Registration, login, email confirmation
└── GibatApplication.java # Main Spring Boot entry point


---

## 🚀 Getting Started

### Prerequisites:
- Java 23+
- Maven
- PostgreSQL

🔐 Security & Authentication
Secure login using JWT (JSON Web Token)

Token validation and authentication filter

Email confirmation is required before full account activation

Attempt limits for code verification to prevent brute-force attacks

✉️ Email Functionality
Emails are sent via SMTP (customizable)

Email history is stored in the database

Email types are managed via the EmailType enum

🌍 Language Support (i18n)
Language support for multiple locales

Error messages and system responses are dynamically translated based on selected language (e.g., AppLanguage enum)

📘 API Documentation
Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html

This interface documents all available endpoints with request/response examples.

✅ Features Summary
✅ User Registration

✅ Email Verification

✅ JWT Authentication

✅ Profile Management

✅ Role-based status checking

✅ Custom Exception Handling

✅ Fully RESTful API structure

✅ Multilingual Support (i18n)


👨‍💻 Author
Avazbek Yuldashev
Backend Developer – Java | Spring Boot
📍 Andijan, Uzbekistan
📧 


