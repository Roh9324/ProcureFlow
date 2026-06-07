# ProcureFlow

**ProcureFlow** is an enterprise-grade full-stack asset procurement and request lifecycle management platform designed to streamline how employees request assets, HR reviews requirements, quotations are managed, final approvals are processed, and users are notified through secure email-based workflows.

The platform provides a role-based workflow for **Employees**, **HR Managers**, **Admins**, and **Final Approvers**, with secure authentication, OTP email verification, JWT authorization, request tracking, approval history, and cloud-hosted deployment across modern free-tier infrastructure.

---

## Live Deployment

| Layer       | Platform            | URL                                             |
| ----------- | ------------------- | ----------------------------------------------- |
| Frontend    | Vercel              | `https://procure-flow-kvp9.vercel.app`          |
| Backend API | Render Web Service  | `https://procureflow-backend-knk5.onrender.com` |
| Database    | Supabase PostgreSQL | Cloud PostgreSQL instance                       |
| Email API   | Resend              | OTP and workflow notification delivery          |

---

## Project Overview

ProcureFlow solves a common enterprise operations problem: managing employee asset requests through a controlled, trackable, and auditable approval process.

Instead of handling procurement manually through emails, spreadsheets, or informal communication, ProcureFlow centralizes the full workflow:

1. Employee registers and verifies account using OTP.
2. Employee logs in securely using JWT-based authentication.
3. Employee creates an asset request.
4. HR reviews the request.
5. Dealer quotation details can be recorded.
6. Final approver approves or rejects the request.
7. Employee receives status updates and email notifications.
8. Complete request history is maintained for tracking and audit visibility.

---

## Key Features

### Authentication and Security

* Email-based user registration.
* OTP verification using Resend Email API.
* Secure login flow with JWT authentication.
* Stateless backend security using Spring Security.
* Role-based access control for different user types.
* Protected REST APIs using JWT bearer tokens.
* Secure password hashing using BCrypt.
* CORS configuration for local and deployed frontend origins.

### Role-Based Portals

* **Employee Portal**

  * Register and verify account.
  * Login securely.
  * Create asset requests.
  * View personal request dashboard.
  * Track request status and history.
  * View employee profile.

* **HR Portal**

  * Review submitted employee asset requests.
  * Process and forward requests.
  * Manage request history.
  * Mark approved assets as delivered.

* **Admin Capabilities**

  * User, role, and department-oriented backend structure.
  * Supports enterprise user management foundations.

* **Final Approver Workflow**

  * Review quotation and request details.
  * Approve or reject final asset procurement request.
  * Trigger final decision notification emails.

### Asset Request Lifecycle

* Employee asset request creation.
* HR review stage.
* Dealer quotation management.
* Final approval or rejection.
* Delivery completion tracking.
* Request history timeline.
* Dashboard statistics for request counts and statuses.

### Email Notification System

* OTP email during registration.
* Final approval/rejection email.
* Asset delivery completion email.
* Resend HTTP API integration for cloud-compatible email delivery.
* Replaced Gmail SMTP because Render free tier blocks outbound SMTP traffic.

### Frontend Engineering

* React + TypeScript frontend.
* Vite build system.
* Axios-based API communication.
* Centralized API client using `VITE_API_BASE_URL`.
* Token storage utility for JWT management.
* Role-based frontend routing.
* Responsive dashboard-style UI.
* Vercel SPA rewrite configuration using `vercel.json`.

### Backend Engineering

* Spring Boot REST API.
* Spring Security with JWT authentication filter.
* JPA/Hibernate ORM.
* PostgreSQL cloud database connectivity.
* Docker-based backend deployment on Render.
* Environment-driven configuration for production.
* Modular package structure for auth, users, roles, asset requests, dealer quotations, final approvals, notifications, OTP, and request history.

---

## Tech Stack and Architecture

| Category          | Technology                     |
| ----------------- | ------------------------------ |
| Frontend          | React, TypeScript, Vite        |
| Frontend Hosting  | Vercel                         |
| Backend           | Java 21, Spring Boot           |
| Backend Security  | Spring Security, JWT, BCrypt   |
| Backend Hosting   | Render Web Service             |
| Database          | Supabase PostgreSQL            |
| ORM               | Spring Data JPA, Hibernate     |
| Email Service     | Resend Email API               |
| API Communication | REST, Axios                    |
| Deployment        | GitHub, Docker, Render, Vercel |
| Configuration     | Environment Variables          |
| Version Control   | Git, GitHub                    |

---

## ASCII Architecture Diagram

```text
                                      ┌──────────────────────────────┐
                                      │          End Users            │
                                      │ Employees / HR / Admin /      │
                                      │ Final Approver                │
                                      └───────────────┬──────────────┘
                                                      │
                                                      │ HTTPS
                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Frontend - Vercel                                  │
│                                                                             │
│  React + TypeScript + Vite                                                   │
│  - Login / Register UI                                                       │
│  - OTP Verification Flow                                                     │
│  - Employee Dashboard                                                        │
│  - HR Dashboard                                                              │
│  - Asset Request Pages                                                       │
│  - Profile Pages                                                             │
│  - Axios API Client                                                          │
│  - JWT Token Storage                                                         │
│                                                                             │
│  Environment:                                                               │
│  VITE_API_BASE_URL=https://procureflow-backend-knk5.onrender.com             │
└─────────────────────────────────────┬───────────────────────────────────────┘
                                      │
                                      │ REST API over HTTPS
                                      │ Authorization: Bearer <JWT>
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Backend - Render Web Service                        │
│                                                                             │
│  Java 21 + Spring Boot                                                       │
│  - Auth Module                                                               │
│  - OTP Verification                                                          │
│  - JWT Authentication                                                        │
│  - Spring Security                                                           │
│  - User / Role Management                                                    │
│  - Asset Request Management                                                  │
│  - HR Review Workflow                                                        │
│  - Dealer Quotation Workflow                                                 │
│  - Final Approval Workflow                                                   │
│  - Request History Tracking                                                  │
│  - Email Notification Service                                                │
│                                                                             │
│  Environment:                                                               │
│  DB_URL, DB_USERNAME, DB_PASSWORD                                             │
│  JWT_SECRET, JWT_EXPIRATION                                                  │
│  RESEND_API_KEY, RESEND_FROM_EMAIL                                           │
└───────────────┬───────────────────────────────┬─────────────────────────────┘
                │                               │
                │ JDBC / SSL                    │ HTTPS API
                ▼                               ▼
┌───────────────────────────────────┐   ┌─────────────────────────────────────┐
│        Supabase PostgreSQL         │   │             Resend Email API         │
│                                    │   │                                     │
│  Cloud-hosted relational database  │   │  - OTP Emails                       │
│  - Users                           │   │  - Final Approval Emails            │
│  - Roles                           │   │  - Rejection Emails                 │
│  - Departments                     │   │  - Delivery Completion Emails       │
│  - Asset Requests                  │   │                                     │
│  - Dealer Quotations               │   └─────────────────────────────────────┘
│  - Final Approvals                 │
│  - OTP Records                     │
│  - Request History                 │
└───────────────────────────────────┘
```

---

## System Flow and Business Logic

### 1. Registration and OTP Verification

When a user registers, the frontend sends registration details to the backend through the authentication API. The backend validates the request, stores user-related data, generates an OTP, and sends the OTP email through Resend.

The user must verify the OTP before completing the authentication flow.

```text
User → Vercel Frontend → Render Backend → Supabase PostgreSQL
                                  │
                                  └──→ Resend Email API → OTP Email
```

### 2. Login and JWT Authentication

After successful login, the backend generates a JWT token. The frontend stores the token and attaches it to protected requests using the Authorization header.

```text
Authorization: Bearer <jwt-token>
```

Spring Security validates the JWT using a custom JWT authentication filter. Public authentication routes such as registration, login, and OTP verification bypass JWT filtering.

### 3. Asset Request Lifecycle

The employee creates an asset request from the employee portal. The request is stored in Supabase PostgreSQL and becomes visible to HR users.

HR reviews the request and may forward it through the procurement workflow. Dealer quotation details and final approval details are stored as part of the request lifecycle.

### 4. Final Approval and Notification

The final approver reviews the request and quotation information. Once approved or rejected, the backend records the decision and sends a notification email to the employee.

### 5. Request Tracking

Each major action in the lifecycle is recorded in request history so that the employee, HR, and authorized users can track the full timeline of the request.

---

## Database Schema / Structure Overview

The application uses a relational PostgreSQL database hosted on Supabase.

### Core Domain Tables

```text
users
├── id
├── name
├── email
├── password
├── role_id
├── department_id
├── verified / verification status
└── timestamps

roles
├── id
├── name
└── description

departments
├── id
├── name
└── description

otp
├── id
├── email / user reference
├── otp_code
├── expiry_time
└── verification status

asset_requests
├── id
├── employee_id
├── asset_name
├── quantity
├── reason / description
├── status
├── created_at
└── updated_at

dealer_quotations
├── id
├── asset_request_id
├── dealer_name
├── quoted_amount
├── delivery_days
├── remarks
└── timestamps

final_approvals
├── id
├── asset_request_id
├── decision
├── reason
├── approved_by
└── decision_time

request_history
├── id
├── asset_request_id
├── action
├── status
├── performed_by
├── remarks
└── created_at
```

### Relationship Summary

```text
User ──────── belongs to ──────── Role
User ──────── belongs to ──────── Department
User ──────── creates ─────────── AssetRequest
AssetRequest ─ has ───────────── DealerQuotation
AssetRequest ─ has ───────────── FinalApproval
AssetRequest ─ has many ──────── RequestHistory
User Email ─── has ───────────── OTP Verification Record
```

---

## API Endpoints

The backend exposes REST APIs under the Render-hosted Spring Boot server.

Base URL:

```text
https://procureflow-backend-knk5.onrender.com
```

Local backend URL:

```text
http://localhost:8081
```

### Authentication APIs

| Method | Endpoint               | Description                            | Access |
| ------ | ---------------------- | -------------------------------------- | ------ |
| POST   | `/api/auth/register`   | Register a new user and send OTP email | Public |
| POST   | `/api/auth/verify-otp` | Verify registration OTP                | Public |
| POST   | `/api/auth/login`      | Authenticate user and return JWT token | Public |

### User APIs

| Method | Endpoint          | Description                                 | Access             |
| ------ | ----------------- | ------------------------------------------- | ------------------ |
| GET    | `/api/users/me`   | Get currently authenticated user profile    | Authenticated      |
| GET    | `/api/users`      | Get users list for administrative workflows | Admin / Authorized |
| PUT    | `/api/users/{id}` | Update user details                         | Admin / Authorized |
| DELETE | `/api/users/{id}` | Delete user                                 | Admin / Authorized |

### Asset Request APIs

| Method | Endpoint                   | Description                                      | Access                |
| ------ | -------------------------- | ------------------------------------------------ | --------------------- |
| POST   | `/api/asset-requests`      | Create a new employee asset request              | Employee              |
| GET    | `/api/asset-requests/my`   | Get asset requests created by logged-in employee | Employee              |
| GET    | `/api/asset-requests`      | Get all asset requests for review                | HR / Admin / Approver |
| GET    | `/api/asset-requests/{id}` | Get detailed request information                 | Authorized            |
| PUT    | `/api/asset-requests/{id}` | Update request information or status             | Authorized            |
| DELETE | `/api/asset-requests/{id}` | Delete request where allowed                     | Authorized            |

### HR Workflow APIs

| Method | Endpoint                  | Description                        | Access     |
| ------ | ------------------------- | ---------------------------------- | ---------- |
| GET    | `/api/hr/requests`        | View requests pending HR action    | HR Manager |
| PUT    | `/api/hr/requests/{id}`   | Review, update, or forward request | HR Manager |
| GET    | `/api/hr/request-history` | View HR request history            | HR Manager |

### Dealer Quotation APIs

| Method | Endpoint                             | Description                      | Access          |
| ------ | ------------------------------------ | -------------------------------- | --------------- |
| POST   | `/api/dealer-quotations`             | Add dealer quotation for request | HR / Authorized |
| GET    | `/api/dealer-quotations/{requestId}` | Get quotation for request        | Authorized      |
| PUT    | `/api/dealer-quotations/{id}`        | Update quotation details         | Authorized      |

### Final Approval APIs

| Method | Endpoint                           | Description                                 | Access         |
| ------ | ---------------------------------- | ------------------------------------------- | -------------- |
| GET    | `/api/final-approvals`             | View requests pending final approval        | Final Approver |
| POST   | `/api/final-approvals`             | Submit final approval or rejection decision | Final Approver |
| GET    | `/api/final-approvals/{requestId}` | Get final approval details for a request    | Authorized     |

### Request History APIs

| Method | Endpoint                           | Description                             | Access               |
| ------ | ---------------------------------- | --------------------------------------- | -------------------- |
| GET    | `/api/request-history/{requestId}` | View full timeline for an asset request | Authorized           |
| POST   | `/api/request-history`             | Add request lifecycle history entry     | Backend / Authorized |

> Note: Endpoint names reflect the application module structure and primary API surface implemented for authentication, employee requests, HR review, dealer quotation, final approval, and request history workflows.

---

## Local Setup and Installation

### Prerequisites

Install the following:

* Java 21
* Maven
* Node.js
* npm
* PostgreSQL or Supabase database
* Git

---

## Repository Structure

```text
ProcureFlow/
├── ProcureFlowFrontEnd/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── types/
│   │   └── utils/
│   ├── package.json
│   ├── vite.config.ts
│   ├── vercel.json
│   └── .env.example
│
├── PrcureflowBackend/
│   └── PrcureflowBackend/
│       ├── src/main/java/com/example/PrcureflowBackend/
│       │   ├── auth/
│       │   ├── security/
│       │   ├── user/
│       │   ├── role/
│       │   ├── department/
│       │   ├── assetrequest/
│       │   ├── dealerquotation/
│       │   ├── finalapproval/
│       │   ├── notification/
│       │   ├── otp/
│       │   └── requesthistory/
│       ├── src/main/resources/
│       │   ├── application.properties
│       │   └── application-local.properties
│       ├── Dockerfile
│       └── pom.xml
│
└── .gitignore
```

---

## Frontend Setup

Navigate to the frontend directory:

```bash
cd ProcureFlowFrontEnd
```

Install dependencies:

```bash
npm install
```

Create `.env`:

```env
VITE_API_BASE_URL=http://localhost:8081
```

Run locally:

```bash
npm run dev
```

Frontend will run on:

```text
http://localhost:5173
```

---

## Backend Setup

Navigate to the backend directory:

```bash
cd PrcureflowBackend/PrcureflowBackend
```

Create or update `application-local.properties` for local development:

```properties
spring.application.name=PrcureflowBackend

spring.datasource.url=jdbc:postgresql://localhost:5432/procureflow
spring.datasource.username=postgres
spring.datasource.password=your_local_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

server.port=8081

jwt.secret=your-local-jwt-secret-key
jwt.expiration=86400000

resend.api.key=your_resend_api_key
resend.from.email=onboarding@resend.dev

spring.flyway.enabled=false
```

Run backend locally:

```bash
mvn spring-boot:run
```

Backend will run on:

```text
http://localhost:8081
```

---

## Production Environment Variables

### Frontend - Vercel

```env
VITE_API_BASE_URL=https://procureflow-backend-knk5.onrender.com
```

### Backend - Render

```env
DB_URL=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require
DB_USERNAME=postgres.sfunjrzlfhbcuqwlkfea
DB_PASSWORD=your_supabase_database_password

JWT_SECRET=your_secure_jwt_secret
JWT_EXPIRATION=86400000

JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
HIBERNATE_FORMAT_SQL=false
FLYWAY_ENABLED=false
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

RESEND_API_KEY=your_resend_api_key
RESEND_FROM_EMAIL=onboarding@resend.dev
```

---

## Vercel SPA Routing Configuration

The frontend uses client-side routing. To prevent `404: NOT_FOUND` on direct refresh of routes such as `/login`, `/register`, and `/employee/dashboard`, the project includes:

```json
{
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

File location:

```text
ProcureFlowFrontEnd/vercel.json
```

---

## Backend Deployment Notes

The backend is deployed as a Dockerized Render Web Service.

Render provides a dynamic port through the `PORT` environment variable. The backend must bind to that port in production.

Typical production server configuration:

```properties
server.port=${PORT:8081}
```

The backend connects to Supabase PostgreSQL using the pooled Supabase connection string with SSL enabled.

---

## Email Delivery Notes

Originally, the application used Gmail SMTP:

```text
smtp.gmail.com:587
```

However, Render free services block outbound SMTP traffic on common SMTP ports. To support email delivery in the deployed environment, ProcureFlow uses **Resend Email API** over HTTPS.

Current email capabilities:

* OTP email verification.
* Final approval notification.
* Rejection notification.
* Asset delivery completion notification.

For testing, `onboarding@resend.dev` can be used as the sender. For production-grade sending to all users, a verified domain should be configured in Resend.

---

## Security Model

ProcureFlow uses stateless JWT authentication.

### Public Routes

```text
/api/auth/register
/api/auth/login
/api/auth/verify-otp
/error
```

### Protected Routes

All other API endpoints require:

```text
Authorization: Bearer <jwt-token>
```

### Security Features

* JWT token validation.
* Role-based authorities.
* BCrypt password hashing.
* Stateless session policy.
* CORS restricted to known frontend origins.
* Dedicated JWT authentication filter.
* OTP-based email verification.

---

## Request Lifecycle Example

```text
Employee registers
        │
        ▼
OTP sent through Resend
        │
        ▼
Employee verifies OTP
        │
        ▼
Employee logs in
        │
        ▼
JWT token issued
        │
        ▼
Employee creates asset request
        │
        ▼
Request stored in Supabase PostgreSQL
        │
        ▼
HR reviews request
        │
        ▼
Dealer quotation is added
        │
        ▼
Final approver approves or rejects
        │
        ▼
Employee receives status email
        │
        ▼
Request history is updated
```

---

## Deployment Stack

| Component | Platform         | Purpose                              |
| --------- | ---------------- | ------------------------------------ |
| GitHub    | Source Control   | Repository and deployment trigger    |
| Vercel    | Frontend Hosting | React SPA hosting                    |
| Render    | Backend Hosting  | Spring Boot REST API service         |
| Supabase  | Database         | Managed PostgreSQL database          |
| Resend    | Email API        | OTP and workflow notification emails |

---

## Common Deployment Issues Resolved

### 1. Vercel Direct Route 404

Problem:

```text
/login or /register returned 404 on refresh
```

Resolution:

```text
Added ProcureFlowFrontEnd/vercel.json with SPA rewrite to /index.html
```

### 2. Render Backend SMTP Failure

Problem:

```text
Mail server connection failed: smtp.gmail.com:587
```

Resolution:

```text
Replaced Gmail SMTP with Resend HTTP Email API
```

### 3. Backend Security Blocking Auth Routes

Problem:

```text
/api/auth/register returned 403
```

Resolution:

```text
Updated Spring Security and JWT filter to allow /api/auth/** and OPTIONS requests
```

### 4. Supabase Dialect / JDBC Configuration

Problem:

```text
Unable to determine Hibernate dialect
```

Resolution:

```text
Configured PostgreSQL JDBC URL, credentials, and PostgreSQL dialect for Supabase
```

---

## Future Enhancements

* Add verified production email domain in Resend.
* Add admin dashboard for full user and department management.
* Add audit logs for enterprise compliance.
* Add file attachment support for asset requests.
* Add analytics dashboard for procurement metrics.
* Add automated request escalation rules.
* Add role-based seed data and database migration scripts.
* Add CI/CD pipeline with automated tests.

---

## Author

**Rohan Mishra**

GitHub: `Roh9324`

---

## Status

ProcureFlow is successfully deployed as a live full-stack cloud application using:

```text
Vercel + Render + Supabase + Resend
```

The application supports live authentication, OTP email delivery, protected dashboards, and enterprise asset request workflow management.
