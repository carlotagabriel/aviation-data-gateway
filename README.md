## Download

[![Download Latest Build](https://img.shields.io/badge/Download-Latest%20Build-blue)](https://github.com/carlotagabriel/aviation-data-gateway/releases/latest/download/aviation-data-gateway-0.0.1-SNAPSHOT.jar)

# Aviation Data Gateway

A resilient Spring Boot gateway for fetching airport data from an external provider.

## Table of Contents

- [Key Features & Architecture](https://www.google.com/search?q=%23key-features--architecture)
- [Setup and Run](https://www.google.com/search?q=%23setup-and-run)
- [Testing](https://www.google.com/search?q=%23testing)

## Key Features & Architecture

* **Architecture**: Built using Clean Architecture principles to separate concerns into `domain`, `application`, and `adapter` layers.
* **Resilience**: Uses a Resilience4j `@CircuitBreaker` to handle external API failures and prevent cascading failures.
* **Caching**: Implements a Caffeine-based cache (`@Cacheable`) on the service layer to reduce latency and external calls.
* **Asynchronous I/O**: Leverages the reactive `WebClient` for non-blocking HTTP requests to the external provider.
* **Observability**: Exposes health, metrics, and other operational data through Spring Boot Actuator. A custom Micrometer timer (`aviation.api.latency`) tracks provider response times.

## Setup and Run

**1. Prerequisites:**

* Java 21
* Maven

**2. Configure:**

* Set the external provider URL in `src/main/resources/application.yml`:
  ```yaml
    aviation:
        api:
            base-url: https://api.aviationapi.com/v1
  ```

**3. Build & Run:**

* **Build the executable JAR:**
  ```bash
  mvn clean  install
  ```
* **Run the application:**
  ```bash
  java -jar target/aviation-data-gateway-0.0.1-SNAPSHOT.jar
  ```

## Testing

Execute the unit and integration tests using Maven:

```bash
mvn test
```

```bash
curl -X GET http://localhost:8080/api/v1/airports/ATL
```

```bash
curl -X GET http://localhost:8080/api/v1/airports/XXXX
```

### Notes on AI Usage

AI was used as a productivity tool to accelerate development in this project. Its use was focused on the following areas:

* **Boilerplate Code Generation:** AI assisted in creating code with predictable structures, such as DTOs, mappers between layers, and initial configuration files.

* **Project and Test Scaffolding:** It was used to suggest the initial package structure and to generate skeletons for unit and integration tests, including basic happy path and error scenarios.

* **Code Implementation:** Beyond initial scaffolding, AI also provided suggestions for implementing specific logic and methods in various parts of the code.

* **Documentation:** AI generated preliminary versions of documentation, such as the `README.md` and code comments, which served as a baseline.

All AI-generated output served as a starting point. The code and documentation were subsequently reviewed, refactored where necessary, and extended. Furthermore, other parts of the project were developed entirely manually. The final architecture, business logic, and code quality are the result of this developer-led process.
