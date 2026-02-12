Here is the comprehensive, professional `README.md` for your hackathon project. It is structured to highlight your engineering decisions and the technical complexity of the **AI Mentor** system.

---

# 🛡️ Gusion: The AI-Powered "Mentor" Online Judge

**Theme:** AI-Powered Online Education & Automated Hiring

**Submission:** Spring Boot Cohort 4.0 Hackathon (Feb 2026)

**Project Status:** Production-Ready Backend Service

---

## 📖 Table of Contents

* [Vision & Innovation](https://www.google.com/search?q=%23-vision--innovation)
* [The AI Mentor Logic](https://www.google.com/search?q=%23-the-ai-mentor-logic)
* [Technical Architecture](https://www.google.com/search?q=%23-technical-architecture)
* [Core Technology Stack](https://www.google.com/search?q=%23-core-technology-stack)
* [Security & Code Execution](https://www.google.com/search?q=%23-security--code-execution)
* [Installation & Local Setup](https://www.google.com/search?q=%23-installation--local-setup)
* [API Documentation (Swagger)](https://www.google.com/search?q=%23-api-documentation-swagger)
* [Architecture Decision Records (ADR)](https://www.google.com/search?q=%23-architecture-decision-records-adr)

---

## 🎯 Vision & Innovation

Traditional Online Judges (OJs) are binary: they tell a student their code is **WRONG**, but never **WHY**. This creates a "frustration gap" where students resort to Googling solutions or using LLMs to simply write the code for them, destroying the learning process.

**Gusion** acts as a **virtual teaching assistant** that:

1. **Analyzes** the specific failure (RE, WA, TLE).
2. **Guides** the student using progressive, adaptive hints.
3. **Preserves Learning** by focusing on algorithmic concepts rather than providing direct code solutions.

---

## 🧠 The AI Mentor Logic

Gusion utilizes **Spring AI** to orchestrate a sophisticated feedback loop. When a submission fails, the `AnalysisService` triggers an analysis that generates three distinct levels of guidance:

| Hint Level | Type | Focus Area |
| --- | --- | --- |
| **Level 1** | 💡 Conceptual | High-level algorithmic suggestions (e.g., "Consider a Two-Pointer approach"). |
| **Level 2** | 🛠️ Logical | Specific logic flow improvements (e.g., "Check your loop exit condition"). |
| **Level 3** | 🧩 Structural | Edge-case hints or pseudo-code skeletons to unblock the student. |

This ensures the student remains the "driver" of the problem-solving process.

---

## 🏗️ Technical Architecture

Gusion follows a decoupled, service-oriented architecture to ensure code execution and AI analysis are independent and scalable.

* **API Layer:** RESTful endpoints built with Spring Boot 3.4.
* **Judge Engine:** An isolated **Docker-based** environment where user code is executed against hidden test cases.
* **AI Integration:** Spring AI acts as a portable abstraction layer for LLM interactions.
* **Persistence:** PostgreSQL 16 stores user stats, problem metadata, and AI-generated insights.

---

## 💻 Core Technology Stack

* **Framework:** Spring Boot 3.4 (utilizing Virtual Threads for high-concurrency judging).
* **AI Integration:** Spring AI + OpenAI API.
* **Database:** PostgreSQL 16 (Relational storage for complex submission histories).
* **Containerization:** Docker Engine API for secure, ephemeral code execution.
* **Documentation:** SpringDoc OpenAPI (Swagger UI).

---

## 🔒 Security & Code Execution

A primary concern of any OJ is **Remote Code Execution (RCE)**. Gusion mitigates this through:

* **Ephemeral Containers:** Every submission runs in a fresh, isolated Docker container.
* **Resource Throttling:** CPU and RAM limits are strictly enforced at the container level.
* **Network Isolation:** Containers are denied outbound internet access to prevent data exfiltration.

---

## 🚀 Installation & Local Setup

### Prerequisites

* **JDK 21**
* **Maven 3.9+**
* **Docker Desktop** (Running for the Judge Service)
* **OpenAI API Key**

### Local Development

1. **Clone the Repository:**
```bash
git clone https://github.com/BideshMal/Gusion_Backend
cd Gusion_Backend

```


2. **Environment Setup:**
Create a `.env` file in the root directory (ensure it is in `.gitignore`):
```env
OPENAI_API_KEY=sk-proj-xxxx
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gusion

```


3. **Run with Maven Wrapper:**
```bash
./mvnw clean spring-boot:run

```



---

## 🌐 API Documentation (Swagger)

The project includes a fully interactive Swagger UI for testing. Once the app is running locally, visit:

* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI Spec:** `http://localhost:8080/v3/api-docs`

---

## 📜 Architecture Decision Records (ADR)

### ADR 1: Use of Spring AI over Direct SDK

**Decision:** Use Spring AI's `ChatClient`.

**Rationale:** Decouples business logic from specific AI providers, allowing future portability between OpenAI, Google Gemini, or local models via Ollama.

### ADR 2: Docker-based Execution Sandbox

**Decision:** Execute user code in isolated Docker containers.

**Rationale:** Ensures absolute security against malicious code and prevents one student's submission from impacting another's resources.

---

### 🌟 Acknowledgments

Built with ❤️ for the **Spring Boot 4.0 Hackathon**. Special thanks to the mentors for the 10-day challenge!

