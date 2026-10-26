# JobTrackr — Job Application Tracker

**A Spring Boot REST API for managing and tracking your job applications.**

JobTrackr is a backend project designed to help users organize their job search in one place. It provides user registration and login, JWT-based authentication, job application management, and analytics endpoints.

Built with Java, Spring Boot, and PostgreSQL, the project demonstrates backend development concepts including RESTful APIs, database integration, authentication, input validation, and database migrations.

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?style=for-the-badge&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&logo=apachemaven" alt="Maven">
  <img src="https://img.shields.io/badge/API-REST-009688?style=for-the-badge" alt="REST API">
</p>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Testing with Postman](#-testing-with-postman)
- [Database Migrations](#-database-migrations)
- [Project Structure](#-project-structure)
- [Future Improvements](#-future-improvements)
- [Contributing](#-contributing)
- [Author](#-author)

---

## 🎯 Overview

Searching for a job involves applying to multiple companies, keeping track of different positions, and monitoring application progress.

JobTrackr aims to simplify this process by providing a backend API for managing job applications.

The application supports authenticated users and provides endpoints for creating, viewing, updating, and deleting application records. It also includes analytics endpoints for retrieving application summaries and weekly data.

### Project Goals

- Build a practical backend application using Java and Spring Boot.
- Implement secure, token-based authentication.
- Perform database operations using PostgreSQL.
- Design RESTful APIs for application management.
- Use database migrations to manage schema changes.
- Test API endpoints using Postman.

---

## ✨ Features

### 🔐 Authentication and Authorization
- User registration with email and password.
- User login with credential validation.
- JWT-based authentication.
- Protected API access using Bearer tokens.
- Password hashing using a password encoder.

### 📋 Job Application Management
- Create job application records.
- Retrieve application records.
- Retrieve an individual application by ID.
- Update existing applications.
- Delete applications.
- Retrieve application history.

### 📊 Analytics
- Retrieve an application summary.
- Retrieve weekly application analytics.

### 🗄️ Database Management
- PostgreSQL database integration.
- Database schema versioning with Flyway.
- Configurable database connection settings.

### 🛡️ API Validation
- Request validation using Jakarta Bean Validation.
- Email format validation.
- Password length validation during registration.
- Required-field validation.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 3.5.0 | Backend application framework |
| Spring Web | REST API development |
| PostgreSQL | Relational database |
| Flyway | Database schema migrations |
| JWT | Token-based authentication |
| Jakarta Bean Validation | Request validation |
| Maven | Dependency management and build automation |
| Postman | API testing |
| Git and GitHub | Version control and source code hosting |

---

## 🏗️ Architecture

JobTrackr follows a backend API architecture in which the client communicates with the Spring Boot application through HTTP requests.

```text
                 ┌──────────────────────┐
                 │    Client / Postman  │
                 └──────────┬───────────┘
                            │
                      HTTP Requests
                            │
                 ┌──────────▼───────────┐
                 │    Spring Boot API   │
                 │                      │
                 │  ┌────────────────┐  │
                 │  │   Controllers  │  │
                 │  └───────┬────────┘  │
                 │          │           │
                 │  ┌───────▼────────┐  │
                 │  │    Services    │  │
                 │  └───────┬────────┘  │
                 │          │           │
                 │  ┌───────▼────────┐  │
                 │  │ Repositories   │  │
                 │  └───────┬────────┘  │
                 └──────────┼───────────┘
                            │
                 ┌──────────▼───────────┐
                 │     PostgreSQL       │
                 └──────────────────────┘
```

**Request flow:**

1. The client sends an HTTP request to an API endpoint.
2. Authentication is checked when the endpoint requires it.
3. The controller receives the request and validates its input.
4. The service layer executes the application logic.
5. Database operations are performed through the persistence layer.
6. The API returns an HTTP response to the client.

*Note: The diagram illustrates the application's logical architecture, not every implementation detail.*

---

## 🚀 Getting Started

Follow these steps to run JobTrackr locally.

### Prerequisites

Install the following software:

- **Java 21** — Java development kit.
- **Maven** — project build tool.
- **PostgreSQL** — relational database.
- **Git** — version control.
- **Postman** — for testing the API.

Verify your installations:

```bash
java -version
mvn -version
git --version
```

### 1. Clone the Repository

```bash
git clone https://github.com/SUMANNNN05/JobTrackr.git
```

Navigate to the project directory:

```bash
cd JobTrackr
```

### 2. Create the PostgreSQL Database

Open PostgreSQL using `psql` or your preferred PostgreSQL client.

Create the database:

```sql
CREATE DATABASE jobtrackr;
```

Create a dedicated database user if you do not already have one:

```sql
CREATE USER jobtrackr WITH PASSWORD 'your_local_password';
```

Grant the user access to the database:

```sql
GRANT ALL PRIVILEGES ON DATABASE jobtrackr TO jobtrackr;
```

If your project uses a different database user, update the application configuration accordingly.

**Important:** Use your own local development password. Never commit real database credentials to GitHub.

### 3. Configure Database Access

The application uses environment variables for its database connection.

Set the following variables in your terminal before starting the application.

**PowerShell:**

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "jobtrackr"
$env:DB_USER = "jobtrackr"
$env:DB_PASSWORD = "your_local_password"
```

Replace `your_local_password` with the password configured for your PostgreSQL user.

These commands configure the variables for the current PowerShell session.

---

## ⚙️ Configuration

The database connection is configured in `src/main/resources/application.yml`.

The current configuration supports environment-variable overrides:

```yaml
spring:
  application:
    name: jobtrackr

  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:jobtrackr}
    username: ${DB_USER:jobtrackr}
    password: ${DB_PASSWORD:jobtrackr}
```

### Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `jobtrackr` |
| `DB_USER` | Database username | `jobtrackr` |
| `DB_PASSWORD` | Database password | `jobtrackr` |

The defaults are intended for local development. Configure your own credentials before running the project.

**Security note:** Never use the default database password in a production environment. If the JWT signing secret is configurable in your project, configure it securely as well.

---

## ▶️ Running the Application

Start the application from the project root:

```bash
mvn spring-boot:run
```

Alternatively, build the project:

```bash
mvn clean package
```

Then run the generated JAR:

```bash
java -jar target/jobtrackr-0.0.1-SNAPSHOT.jar
```

The JAR filename may differ depending on your Maven configuration.

Once the application starts, the API is available at:

```text
http://localhost:8080
```

### Check Application Health

Open the following endpoint in your browser or Postman:

```http
GET http://localhost:8080/actuator/health
```

Expected response when the application is healthy:

```json
{
  "status": "UP"
}
```

---

## 📡 API Documentation

The following endpoints are available in the current backend implementation.

### Authentication

Base path: `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate an existing user |

#### Register a User

**Request**

```http
POST /api/auth/register
Content-Type: application/json
```

**Request body**

```json
{
  "email": "user@example.com",
  "password": "StrongPass123!",
  "fullName": "Test User"
}
```

**Expected result:** A successful registration returns HTTP `201 Created` with an authentication response containing the token, email, and full name.

Example response structure:

```json
{
  "token": "<jwt-token>",
  "email": "user@example.com",
  "fullName": "Test User"
}
```

#### Login

**Request**

```http
POST /api/auth/login
Content-Type: application/json
```

**Request body**

```json
{
  "email": "user@example.com",
  "password": "StrongPass123!"
}
```

**Expected result:** Successful authentication returns a JWT and the user's account information.

Save the returned token for subsequent authenticated requests.

---

### Job Applications

Base path: `/api/applications`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/applications` | Create an application |
| GET | `/api/applications` | Retrieve applications |
| GET | `/api/applications/{id}` | Retrieve an application by ID |
| PUT | `/api/applications/{id}` | Update an application |
| DELETE | `/api/applications/{id}` | Delete an application |
| GET | `/api/applications/{id}/history` | Retrieve application history |

#### Create an Application

**Request**

```http
POST /api/applications
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request body**

```json
{
  "company": "Google",
  "role": "Software Engineer"
}
```

The `company` and `role` fields are required for this request.

A successful request creates a job application record.

#### Retrieve Applications

```http
GET /api/applications
Authorization: Bearer <jwt-token>
```

Returns the application records available through the endpoint.

#### Retrieve a Single Application

Replace `{id}` with the application's actual identifier.

```http
GET /api/applications/{id}
Authorization: Bearer <jwt-token>
```

#### Update an Application

```http
PUT /api/applications/{id}
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

Use the request fields defined by your application's update DTO.

#### Delete an Application

```http
DELETE /api/applications/{id}
Authorization: Bearer <jwt-token>
```

#### Retrieve Application History

```http
GET /api/applications/{id}/history
Authorization: Bearer <jwt-token>
```

---

### Analytics

Base path: `/api/analytics`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/analytics/summary` | Retrieve application summary |
| GET | `/api/analytics/weekly` | Retrieve weekly application analytics |

These endpoints provide analytics data from the backend. The precise response fields depend on the corresponding implementation.

---

## 🧪 Testing with Postman

You can test the API using Postman without building a frontend.

### Step 1: Register

Send a `POST` request to:

```text
http://localhost:8080/api/auth/register
```

Select **Body → raw → JSON** and enter:

```json
{
  "email": "suman.test@example.com",
  "password": "TestPass123!",
  "fullName": "Suman Test"
}
```

Use a unique email if this account has already been registered.

### Step 2: Login

Send a `POST` request to:

```text
http://localhost:8080/api/auth/login
```

Body:

```json
{
  "email": "suman.test@example.com",
  "password": "TestPass123!"
}
```

Copy the JWT from the response.

### Step 3: Configure Authentication

For protected endpoints:

1. Open the request in Postman.
2. Select the **Authorization** tab.
3. Set **Auth Type** to `Bearer Token`.
4. Paste your JWT into the token field.
5. Send the request.

Do not include quotation marks around the token.

### Step 4: Create a Job Application

Send a `POST` request to:

```text
http://localhost:8080/api/applications
```

Body:

```json
{
  "company": "Google",
  "role": "Software Engineer"
}
```

Make sure the Bearer token is configured.

### Step 5: Verify CRUD Operations

Test the remaining endpoints:

- `GET /api/applications`
- `GET /api/applications/{id}`
- `PUT /api/applications/{id}`
- `DELETE /api/applications/{id}`
- `GET /api/applications/{id}/history`

Use an ID returned by your application when testing individual records.

**Tip:** Test both successful requests and failure cases, such as invalid credentials, missing required fields, and requests with an invalid token.

---

## 🗄️ Database Migrations

JobTrackr uses **Flyway** to manage database schema migrations.

Flyway keeps track of migration scripts and applies pending schema changes when the application starts.

Migration scripts are typically stored in:

```text
src/main/resources/db/migration/
```

For example:

```text
V1__initial_schema.sql
V2__add_application_features.sql
```

The actual migration filenames depend on the scripts in your repository.

**Why use Flyway?**

- Keeps database schema changes version-controlled.
- Helps maintain consistent database structures.
- Makes schema changes easier to track across development environments.

---

## 📁 Project Structure

The project uses a conventional Spring Boot structure.

```text
JobTrackr/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── jobtrackr/
│   │   │           ├── auth/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── AuthService.java
│   │   │           │   └── AuthDtos.java
│   │   │           │
│   │   │           ├── application/
│   │   │           │   └── ...
│   │   │           │
│   │   │           ├── analytics/
│   │   │           │   └── ...
│   │   │           │
│   │   │           └── ...
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │
│   └── test/
│
├── .gitignore
├── pom.xml
└── README.md
```

*This is an illustrative structure. The exact packages and filenames may differ in your repository.*

---

## 🔮 Future Improvements

The following are potential enhancements for future versions of JobTrackr:

- [ ] Add application status tracking, such as Applied, Interview, Offer, and Rejected.
- [ ] Implement search, filtering, sorting, and pagination.
- [ ] Add interview scheduling and follow-up reminders.
- [ ] Build a frontend dashboard for managing applications.
- [ ] Add unit and integration tests.
- [ ] Improve API documentation with OpenAPI/Swagger.
- [ ] Add Docker support for easier deployment.
- [ ] Deploy the backend and database to a cloud platform.
- [ ] Add CI/CD automation using GitHub Actions.

These are planned improvements, not claims about existing functionality.

---

## 🤝 Contributing

Contributions, suggestions, and feedback are welcome.

To contribute:

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Test your changes locally.
5. Submit a pull request describing your improvements.

For small fixes, feel free to open an issue to discuss the proposed change.

---

## 👨‍💻 Author

**Suman Mondal**

B.Tech Computer Science and Engineering

- GitHub: [SUMANNNN05](https://github.com/SUMANNNN05)
- Project: [JobTrackr](https://github.com/SUMANNNN05/JobTrackr)

---

## ⭐ Support

If you find this project useful, consider giving the repository a star on GitHub.

Feedback and suggestions for improvement are always welcome.

---

<p align="center">
  Built with Java, Spring Boot, and PostgreSQL.
</p>
