# MultiInterface-java-core

**multiInterface-java-core** is a flexible, extensible **Spring Boot-based Java core template** designed to serve as a fast starting point for backend projects.

It supports multiple input interfaces (REST API, Kafka), and modular job execution — making it ideal as a **centralized core** for **microservice-oriented architectures**.

---

## ✨ Features

- 🔌 **Multi-Interface Support**
  - REST API (Spring Web)
  - Kafka Listener (Spring Kafka)

- 🧩 **Modular Job Execution**
  - Pluggable job beans (e.g., based on triggered events or Api calls)
  - Flexible dispatcher design

- ⚙️ **Microservice-Friendly**
  - Acts as a gateway hub, orchestrator, or standalone service
  - Clean separation of interface, auth, and business logic layers

---

## 🛣️ Future Roadmap

Planned enhancements to make `multiInterface-java-core` more powerful and production-ready:

### 🔌 Multi-Interface Support

* **gRPC Support**
  Add robust gRPC server integration using Netty-based configuration.

* **Advanced Kafka Configuration**
  Include support for Kafka Admin APIs to allow dynamic topic creation, custom partitioning, and more advanced error handling.

* **Confluent Kafka Support**
  Enable full compatibility with Confluent Kafka features such as the Schema Registry and support for Avro/Protobuf serialization.

---

### 🔐 Authentication

* **User Authentication Module**
  Implement modular authentication using Spring Security with support for JWT-based access control.

* **User Context Injection**
  Allow passing of authenticated user context into triggered jobs to support fine-grained access control and audit trails.

---

### 🔑 Spring Vault Integration

* **Enhanced Secrets Management**
  Extend support for multiple Vault secret backends and allow dynamic injection of secrets into job execution.

* **Centralized Configuration via Vault**
  Move toward fully Vault-driven configuration management to improve environment consistency and security.

---

### 🗃️ Database Migration
* **Flyway Integration
  Integrate Flyway to handle automated execution of SQL scripts, ensuring consistent and versioned schema evolution across environments.
  Organize migration scripts in a modular way by feature or module.

---



