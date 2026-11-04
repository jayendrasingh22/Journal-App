# Journal App — Spring Boot Journaling Backend

A REST API for personal journal entries and account management. Built with Java 17, Spring Boot 2.7, MongoDB, Spring Security, JWT, Redis, Kafka, and Spring Mail.

> **Security scope:** This project does not currently implement end-to-end encryption (E2EE). Journal entry content is persisted by the backend in MongoDB.

## Features

### Core application
- User registration and password login with BCrypt.
- Stateless JWT authentication and role-based admin authorization.
- User-scoped journal CRUD operations.
- MongoDB persistence.
- Redis weather-response caching.
- Scheduled weekly sentiment aggregation, Kafka messaging, and email notification workflow.

### API and security improvements
- DTO-based request and response contracts; client requests cannot assign account roles or MongoDB IDs.
- Bean Validation for signup, login, profile, journal, and admin-create requests.
- Centralized JSON error responses for validation, authentication, and authorization failures.
- JWT signing secret loaded from environment configuration, not source code.
- Google OAuth 2.0 login through Spring Security OAuth2 Client. New verified Google identities can create accounts; an existing local account is never linked based on email alone.
- Swagger/OpenAPI documentation with HTTP Bearer JWT support.
- GitHub Actions Maven verification on pushes and pull requests; optional SonarCloud analysis.

## Technology stack

| Area | Technology |
|---|---|
| Language / framework | Java 17, Spring Boot 2.7.16 |
| API | Spring Web, Bean Validation |
| Security | Spring Security, JJWT 0.11.5, BCrypt, OAuth2 Client |
| API documentation | Springdoc OpenAPI 1.7 / Swagger UI |
| Persistence | Spring Data MongoDB |
| Cache | Spring Data Redis |
| Messaging / email | Spring Kafka, Spring Mail |
| Build / quality | Maven, Sonar Maven plugin, GitHub Actions |

## Architecture

~~~mermaid
flowchart TD
    A[Client] --> B[JWT Filter / OAuth2 Login]
    B --> C[Spring Security]
    C --> D[REST Controllers]
    D --> E[DTO Validation]
    E --> F[Service Layer]
    F --> G[Mongo Repositories]
    G --> H[(MongoDB)]
    F --> I[Redis Cache]
    J[Scheduled Sentiment Job] --> K[Kafka Topic]
    K --> L[Sentiment Consumer]
    L --> M[Email Service]
~~~

## Run locally

### Prerequisites
- JDK 17
- MongoDB
- Redis
- Apache Kafka and SMTP configuration for the sentiment email workflow
- Weather API key for the weather integration

### Clone

~~~bash
git clone https://github.com/jayendrasingh22/Journal-App.git
cd Journal-App
~~~

### Configure secrets

Set a fresh, random `JWT_SECRET` of at least 32 characters. The previously committed signing key must be considered compromised and rotated; this invalidates JWTs signed with that key.

PowerShell:

~~~powershell
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
~~~

To enable Google login, also set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`. Configure the Google OAuth redirect URI as:

~~~text
http://localhost:8080/login/oauth2/code/google
~~~

Detailed setup: [Security and Google OAuth](docs/SECURITY_AND_OAUTH.md).

Configure MongoDB, Redis, Kafka, SMTP, and weather API settings for your environment. Never commit credentials or signing keys.

### Start

Windows:

~~~powershell
.\mvnw.cmd spring-boot:run
~~~

Linux/macOS:

~~~bash
./mvnw spring-boot:run
~~~

## API documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Endpoint reference: [docs/API.md](docs/API.md)

Use Swagger's **Authorize** control with `Bearer <JWT>`.

## Authentication endpoints

- `POST /public/signup` — accepts `userName`, `email`, and `password`; roles are assigned by the server.
- `POST /public/login` — accepts `userName` and `password`; returns an object containing `accessToken`.
- `GET /oauth2/authorization/google` — starts Google OAuth. A new verified Google identity creates a local USER account. Existing local accounts are not automatically linked by email.

## Tests and CI

Run locally:

~~~bash
./mvnw test
~~~

GitHub Actions runs `./mvnw verify` on pushes and pull requests targeting `master`. Add a repository secret named `SONAR_TOKEN` to enable the optional SonarCloud analysis step.

A successful build does not prove that MongoDB, Redis, Kafka, SMTP, or Google OAuth credentials are configured or that external integrations work in production. Expand integration tests for those paths before deployment.

## Known scope limitations

- E2EE is not implemented.
- Existing local accounts cannot yet link a Google identity; the callback intentionally refuses automatic email-based linking. A separate authenticated linking flow remains future work.
- Profile updates do not change passwords. Add a dedicated password-change flow with current-password verification before supporting password changes.
- External service integration tests and production deployment hardening remain necessary.

## Author

**Jayendra Singh** — [GitHub](https://github.com/jayendrasingh22)
