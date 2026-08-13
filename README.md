# AI-Powered Fitness Tracking System

A full-stack, microservices-based fitness tracking application that allows users to securely authenticate, record workout activities, view their activity history, and receive AI-generated fitness recommendations based on their workout data.

The system is built using **Java, Spring Boot, Spring Cloud, Keycloak, RabbitMQ, PostgreSQL, MongoDB, Docker, React, and Google Gemini API**.

---

## Table of Contents

* [Overview](#overview)
* [Problem Statement](#problem-statement)
* [Key Features](#key-features)
* [Architecture](#architecture)
* [System Flow](#system-flow)
* [Backend Architecture](#backend-architecture)
* [Frontend Architecture](#frontend-architecture)
* [Microservices](#microservices)
* [Authentication and Authorization](#authentication-and-authorization)
* [User Synchronization](#user-synchronization)
* [Asynchronous Communication](#asynchronous-communication)
* [AI Recommendation Flow](#ai-recommendation-flow)
* [Database Design](#database-design)
* [Technology Stack](#technology-stack)
* [Project Structure](#project-structure)
* [Prerequisites](#prerequisites)
* [Configuration](#configuration)
* [Running the Project](#running-the-project)
* [Frontend Setup](#frontend-setup)
* [Backend API Endpoints](#backend-api-endpoints)
* [Example Application Flow](#example-application-flow)
* [Security Considerations](#security-considerations)
* [Future Improvements](#future-improvements)
* [Author](#author)

---

# Overview

The **AI-Powered Fitness Tracking System** is designed as a distributed fitness platform where different responsibilities are handled by independent microservices.

The application allows an authenticated user to:

1. Log in through Keycloak.
2. Receive an OAuth2/JWT access token.
3. Access protected APIs through the API Gateway.
4. Record fitness activities.
5. View previously recorded activities.
6. Open an individual activity.
7. Receive AI-generated analysis and recommendations.
8. Store and retrieve fitness-related information securely.

The backend follows a microservices architecture to separate user management, activity tracking, and AI recommendation processing.

---

# Problem Statement

Traditional fitness tracking applications often focus primarily on storing workout information such as:

* Activity type
* Duration
* Calories burned
* Workout history

This project extends the traditional tracking model by introducing an AI-powered recommendation layer.

The system processes workout information and sends relevant activity data to an AI service. The AI service uses the **Google Gemini API** to generate personalized insights such as:

* Workout analysis
* Areas for improvement
* Suggested workouts
* Safety guidelines

The project also addresses several backend engineering challenges:

* Secure authentication across distributed services
* Service-to-service communication
* Asynchronous processing
* User synchronization between an identity provider and application database
* Polyglot persistence
* Service discovery
* Containerized infrastructure

---

# Key Features

## Authentication

* Keycloak-based identity management
* OAuth2 authorization
* JWT access tokens
* Authorization Code with PKCE
* Protected API Gateway
* Token validation using Keycloak's JWKS endpoint

## User Management

* User registration
* User profile management
* Keycloak user ID synchronization
* Backend user validation
* Extended user information stored separately from identity management

## Activity Tracking

Users can record activities such as:

* Running
* Walking
* Cycling
* Swimming
* Rowing
* Hiking
* Weightlifting
* Deadlift
* Squats
* Bench Press
* Pull-ups
* Push-ups
* Yoga
* Pilates
* Meditation
* Football
* Cricket
* Badminton
* Dancing
* Zumba
* Martial Arts

Each activity can contain information such as:

* Activity type
* Duration
* Calories burned
* Additional metrics
* Start time

## Activity History

Authenticated users can:

* View their activities
* Select an individual activity
* View detailed activity information
* View AI-generated recommendations associated with the activity

## AI Recommendations

The system uses Google Gemini to generate recommendations based on workout information.

The generated response can contain:

* Overall analysis
* Workout improvements
* Suggestions
* Safety guidelines

## Asynchronous Processing

RabbitMQ is used to decouple activity tracking from AI processing.

When an activity is recorded:

```text
Activity Service
      |
      | Publish Activity Event
      v
   RabbitMQ
      |
      | Consume Event
      v
   AI Service
      |
      | Gemini API
      v
AI Recommendation
      |
      v
   MongoDB
```

## Service Discovery

Eureka is used as a service registry so that microservices can discover each other without relying on hardcoded service locations.

## API Gateway

All externally accessible backend requests go through the API Gateway.

The gateway handles:

* Routing
* Authentication
* JWT validation
* CORS
* User synchronization
* Forwarding requests to internal services

## Containerization

The backend infrastructure and services are containerized using Docker.

This makes it easier to run the distributed system with consistent environments.

---

# Architecture

```text
                         ┌──────────────────────┐
                         │     React Frontend   │
                         │      Vite + MUI      │
                         └──────────┬───────────┘
                                    │
                                    │ OAuth2 / PKCE
                                    ▼
                         ┌──────────────────────┐
                         │       Keycloak       │
                         │  Identity Provider   │
                         └──────────┬───────────┘
                                    │
                                    │ JWT
                                    ▼
                         ┌──────────────────────┐
                         │     API Gateway      │
                         │ Spring Cloud Gateway │
                         │ Security + CORS      │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌────────────┐ ┌──────────────┐ ┌──────────────┐
             │   User     │ │   Activity   │ │      AI      │
             │  Service   │ │   Service    │ │   Service    │
             └─────┬──────┘ └──────┬───────┘ └──────┬───────┘
                   │               │                │
                   ▼               ▼                ▼
             PostgreSQL         MongoDB          MongoDB
                                   │
                                   │
                                   ▼
                              ┌───────────┐
                              │ RabbitMQ  │
                              └─────┬─────┘
                                    │
                                    ▼
                              ┌───────────┐
                              │ AI Service│
                              │  Gemini   │
                              └───────────┘

                     ┌────────────────────┐
                     │       Eureka       │
                     │   Service Registry │
                     └────────────────────┘
```

---

# System Flow

The complete application flow is:

```text
User
 │
 │ Login
 ▼
Keycloak
 │
 │ JWT Access Token
 ▼
React Frontend
 │
 │ Authorization: Bearer <JWT>
 ▼
API Gateway
 │
 │ Validate JWT
 │
 │ Extract Keycloak User ID
 ▼
User Synchronization
 │
 ├── User exists → Continue
 │
 └── User does not exist → Create backend user
 │
 ▼
Activity Service
 │
 ├── Save activity
 │
 └── Publish activity event
        │
        ▼
     RabbitMQ
        │
        ▼
     AI Service
        │
        ├── Process activity
        │
        ├── Call Gemini API
        │
        └── Store recommendation
                │
                ▼
             MongoDB
                │
                ▼
        React Activity Details
```

---

# Backend Architecture

The backend is divided into independent services.

```text
Backend
│
├── API Gateway
│
├── User Service
│
├── Activity Service
│
├── AI Service
│
├── Eureka Server
│
├── Keycloak
│
└── RabbitMQ
```

Each service has a specific responsibility.

This separation prevents one large backend application from becoming responsible for authentication, users, activities, and AI processing simultaneously.

---

# Microservices

## 1. API Gateway

The API Gateway acts as the single entry point for frontend requests.

Responsibilities:

* Route requests to microservices
* Validate JWT access tokens
* Integrate with Keycloak
* Configure CORS
* Synchronize Keycloak users with User Service
* Forward authenticated requests to downstream services

Example:

```text
Frontend
   |
   | GET /api/activities
   ▼
API Gateway
   |
   ▼
Activity Service
```

The gateway is the externally exposed backend endpoint, while the internal microservices are intended to remain behind the internal network/firewall.

---

## 2. User Service

The User Service manages application-specific user information.

It stores extended information about users while Keycloak remains responsible for authentication and identity management.

Example user information:

```text
id
keyCloakId
email
password
firstName
lastName
createdAt
updatedAt
```

The important architectural decision is the `keyCloakId`.

The Keycloak `sub` claim contains the unique Keycloak user ID:

```json
{
  "sub": "04939ea7-4520-4ea5-a462-651d850669f1"
}
```

The same identifier is stored in the User Service so that the identity managed by Keycloak can be linked to the application's user record.

---

## 3. Activity Service

The Activity Service manages fitness activities.

Example activity information:

```json
{
  "type": "RUNNING",
  "duration": 20,
  "caloriesBurned": 150,
  "additionalMetrics": {},
  "startTime": null
}
```

The authenticated user's Keycloak ID is passed from the API Gateway using:

```text
X-User-ID
```

The Activity Service uses this ID to associate activities with the correct user.

---

## 4. AI Service

The AI Service is responsible for generating fitness recommendations.

It receives activity information asynchronously through RabbitMQ.

The service sends relevant workout information to Google Gemini and stores the generated recommendation.

A recommendation can contain:

```text
Analysis
Improvements
Suggestions
Safety Guidelines
```

MongoDB is used to store the recommendation-oriented data.

---

# Authentication and Authorization

The project uses **Keycloak as the Identity Provider**.

The frontend uses the OAuth2 Authorization Code flow with PKCE.

## Authentication Flow

```text
React
  |
  | Login
  ▼
Keycloak
  |
  | Authenticate user
  ▼
Authorization Code
  |
  ▼
React
  |
  | PKCE token exchange
  ▼
Keycloak
  |
  | Access Token
  ▼
React
```

The access token is a JWT.

The frontend sends it with API requests:

```http
Authorization: Bearer <access-token>
```

The API Gateway validates the JWT using Keycloak's JWKS endpoint.

Example configuration:

```properties
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/certs
```

The JWKS endpoint provides the public keys required by the resource server to validate JWT signatures.

---

# User Synchronization

A major part of the architecture is synchronization between Keycloak and the application's User Service.

Keycloak is responsible for:

```text
Authentication
Identity
Credentials
Access Tokens
```

The User Service is responsible for:

```text
Application-specific user information
Fitness-related user information
Extended profile data
```

These are intentionally separated.

For example, the Keycloak JWT contains:

```json
{
  "sub": "04939ea7-4520-4ea5-a462-651d850669f1",
  "preferred_username": "user1",
  "given_name": "User1",
  "family_name": "Last Name 1",
  "email": "user1@gmail.com"
}
```

The API Gateway extracts information from the JWT.

For every authenticated request:

```text
1. Extract Keycloak user ID from JWT
             ↓
2. Ask User Service if user exists
             ↓
3. User exists?
       │
       ├── YES → Continue request
       │
       └── NO → Create user in User Service
             ↓
4. Forward request downstream
```

This approach ensures that when a new user is created in Keycloak and makes their first authenticated request, the backend application automatically creates the corresponding application user.

---

# Asynchronous Communication

RabbitMQ is used for communication between the Activity Service and AI Service.

Without asynchronous processing, the Activity Service would need to directly wait for the AI Service:

```text
Activity Service
      |
      | HTTP
      ▼
AI Service
      |
      ▼
Gemini API
```

This creates tighter coupling between the services.

Instead, the project uses:

```text
Activity Service
      |
      | Publish Event
      ▼
RabbitMQ
      |
      | Consume Event
      ▼
AI Service
```

This allows the Activity Service to complete the activity operation without requiring the AI processing to happen synchronously.

Example RabbitMQ concepts used:

```text
Exchange:
fitness.exchange

Queue:
activity.queue

Routing Key:
activity.tracking
```

---

# AI Recommendation Flow

When a user records an activity:

```text
User enters:
    Activity Type
    Duration
    Calories Burned
          |
          ▼
    Activity Service
          |
          ▼
     Save Activity
          |
          ▼
    Publish RabbitMQ Event
          |
          ▼
       AI Service
          |
          ▼
   Prepare AI Request
          |
          ▼
    Google Gemini API
          |
          ▼
 AI-generated Recommendation
          |
          ▼
       MongoDB
```

When the user opens the activity details page, the frontend requests the corresponding recommendation.

The result can contain:

### Analysis

An overall analysis of the activity.

### Improvements

Potential areas where the user can improve their workout.

### Suggestions

Suggested workout approaches or routines.

### Safety Guidelines

General safety considerations related to the activity.

---

# Database Design

The project uses different databases based on service requirements.

## PostgreSQL

Used by the User Service.

The User Service stores structured application user information.

Example:

```text
User
├── id
├── keyCloakId
├── email
├── password
├── firstName
├── lastName
├── createdAt
└── updatedAt
```

## MongoDB

MongoDB is used for activity/recommendation-oriented data.

The project uses MongoDB for:

* Activity data
* AI recommendation data

The use of MongoDB provides flexibility for data such as:

```text
additionalMetrics
improvements
suggestions
safety
```

where the structure can be more flexible than a traditional relational model.

---

# Technology Stack

## Backend

| Technology           | Purpose                          |
| -------------------- | -------------------------------- |
| Java                 | Primary backend language         |
| Spring Boot          | Microservice development         |
| Spring Cloud Gateway | API Gateway                      |
| Spring Security      | API security                     |
| OAuth2               | Authentication protocol          |
| JWT                  | Access token format              |
| Keycloak             | Identity Provider                |
| Eureka               | Service discovery                |
| RabbitMQ             | Asynchronous messaging           |
| PostgreSQL           | User data                        |
| MongoDB              | Activity and recommendation data |
| Google Gemini API    | AI recommendation generation     |
| Maven                | Dependency management            |
| Lombok               | Boilerplate reduction            |

## Frontend

| Technology             | Purpose                    |
| ---------------------- | -------------------------- |
| React                  | Frontend framework         |
| Vite                   | Frontend build tooling     |
| React Router           | Client-side routing        |
| Redux Toolkit          | State management           |
| React Redux            | Redux integration          |
| Material UI            | UI components              |
| Axios                  | HTTP communication         |
| react-oauth2-code-pkce | OAuth2 PKCE authentication |

## Infrastructure

| Technology     | Purpose                              |
| -------------- | ------------------------------------ |
| Docker         | Containerization                     |
| Docker Compose | Running containerized infrastructure |
| Eureka         | Service discovery                    |
| Keycloak       | Identity management                  |
| RabbitMQ       | Message broker                       |

---

# Frontend Architecture

The frontend is implemented using React and Vite.

The frontend communicates only with the API Gateway.

```text
React
  |
  ▼
Axios
  |
  ▼
API Gateway
```

The frontend does not directly communicate with individual microservices.

---

## Redux Authentication State

Redux Toolkit is used to maintain authentication-related state.

The authentication store contains:

```javascript
{
    user,
    token,
    userId
}
```

The values are also persisted in `localStorage` so that authentication information can survive page refreshes.

---

# Axios API Layer

Axios is used as the HTTP client.

A central Axios instance is configured with the API Gateway URL:

```javascript
const API_URL = "http://localhost:8080/api";

const api = axios.create({
    baseURL: API_URL
});
```

A request interceptor automatically adds authentication information.

```text
Request
   |
   ▼
Axios Interceptor
   |
   ├── X-User-ID
   |
   └── Authorization: Bearer JWT
   |
   ▼
API Gateway
```

This avoids manually adding the token to every API request.

---

# Frontend Pages and Components

The frontend contains components such as:

```text
components/
│
├── ActivityForm.jsx
├── ActivityList.jsx
└── ActivityDetail.jsx
```

### ActivityForm

Allows the user to submit:

* Activity type
* Duration
* Calories burned

### ActivityList

Retrieves activities from the backend and displays them to the authenticated user.

### ActivityDetail

Displays:

* Activity information
* Date
* Duration
* Calories burned
* AI analysis
* Improvements
* Suggestions
* Safety guidelines

---

# Project Structure

The exact structure may vary depending on the repository organization, but the project is conceptually organized as:

```text
AI-Powered-Fitness-Tracking-System/
│
├── backend/
│   │
│   ├── api-gateway/
│   │
│   ├── user-service/
│   │
│   ├── activity-service/
│   │
│   ├── ai-service/
│   │
│   └── eureka-server/
│
├── fitness-app-frontend/
│   │
│   ├── src/
│   │   ├── components/
│   │   │   ├── ActivityForm.jsx
│   │   │   ├── ActivityList.jsx
│   │   │   └── ActivityDetail.jsx
│   │   │
│   │   ├── services/
│   │   │   └── api.js
│   │   │
│   │   ├── store/
│   │   │   ├── store.js
│   │   │   └── authSlice.js
│   │   │
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── authConfig.js
│   │
│   └── package.json
│
├── docker-compose.yml
│
└── README.md
```

> Update the directory names above if your GitHub repository uses a different folder structure.

---

# Prerequisites

Before running the project, install:

* Java
* Maven
* Node.js
* npm
* Docker Desktop
* PostgreSQL
* MongoDB
* Keycloak
* RabbitMQ

You should also have a Google Gemini API key for AI recommendation generation.

---

# Configuration

The application uses configuration values for services such as:

```text
PostgreSQL
MongoDB
RabbitMQ
Keycloak
Eureka
Google Gemini API
```

For production or public repositories, sensitive values such as:

```text
API keys
Database passwords
Client secrets
Credentials
```

should be provided through environment variables or secure configuration rather than committed directly to Git.

---

# Running the Project

## 1. Clone the Repository

```bash
git clone https://github.com/sanketkumar100/AI-Powered-Fitness-Tracking-System.git
```

```bash
cd AI-Powered-Fitness-Tracking-System
```

---

# 2. Start Infrastructure

Start the required Docker containers using Docker Compose:

```bash
docker compose up -d
```

Check running containers:

```bash
docker compose ps
```

Make sure the required infrastructure is running before starting the Spring Boot services.

---

# 3. Start Eureka Server

Start the Eureka Server first so that services can register themselves.

```bash
mvn spring-boot:run
```

The Eureka server should be available at:

```text
http://localhost:8761
```

---

# 4. Start Backend Services

Start the following services:

```text
User Service
Activity Service
AI Service
API Gateway
```

Each service registers itself with Eureka.

The API Gateway then discovers services using their registered service names.

---

# 5. Start Keycloak

Keycloak should be available at:

```text
http://localhost:8181
```

Create/configure the realm:

```text
fitness-oauth2
```

Configure the OAuth2 client:

```text
oauth2-pkce-client
```

The frontend uses the following authorization endpoint:

```text
http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth
```

Token endpoint:

```text
http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/token
```

JWKS endpoint:

```text
http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/certs
```

---

# 6. Start the Frontend

Navigate to the frontend directory:

```bash
cd fitness-app-frontend
```

Install dependencies:

```bash
npm install
```

Start the Vite development server:

```bash
npm run dev
```

The frontend should be available at:

```text
http://localhost:5173
```

---

# Backend API Endpoints

All external requests are routed through the API Gateway.

Base URL:

```text
http://localhost:8080/api
```

## User APIs

### Register User

```http
POST /users/register
```

### Get User Profile

```http
GET /users/{userId}
```

### Validate User

```http
GET /users/{userId}/validate
```

---

# Activity APIs

### Create Activity

```http
POST /activities
```

Example:

```json
{
  "type": "RUNNING",
  "duration": 20,
  "caloriesBurned": 150,
  "additionalMetrics": {}
}
```

The API Gateway provides the authenticated user's Keycloak ID through:

```http
X-User-ID
```

The Activity Service associates the activity with that user.

---

### Get User Activities

```http
GET /activities
```

Returns the authenticated user's activities.

---

### Get Activity by ID

```http
GET /activities/{activityId}
```

Returns the selected activity and associated recommendation data where available.

---

# Example Application Flow

Suppose a user named `user1` logs into the application.

### Step 1 — Authentication

The user selects:

```text
LOGIN
```

The frontend redirects the user to Keycloak.

---

### Step 2 — Keycloak Authentication

The user enters their Keycloak credentials.

Keycloak authenticates the user and issues an OAuth2 access token.

---

### Step 3 — JWT

The token contains information such as:

```json
{
  "sub": "04939ea7-4520-4ea5-a462-651d850669f1",
  "preferred_username": "user1",
  "given_name": "User1",
  "family_name": "Last Name 1",
  "email": "user1@gmail.com"
}
```

The `sub` claim represents the unique Keycloak user ID.

---

### Step 4 — API Request

The frontend sends:

```http
Authorization: Bearer <JWT>
```

The Axios interceptor also adds:

```http
X-User-ID: <Keycloak User ID>
```

---

### Step 5 — API Gateway

The gateway:

1. Receives the request.
2. Validates the JWT.
3. Extracts the Keycloak user ID.
4. Synchronizes the user with User Service.
5. Routes the request to the appropriate microservice.

---

### Step 6 — Activity Creation

The Activity Service stores the activity.

Example:

```json
{
  "type": "RUNNING",
  "duration": 20,
  "caloriesBurned": 150
}
```

---

### Step 7 — RabbitMQ

The Activity Service publishes the activity event.

```text
Activity Service
      ↓
fitness.exchange
      ↓
activity.tracking
      ↓
activity.queue
```

---

### Step 8 — AI Processing

The AI Service consumes the event and sends the relevant workout information to Gemini.

Gemini generates:

```text
Analysis
Improvements
Suggestions
Safety Guidelines
```

---

### Step 9 — Recommendation Storage

The AI recommendation is stored in MongoDB.

---

### Step 10 — Frontend

When the user selects the activity, the frontend retrieves the activity details and displays the AI recommendation.

```text
Activity Details
       +
AI Recommendation
       |
       ├── Analysis
       ├── Improvements
       ├── Suggestions
       └── Safety Guidelines
```

---

# Security Considerations

The project follows a centralized authentication architecture.

```text
                Keycloak
                   |
                JWT Token
                   |
                   ▼
              API Gateway
                   |
             JWT Validation
                   |
                   ▼
           Internal Services
```

The API Gateway is the publicly exposed entry point.

Internal microservices should not be directly exposed to the public internet in a production deployment.

Sensitive configuration should be stored outside source control.

Do not commit:

```text
API keys
Passwords
Database credentials
Client secrets
Private keys
Environment files containing secrets
```

---

# Design Decisions

## Why Microservices?

The system separates responsibilities into independent services:

```text
User Service
Activity Service
AI Service
```

This makes the application easier to evolve as functionality grows.

---

## Why API Gateway?

Instead of exposing every microservice directly to the frontend:

```text
Frontend → API Gateway → Microservices
```

The gateway provides a centralized location for:

* Authentication
* Routing
* CORS
* User synchronization

---

## Why Keycloak?

Authentication and identity management are delegated to a dedicated identity provider rather than implementing authentication logic directly inside every microservice.

---

## Why RabbitMQ?

AI recommendation generation does not need to block the activity-tracking workflow.

RabbitMQ allows the Activity Service and AI Service to communicate asynchronously.

---

## Why PostgreSQL and MongoDB?

Different types of data have different requirements.

```text
PostgreSQL
    ↓
Structured user information

MongoDB
    ↓
Activity / recommendation-oriented data
```

This demonstrates polyglot persistence within the microservices architecture.

---

## Why Eureka?

Microservices need to locate each other dynamically.

Instead of hardcoding service addresses, services register themselves with Eureka.

```text
Activity Service
      ↓
   Eureka
      ↑
 AI Service
```

The API Gateway can then resolve service names dynamically.

---

# Future Improvements

The current version provides the core application functionality. Future development could include:

## Frontend

* Improved dashboard UI
* Responsive design
* User profile page
* Activity statistics
* Workout charts
* Calories tracking
* Progress visualization
* Better loading and error states
* Dark/light theme
* Improved navigation

## Backend

* More comprehensive validation
* Centralized exception handling
* Distributed tracing
* Rate limiting
* API documentation with OpenAPI/Swagger
* Improved observability and monitoring
* Automated integration testing
* Production-ready configuration management

## AI

* Personalized long-term fitness plans
* Historical workout analysis
* Trend detection
* Weekly/monthly fitness summaries
* More detailed health and workout metrics

## Deployment

* Production Docker Compose configuration
* Kubernetes deployment
* CI/CD pipeline
* Cloud deployment
* Centralized logging
* Monitoring and alerting

---

# Project Highlights

The project demonstrates practical experience with:

* **Java backend development**
* **Spring Boot**
* **Spring Cloud**
* **Microservices architecture**
* **REST API development**
* **API Gateway**
* **Service discovery**
* **OAuth2**
* **JWT**
* **Keycloak**
* **PKCE**
* **RabbitMQ**
* **Asynchronous processing**
* **PostgreSQL**
* **MongoDB**
* **Google Gemini API**
* **Docker**
* **Docker Compose**
* **React**
* **Redux Toolkit**
* **Axios**

---

# Author

**Sanket Kumar**

B.E. Computer Science & Engineering

GitHub:
https://github.com/sanketkumar100

---

# License

This project is intended for educational and portfolio purposes.
