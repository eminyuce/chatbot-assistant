# 🤖 Chatbot Assistant

A Java Spring Boot-based chatbot assistant powered by a locally running LLM (via [Ollama](https://ollama.com/)) and integrated with **Spring AI**. This assistant can intelligently detect user intent and respond with information such as weather updates or stock prices. The architecture includes two different service modes for flexibility and experimentation.

---

## ✨ Features

- 🔁 **Locally Hosted LLM**: Utilizes Ollama to run a Large Language Model locally for fast, secure, and offline access.
- 🎯 **Intent Detection**: Extracts user intent from input using LLM and routes to the correct service.
- ☁️ **Weather & Stock Info**: Dynamically calls external APIs (or static fallbacks) using FeignClient based on user intent.
- 🛠️ **Two Operating Modes**:
  - `ask-ai-stream`: Uses LLM for intent extraction and a `switch`-based logic to call API services.
  - `ask-ai-tool`: Uses Spring AI's `@Tool` annotation to dynamically trigger corresponding service logic.

---

## 🧱 Architecture Overview

```
User Input --> Spring AI (LLM via Ollama)
           --> Extracted Intent
                --> [ask-ai-stream] --> Switch/Case based service call
                --> [ask-ai-tool]   --> @Tool annotated dynamic method call
           --> Response (Weather / Stock Info / Static Fallback)
```

---

## 🧩 Tech Stack

- **Java 17+**
- **Spring Boot 3**
- **Spring AI**
- **Ollama (Local LLM Runtime)**
- **FeignClient** (for external API calls)
- **Static Abstraction Layer** (fallback for service layer when APIs are unavailable)

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Docker (for Ollama)
- Ollama installed and running with a chosen LLM model (e.g., `llama2`, `mistral`, etc.)

### Clone the Repo

```bash
git clone https://github.com/your-username/chatbot-assistant.git
cd chatbot-assistant
```

### Start Ollama

```bash
ollama run mistral
```

> Replace `mistral` with the model you're using.

### Run the App

Choose one of the service modes:

```bash
# Run ask-ai-stream service
./mvnw spring-boot:run -Dspring-boot.run.profiles=stream

# Run ask-ai-tool service
./mvnw spring-boot:run -Dspring-boot.run.profiles=tool
```

---

## 🧪 Example Intents

| User Input                     | Extracted Intent | Service Called    |
|-------------------------------|------------------|-------------------|
| "What's the weather in Paris?"| weather          | Weather API       |
| "Get me the latest stock price of AAPL" | stock       | Stock Price API  |

---

## 📁 Project Structure

```bash
src/
├── main/
│   ├── java/com/yourname/chatbot/
│   │   ├── stream/         # ask-ai-stream implementation
│   │   ├── tool/           # ask-ai-tool implementation
│   │   ├── service/        # Business logic layer
│   │   ├── static/         # Static fallback service
│   │   └── config/         # Ollama and Spring AI configs
│   └── resources/
│       └── application.yml # Profiles and configs
```

---

## 🧠 Notes

- Static layer is used when external API access is unavailable, simulating the real service responses.
- Intent routing logic in `ask-ai-stream` is ideal for basic rule-based control.
- `@Tool` annotation in `ask-ai-tool` provides a dynamic and elegant approach to delegate tasks using Spring AI.

---

## 🏗️ Future Improvements

- ✅ Add real API integrations with proper authentication
- 💬 Add support for multi-turn conversations
- 📦 Package as Docker container for easy deployment
- 🧪 Add unit and integration tests for services

---

## 📜 License

MIT License — feel free to use and contribute!
