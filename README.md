# 🏋️ AI-Powered Fitness Tracking System

An AI-powered fitness tracking platform built using **Java**, **Spring Boot**, and **Microservices**. The application allows users to record their fitness activities and receive personalized workout recommendations generated using the **Google Gemini AI API**.

The project follows a modern **microservices architecture** with asynchronous communication, centralized API routing, secure authentication, and containerized deployment.

---

## 🚀 Features

- User registration and profile management
- Track fitness activities (running, walking, cycling, gym, etc.)
- Store workout details such as:
  - Activity type
  - Duration
  - Calories burned
  - Start time
  - Additional workout metrics
- AI-generated personalized fitness recommendations
- Secure authentication using OAuth2 & JWT
- Asynchronous communication using RabbitMQ
- Service discovery using Eureka Server
- Centralized routing using API Gateway
- Docker containerization
- Independent databases for each microservice

---

# 🏗️ Architecture

The application is built using a **Microservices Architecture** consisting of the following services:

```
                        Client
                           │
                    API Gateway
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
  User Service      Activity Service      AI Service
        │                  │                  │
 PostgreSQL          MongoDB          Gemini AI API
                           │
                       RabbitMQ
```

---

# 📦 Microservices

## 1. User Service

Responsible for managing user accounts.

### Responsibilities

- Register new users
- Retrieve user details
- Manage user profiles
- Store user information

### Database

- PostgreSQL

---

## 2. Activity Service

Responsible for storing fitness activity data.

### Responsibilities

- Record workouts
- Store calories burned
- Store workout duration
- Store activity type
- Publish activity events to RabbitMQ

### Database

- MongoDB

---

## 3. AI Service

Responsible for generating personalized fitness recommendations.

### Responsibilities

- Consume activity events from RabbitMQ
- Analyze workout history
- Send prompts to Google Gemini API
- Generate personalized recommendations
- Store AI recommendations

### AI Integration

- Google Gemini API

Example recommendation:

> "You completed three cardio sessions this week. Consider adding one strength training session and increase your daily protein intake."

---

# 🔄 Communication Flow

## Step 1

User logs a workout.

↓

Activity Service saves workout.

↓

Activity Service publishes an event to RabbitMQ.

↓

AI Service consumes the event.

↓

AI Service calls Google Gemini API.

↓

Gemini returns personalized recommendation.

↓

Recommendation is stored and returned to the user.

---

# 🔐 Security

Authentication is implemented using **Keycloak**.

Features include:

- OAuth2 Authentication
- JWT Tokens
- Role-Based Authorization
- Protected REST APIs

Only authenticated users can access protected endpoints.

---

# 🌐 API Gateway

Spring Cloud Gateway is used as the single entry point.

Responsibilities:

- Route requests
- Authentication
- Centralized API access
- Load balancing

---

# 🔍 Service Discovery

The project uses **Netflix Eureka**.

Benefits:

- Automatic service registration
- Dynamic service discovery
- No hardcoded service URLs

---

# 📨 RabbitMQ

RabbitMQ enables asynchronous communication.

Instead of directly calling the AI Service:

```
Activity Service
        │
        ▼
    RabbitMQ Queue
        │
        ▼
    AI Service
```

Benefits:

- Loose coupling
- Better scalability
- Improved performance
- Fault tolerance

---

# 🤖 Google Gemini AI

Google Gemini API is used to generate intelligent fitness recommendations.

The AI analyzes:

- Workout frequency
- Calories burned
- Exercise type
- Workout duration

Example Prompt:

```
User completed:

Running - 45 mins
Cycling - 30 mins
Calories Burned - 700

Suggest improvements.
```

Example Response:

```
Increase strength training twice a week.
Maintain hydration.
Aim for 8 hours of sleep.
```

---

# 🗄️ Databases

| Service | Database |
|----------|----------|
| User Service | PostgreSQL |
| Activity Service | MongoDB |
| AI Service | MongoDB |

---

# 🐳 Docker

Each microservice can run inside its own Docker container.

Benefits:

- Easy deployment
- Platform independence
- Consistent environments
- Better scalability

---

# 🛠️ Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Cloud
- Hibernate

## Microservices

- Eureka Server
- API Gateway
- Config Server
- RabbitMQ

## Security

- Keycloak
- OAuth2
- JWT

## AI

- Google Gemini API

## Databases

- PostgreSQL
- MongoDB

## DevOps

- Docker
- Maven

## Testing

- Postman

---

# 📁 Project Structure

```
Fitness-Tracking-System
│
├── api-gateway
│
├── config-server
│
├── discovery-server
│
├── user-service
│
├── activity-service
│
├── ai-service
│
└── README.md
```

---

# ⚙️ How to Run

## Clone Repository

```bash
git clone https://github.com/sanketkumar100/Fitness-Tracking-System.git
```

---

## Start Required Services

- PostgreSQL
- MongoDB
- RabbitMQ
- Keycloak

---

## Run Services

1. Config Server
2. Eureka Server
3. API Gateway
4. User Service
5. Activity Service
6. AI Service

---

# 🎯 Learning Outcomes

This project helped me gain practical experience with:

- Microservices Architecture
- Spring Cloud
- REST APIs
- Event-Driven Architecture
- RabbitMQ
- API Gateway
- Eureka Discovery
- Docker
- OAuth2 Authentication
- JWT Security
- Google Gemini AI Integration
- PostgreSQL
- MongoDB
- Backend Scalability
- Distributed Systems

---

# 👨‍💻 Author

**Sanket Kumar**

Java Full Stack Developer

- LinkedIn: https://www.linkedin.com/in/sanket-kumar-java/
- GitHub: https://github.com/sanketkumar100

---
