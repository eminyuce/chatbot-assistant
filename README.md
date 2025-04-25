
# 🤖 Chatbot Assistant (Java Spring Boot + Local LLM via Ollama)

A smart, role-based AI assistant built with Java 21, Spring Boot 3, Spring AI, and locally hosted LLM (via Ollama). It extracts user intent from chat input and routes it to appropriate backend services (e.g., weather, stock price, book DB, etc.).
The front-end is built with Angular: Angular Frontend Repo: https://github.com/eminyuce/angular-chatbot

## 🔥 Key Features

- 🧠 **Intent Extraction** with vector similarity and LLM JSON parsing
- 🔐 **Role-based Access** using Spring Security + JWT (Angular/Admin roles)
- 📡 **Third-party API Integration** via FeignClient
- ⚙️ **Pluggable Services** dynamically executed based on intent
- 🖥️ **Locally Hosted LLM** using [Ollama](https://ollama.com/) for fast and secure inference

## 🧱 Architecture

```text
Angular Frontend
       ↓
REST API (Spring Boot)
       ↓
Intent Extraction Service (LLM + Embedding Similarity)
       ↓
Resolved Service Layer (Weather, Books, Stocks, etc.)
       ↓
Third Party APIs / Internal Logic
```

## 🛠️ Tech Stack

| Layer             | Tech                            |
|------------------|----------------------------------|
| Language          | Java 21                          |
| Framework         | Spring Boot 3                    |
| LLM Runtime       | Ollama (e.g., LLaMA2, Mistral)   |
| Security          | Spring Security + JWT            |
| REST API          | Spring Web, Spring Actuator      |
| Database          | H2 (in-memory)                   |
| Vector Matching   | Custom Embedding Service         |
| External APIs     | FeignClient                      |
| Documentation     | Swagger/OpenAPI                  |

## 🚀 Getting Started

### Prerequisites

- Java 21
- Docker
- [Ollama installed](https://ollama.com/)
- A running LLM model (e.g. `llama2`, `mistral`, etc.)

### Run Ollama Model

```bash
ollama run llama2
```

### Run Spring Boot App

```bash
./mvnw spring-boot:run
```

### Access API Documentation

```
http://localhost:8080/swagger-ui/index.html
```

## 📂 Project Structure

```
src/
├── controller/           // REST endpoints
├── service/              // Core services (intent, embedding, execution)
├── security/             // JWT & role-based access
├── llm/                  // Spring AI LLM integration
├── config/               // App and Feign configuration
└── model/                // DTOs and domain models
```

## ✨ Supported Intents & Examples

| Intent         | Example Input                                  |
|----------------|------------------------------------------------|
| Weather        | “What is the weather in Istanbul?”             |
| Stock Price    | “What is the stock price of AMD?”              |
| Book CRUD      | “Add book titled ‘AI Revolution’ by John”      |
| Recipe         | “Give me the recipe of New York Pizza”         |
| Drug Info      | “Tell me about aspirin”                        |
| Admin Users    | “Get all users of chatbot” (admin only)        |

## 🔒 Role-Based Access

Service | Example Input | Roles Allowed
|----------------|-------------|-----------------------------------|
Weather          | "What's the weather in Istanbul?"          | Angular, Admin
Stock Prices     | "Get AMD's stock price."                   | Angular, Admin
Book Database    | "Add a book titled 'AI Revolution'."       | Angular, Admin
Food Recipe      | "Give me the recipe for New York pizza"     | Angular, Admin
Drug Info        | "Tell me about Aspirin."                   | Angular, Admin
Chatbot Users    | "List all chatbot users."                 | Admin only

## 🧠 Intent Extraction Flow

1. Receive user input via API
2. Generate embedding vector of input
3. Match it to stored intents
4. Use LLM to extract structured intent JSON
5. Execute appropriate backend service

## 🌐 Repositories

- 🔗 [Backend Repo (Spring Boot)](https://github.com/eminyuce/chatbot-assistant)
- 🔗 [Frontend Repo (Angular)](https://github.com/eminyuce/angular-chatbot)

## 🧪 Testing

You can use tools like Postman or the Swagger UI to test:

 

## 📢 Future Enhancements

- Integrate real Drug APIs via GraphQL
- Support image generation with multimodal models
- Improve Aspect-based role validation messages

