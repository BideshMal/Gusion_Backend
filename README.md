

# 🛡️ Gusion: The AI-Powered "Mentor" Online Judge

**Deployed Link** https://conceptual-datha-personal00abhi-6944df08.koyeb.app

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
🏗️ System Architecture Diagram (Detailed)
This diagram represents the Request-Response Lifecycle of Gusion, specifically highlighting the asynchronous nature of the AI Mentor.


    User((Student)) -->|1. Submit Code| API[Spring Boot REST API]
    API -->|2. Persist Submission| DB[(PostgreSQL 16)]
    API -->|3. Trigger Judge| Judge[Docker Judge Service]
    
    subgraph "Isolated Sandbox Environment"
        Judge -->|4. Create Container| Docker[Ephemeral Docker Container]
        Docker -->|5. Run Test Cases| Result{Evaluation}
    end
    
    Result -->|Success| API
    Result -->|Failure: WA/RE/TLE| API
    
    subgraph "AI Mentor Scaffolding"
        API -->|6. If Failure: Analyze| SpringAI[Spring AI Framework]
        SpringAI -->|7. Contextual Prompt| OpenAI[GPT-4o-mini]
        OpenAI -->|8. Structured Hints| SpringAI
        SpringAI -->|9. Store Hints| DB
    end
    
    API -->|10. Reveal Level-based Hints| User

    
📄 Design Document: Gusion AI Mentor
1. System Overview
Gusion is a specialized Online Judge designed for educational environments. Unlike competitive programming platforms that focus only on performance, Gusion focuses on pedagogical guidance. It uses a containerized execution model for security and a Large Language Model (LLM) for personalized student feedback.

2. Component Breakdown
Judge Core: Uses the Docker Engine API to pull images (e.g., openjdk:21-slim) and execute student code with hard resource limits (512MB RAM, 1s CPU time).

AI Analysis Service: Built on Spring AI ChatClient. It uses a "Prompt Template" that includes the problem description, the student's code, and the specific error message to generate non-spoiler hints.

Persistence Layer: PostgreSQL manages the relational state of problems, users, and submissions. It ensures that if a student revisits a problem, their previous AI hints are immediately available without re-triggering the LLM (cost-saving).

3. Key Design Decisions (ADRs)
Decision: Use Virtual Threads (Project Loom) for the Judge Service.

Rationale: Judging is I/O intensive (waiting for Docker containers). Virtual threads allow Gusion to handle hundreds of concurrent submissions on a single "Nano" instance without thread exhaustion.

Decision: Progressive Disclosure for AI Hints.

Rationale: To prevent students from bypassing the thinking process, hints are locked behind "Level" thresholds. A student must wait or attempt again before seeing higher-level hints.

4. Security Model
Resource Exhaustion: Protected by Docker Cgroups (prevents Fork Bombs).

Data Privacy: No personal student data is sent to OpenAI; only the code and the error are transmitted for analysis.

Network Security: The judge containers run with --network none to prevent students from making outbound requests or accessing the internal database.

### 🌟 Acknowledgments

Built with ❤️ for the **Spring Boot 4.0 Hackathon**. Special thanks to the mentors for the 10-day challenge!

