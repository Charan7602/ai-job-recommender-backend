# AI-Powered Job Recommender Backend

A production-ready backend system that recommends jobs to users using a combination of deterministic scoring and AI-based semantic matching, with explainable results powered by LLMs.

Built with **Java 17, Spring Boot, PostgreSQL, JPA**, and **OpenAI APIs**.

---

## 🚀 Overview

This project implements a job recommendation engine that:
- Matches users to jobs based on **experience**, **location**, and **semantic relevance**
- Uses **OpenAI embeddings** for semantic similarity
- Generates **human-readable explanations** for top recommendations using an LLM
- Preserves **stable ranking across pagination**
- Is designed to be **deployment-ready** with environment-based configuration

The system is intentionally backend-focused and avoids unnecessary frontend or infrastructure complexity.

---

## 🧠 Key Features

### 1. Core Recommendation Engine
Each job is scored using:
- **Experience Score (0–50)**  
  Based on how well user experience matches job requirements.
- **Location Score (0–20)**  
  Soft matching on preferred location.
- **Semantic Score (0–30)**  
  Cosine similarity between resume and job description embeddings.

Final score: experienceScore + locationScore + semanticScore


Jobs are ranked globally first, then paginated to ensure **ordering consistency across pages**.

---

### 2. Semantic Matching with Embeddings
- Embeddings are generated **at write time**, not during recommendation.
- Resume embeddings → derived from `rawText`
- Job embeddings → derived from `jobDescription`
- Stored as **JSON (TEXT column)** in PostgreSQL
- Parsed into vectors and compared using **cosine similarity**
- A configurable similarity threshold avoids noisy matches

This keeps recommendation latency low and predictable.

---

### 3. Explainable Recommendations (LLM)
- For the **top N jobs only** (default: 3), an LLM generates a short explanation:
    - Why this job matches the resume
    - Focuses on skills and experience alignment
- The LLM is **not part of scoring**
- Fail-safe design: if the LLM fails, recommendations still work

This separation ensures reliability and deterministic ranking.

---

### 4. Pagination & Stability
- Uses `Pageable` + `PageImpl`
- Ranking is done **before pagination**
- Serialization stabilized using Spring Data `VIA_DTO` mode
- Prevents common pagination bugs where scores shift across pages

---

##  Architecture (High Level)

1. User creates a Resume → embedding generated & stored
2. JobPosting is created → embedding generated & stored
3. Recommendation request:
    - Load user + resume
    - Score all jobs deterministically
    - Sort globally by score
    - Paginate results
    - Generate LLM explanations for top N only
4. Return paginated, explainable recommendations

---

## 🧪 Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenAI API**
    - Embeddings API (semantic matching)
    - Chat Completions API (explanations)

---

## 🔐 Configuration & Environments

- `application.properties`
    - Non-sensitive defaults (URLs, model names)
- `application-prod.properties`
    - Production profile
    - Secrets injected via environment variables
- No API keys or credentials are committed

The application is **deployment-ready** (Render / Railway / AWS).

---

## 📌 Design Decisions & Tradeoffs

- ❌ No vector DB (pgvector) yet  
  → Simpler setup, sufficient for current scale
- ✅ Write-time embeddings  
  → Faster recommendations, predictable latency
- ❌ LLM in scoring path  
  → Avoids instability and non-determinism
- ✅ LLM for explanations only  
  → Improves UX without affecting ranking

---

## 📈 Possible Extensions (Not Implemented Yet)

- pgvector for large-scale similarity search
- Caching embeddings and explanations
- Async LLM calls
- Frontend or public demo UI

These were intentionally skipped to keep the system focused and robust.

---

## 🧾 Sample API

GET /api/recommendations?userId=1&page=0&size=5


Returns:
- Paginated job recommendations
- Scores breakdown
- Optional LLM explanation for top results

---

## ✅ Status

- Fully functional locally
- Production-ready configuration
- GitHub version controlled
- Deployment optional (can be done in <1 hour)

---


