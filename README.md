# Journal App — Spring Boot Journaling Backend

A REST API backend for managing user accounts and personal journal entries. The application uses Spring Boot, MongoDB, Spring Security, and JWT authentication, with Redis-backed weather-response caching and a scheduled Kafka-based sentiment notification workflow.

> **Security and scope note:** The current source code does not implement end-to-end encryption (E2EE). Journal entry fields are stored in MongoDB through the application data model. The repository name/description should not be interpreted as proof of E2EE.

## Features verified in the source

### Implemented
- User signup and login endpoints.
- Stateless JWT authentication using a custom request filter.
- Password hashing with BCrypt.
- Role-based access control for admin endpoints.
- User-scoped journal entry create, read, update, and delete operations.
- MongoDB persistence for users and journal entries.
- Redis caching for external weather responses, with a 300-second TTL.
- Scheduled selection of users who opted into sentiment analysis and have a valid email address.
- Weekly sentiment aggregation over journal entries from the preceding seven days.
- Kafka producer/consumer flow that sends sentiment notification data to an email service.
- Scheduled application-cache refresh.

### Integrations present in code
| Component | How it is used |
|---|---|
| MongoDB | Persists users, journal entries, and application configuration |
| Redis | Caches weather API responses |
| Apache Kafka | Carries sentiment notification events |
| Spring Mail | Sends sentiment notification emails |
| External weather API | Supplies weather data for the authenticated user greeting |
| SonarQube Maven plugin | Makes Maven-based code-quality analysis available |

Dependencies or configuration alone are not proof of a production deployment. The integrations above are listed because corresponding application code is present; successful operation still depends on valid external service configuration.

## Technology stack

- **Language:** Java 17
- **Framework:** Spring Boot 2.7.16, Spring Web
- **Security:** Spring Security, JWT (JJWT), BCrypt
- **Data:** Spring Data MongoDB
- **Caching:** Spring Data Redis
- **Messaging:** Spring Kafka
- **Email:** Spring Boot Starter Mail
- **Testing:** JUnit 5, Spring Boot Test
- **Build:** Maven
- **Code quality:** SonarQube Maven plugin
- **Utilities:** Lombok, Jackson, RestTemplate

## Architecture

The application uses a layered Spring backend. The primary request path is:

~~~mermaid
flowchart TD
    A[Client / Postman] --> B[JWT Filter]
    B --> C[Spring Security]
    C --> D[REST Controllers]
    D --> E[Service Layer]
    E --> F[Repository Layer]
    F --> G[(MongoDB)]
    E --> H[Redis Service]
    H --> I[(Redis Cache)]
    E --> J[External Weather API]
    K[Scheduled Sentiment Job] --> L[Kafka Topic: weekly_sentiments]
    L --> M[Sentiment Consumer]
    M --> N[Email Service]
    N --> O[SMTP Provider]
~~~

The scheduled sentiment workflow selects opted-in users, aggregates the most frequent non-null sentiment from entries in the previous seven days, publishes a message to the weekly_sentiments Kafka topic, and has a Kafka consumer pass the message to the email service.

## API documentation

See [API Documentation](docs/API.md) for the endpoint list, access requirements, and example request bodies.

### Main endpoint groups

| Group | Base path | Purpose |
|---|---|---|
| Public | /public | Health check, signup, login |
| Journal | /journal | Authenticated user's journal entries |
| User | /user | Authenticated user's account operations and greeting |
| Admin | /admin | Role-protected administrative operations |

## Project structure

~~~text
src/
├── main/
│   ├── java/net/engineeringdigest/journalApp/
│   │   ├── cache/          # Application cache
│   │   ├── config/         # Spring Security and Redis configuration
│   │   ├── constants/      # Application constants
│   │   ├── controller/     # REST controllers
│   │   ├── entity/         # MongoDB entities
│   │   ├── enums/          # Sentiment enum
│   │   ├── filter/         # JWT request filter
│   │   ├── model/          # Kafka message model
│   │   ├── Repository/     # MongoDB repositories
│   │   ├── schedular/      # Scheduled jobs
│   │   ├── Service/        # Business and integration services
│   │   └── utils/          # JWT utilities
│   └── resources/
└── test/
    └── java/               # Unit and Spring Boot test classes
~~~

## Getting started

### Prerequisites
- JDK 17
- MongoDB
- Redis
- Apache Kafka (for the sentiment notification workflow)
- SMTP credentials (for email notifications)
- Weather API key (for the weather integration)

### Clone

~~~bash
git clone https://github.com/jayendrasingh22/Journal-App.git
cd Journal-App
~~~

### Configure services and secrets

Configure the MongoDB, Redis, Kafka, SMTP, and weather API settings for your local environment. The weather service reads the weather.api.key property.

**Do not commit credentials, API keys, or JWT signing secrets.** The current source contains a hardcoded JWT signing secret in JwtUtil; it must be moved to external configuration and rotated before this repository is used beyond local development.

### Run

Windows:

~~~powershell
.\mvnw.cmd spring-boot:run
~~~

Linux/macOS:

~~~bash
./mvnw spring-boot:run
~~~

The application uses the Spring Boot default port unless overridden by configuration.

## Tests and code quality

Run the test suite:

~~~bash
./mvnw test
~~~

On Windows, use mvnw.cmd test.

The repository contains JUnit/Spring Boot test classes. Some test classes or methods are disabled, and the email test currently lacks the Spring Boot test-context annotation. Treat the existing tests as work in progress rather than evidence of comprehensive automated coverage.

The Maven build includes the SonarQube scanner plugin. Configure your SonarQube server and project properties before running an analysis.

## Known limitations and recommended next steps

These are review findings and suggested improvements, not claims that the work is already complete:

1. Remove the hardcoded JWT signing secret, rotate it, and load the replacement from environment-backed configuration.
2. Remove the hardcoded admin response header in the JWT filter.
3. Implement and test actual encryption before describing the project as E2EE.
4. Fix and test journal update behavior; the current update controller assigns the content field twice.
5. Add request validation and consistent error responses.
6. Enable and expand automated tests, including authentication, authorization, journal ownership, Redis, and Kafka flows.
7. Add API examples and an exported Postman collection.

## Author

**Jayendra Singh**

- GitHub: [@jayendrasingh22](https://github.com/jayendrasingh22)
- Repository: [Journal-App](https://github.com/jayendrasingh22/Journal-App)
