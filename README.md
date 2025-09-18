# 📝 Todo Web Application

This is a **full-stack todo web application** built with **Spring Boot** (backend) and supports **AI-powered task scheduling** via [OpenRouter](https://openrouter.ai).  
It allows users to create, update, delete, and view tasks with authentication, pagination, sorting, and automatic scheduling suggestions.

---

## 🚀 Features

- User authentication & authorization
- CRUD operations for tasks
- Pagination and sorting with filters (status, priority, date range)
- Automatic overdue task handling
- AI-powered scheduling assistant:
    - Suggests times to complete tasks within a month
    - Enforces rules: only answers valid month+year queries
    - Returns `"Cannot answer this question"` otherwise
- Rate limiting (Bucket4j) to protect API usage
- JSON response parsing into DTOs (`SuggestionDTO`)

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot (Java 17+)
- **Database**: PostgreSQL (can adapt to H2 for testing)
- **Security**: Spring Security with JWT
- **Scheduling**: Spring `@Scheduled` tasks
- **AI Integration**: OpenRouter API (GPT, DeepSeek, LLaMA models)
- **Rate Limiting**: [Bucket4j](https://github.com/bucket4j/bucket4j)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

---

## ⚙️ Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/WokCao/todo-app.git
cd todo-app
```

### 2. Add value in application.properties
```bash
openrouter.api.key=sk-your_api_key
openrouter.api.url=https://openrouter.ai/api/v1/chat/completions
openrouter.model=your_model

spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

spring.datasource.url=jdbc:postgresql://localhost:5432/todos
spring.datasource.username=your_username
spring.datasource.password=your_password
```
