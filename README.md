# network-scanner

A distributed microservices architecture designed to capture network traffic, associate TCP/HTTP layers, and utilize a C++ Artificial Intelligence model to detect anomalies in real-time.

1. Introduction

The Network Scanner is a specialized packet manager that bridges the gap between raw network data and machine learning analysis. By relating TCP packets to their respective HTTP payloads, the system extracts features sent to a C++ Isolation Forest (Random Forest) model via high-performance gRPC calls. The AI determines if a packet is an "outlier" (potentially malicious) and triggers database flags for security monitoring.

🔐 2. Authentication Service (auth_service)

Security is at the core of this project. The system implements a robust authentication layer to ensure that only verified users can initiate scans or access AI results.

    2FA Verification: Implements a mandatory Email-based Two-Factor Authentication.

    JWT Security: Uses JWTUtil for token generation and validation across services.

    Security Filter Chain: Configured to disable CSRF and protect sensitive endpoints (/api/scanner/**), while maintaining public access to login/creation routes.

    Persistence: Accounts are only fully activated after the first successful 2FA verification.
    
<img width="944" height="294" alt="Captura de tela de 2026-02-10 15-55-17" src="https://github.com/user-attachments/assets/33ff4736-fec9-4868-a656-e3366ee58d8e" />

📡 3. Capture & AI Service (capture_service)

This service acts as the "eyes" of the system, utilizing the Pcap4j library for deep packet inspection.

    Capture Engine: Similar to Wireshark, it sniffs packets, encapsulates relevant metadata, and streams it.

    AI Integration (gRPC): Data is streamed to the C++ AI model. The model returns a flag:

        Flag 0: Secure Packet.

        Flag 1: Dangerous/Outlier Packet.

    Database Sync: The service automatically updates the MySQL database with the AI's findings.

<img width="1141" height="534" alt="Captura de tela de 2026-02-10 16-04-29" src="https://github.com/user-attachments/assets/65206267-95fd-471a-9ca8-c50f87091060" />

🏗️ 4. System Architecture & Docker

The project is fully containerized, ensuring environment parity and easy deployment. Every component (Auth, Capture, AI, Database, and Plotter) runs in an isolated container.

    Inter-service Communication:

        Capture ↔ AI: High-speed gRPC.

        Capture ↔ Auth: JWT-based stateless authorization.

    Database Initialization: Automated via mysql/init to replicate the required schema on startup.

    Deployment: Includes custom scripts for pushing images to DockerHub and GitHub Container Registry.

<img width="1152" height="769" alt="Captura de tela de 2026-02-12 18-39-10" src="https://github.com/user-attachments/assets/3b20ea5a-e4c4-41ad-b380-b57fcd34f231" />

📊 5. Modules

    Common: A shared library containing cross-cutting concerns, DTOs, and utility classes used by both Java services.

    Plotter (In Development): A visualization tool to analyze the frequency of outliers over a customizable timeline.

🛠️ 6. How to Use
Prerequisites

    Docker & Docker Compose

    Configured Environment Variables (See .env.example)

1. Start the Environment

Run the following command in the root directory:
Bash

```
docker compose up --build
```

2. Authenticate

    Create/Login: Send a POST request to /api/auth/create or /api/auth/login.

    Verify: Check your email for the 2FA code and POST it to /api/auth/verify-code.

3. Operate the Scanner

Once authenticated (using the provided JWT), use these endpoints:

    POST /api/scanner/start: Begins real-time packet capture and analysis.

    GET /api/scanner/manual: Access the internal instruction manual.

    POST /api/scanner/plotter: Access the visualization module (Beta).

🛠️ 7. Requirements for Development

If you wish to modify the source code, ensure you have:

    Java 17+ (Maven)

    C++ Compiler (supporting C++17)

    Protobuf Compiler (for gRPC changes)

    Libpcap installed on the host machine (for local testing)
