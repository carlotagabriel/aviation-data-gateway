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

All AI-generated output served as a starting point. The code and documentation were subsequently reviewed, refactored where necessary, and extended.


Aqui está a tradução completa para o inglês, mantendo o tom técnico e formal do original:

---

# Scalability Strategy: Aviation Data Gateway

## 1. Overview

The current implementation of the Aviation Data Gateway is robust and resilient at the instance level, leveraging patterns such as Local Cache (Caffeine) and Circuit Breaker (Resilience4j). However, in order to scale horizontally (i.e., run multiple service instances) and maintain stability under high load, it is necessary to adopt distributed architecture patterns.

This document proposes a two-pillar architecture for the service evolution:

* **Distributed Cache with Redis**: To drastically improve performance and resilience.
* **Centralized Egress Gateway with Spring Cloud Gateway**: To intelligently manage communication with external services and centralize resilience rules.

---

## 2. Pillar 1: Distributed Cache with Redis

The first improvement is replacing the local cache (Caffeine) with a distributed and shared cache, such as Redis.

### 2.1. The Problem with Local Cache at Scale

The current Caffeine cache is extremely fast, but its memory is local to each service instance. If we have 10 running instances, we will have 10 independent caches. This leads to two problems:

* **Inefficiency**: If Instance A makes a call for ICAO "SBGR", only it will have that data cached. If the next request for "SBGR" hits Instance B, it will need to make a new call to the external API, resulting in unnecessary network calls.
* **Inconsistency**: Data may become desynchronized across instances.

### 2.2. The Solution: Centralized Cache

By introducing a Redis cluster, all service instances share a single centralized cache.

### 2.3. Benefits

* **Massive Performance**: Once an airport’s data (which rarely changes) is cached, no service instance will need to call the external API again, resulting in millisecond response times.
* **Increased Resilience**: If the external API becomes unavailable, our service can continue operating normally for all ICAO codes already cached. The perceived availability for the end user increases dramatically.
* **Cost Reduction**: Significantly fewer calls to the external API, which may reduce costs if the service is paid.

---

## 3. Pillar 2: Centralized Egress Gateway

To manage resilience in a coordinated way across multiple instances, the best practice is to centralize the external communication logic in an **Egress Gateway**, which can be implemented with Spring Cloud Gateway.

### 3.1. The Problem with Instance-Level Resilience

With Resilience4j configured at each instance, we have multiple independent Circuit Breakers and Retry policies. This creates serious problems at scale:

* **Retry Storm**: If the external API becomes slow, all 10 instances may start retrying simultaneously, multiplying the load and potentially taking down the external service entirely.
* **Thundering Herd**: When the external API recovers, the Circuit Breakers of all instances may try reconnecting at the same time, causing another load spike that could crash the service again.

### 3.2. The Solution: Centralize the Rules

The Egress Gateway becomes the **single point of contact** with the external API. All our service instances make internal calls to the gateway.

### 3.3. Benefits

* **Coordinated Resilience**: There is only one Circuit Breaker. If it opens, all instances are protected instantly. The “Thundering Herd” problem is eliminated.
* **Simplified Service**: Our AviationDataGateway can be simplified. Its responsibility is no longer managing network complexity, but focusing only on business logic (caching, mapping, etc.).
* **Centralized Security and Control**: The Egress Gateway is the ideal place to manage API Keys, enforce security policies (TLS), and implement centralized rate limiting.
* **Consistent Observability**: All external communication metrics (latency, errors, etc.) are generated and monitored in a single place.
