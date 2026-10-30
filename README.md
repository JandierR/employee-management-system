---
# Employee Management System

A RESTful web service built with Java and Spring Boot that handles organization departments and employee records. The application exposes standard REST endpoints for managing entities, linking employees to departments, validating request inputs, and managing persistence with MySQL.

---

## Technical Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3
* **Data Access:** Spring Data JPA / Hibernate
* **Database:** MySQL 8
* **API Documentation & Testing:** Postman
* **Build Tool:** Maven

---

## Application Architecture

The system follows a standard three-tier enterprise architecture:

1. **Controller Layer:** Exposes RESTful HTTP endpoints (`/api/departments` and `/api/employees`), handles incoming JSON requests, validates payload constraints using Jakarta Bean Validation, and formats outgoing responses using `ResponseEntity`.
2. **Service Layer:** Houses core business rules, including entity mapping via custom utility classes, entity existence checks, and declarative transaction management (`@Transactional`).
3. **Repository Layer:** Extends `JpaRepository` to perform CRUD operations, pagination, sorting, and dynamic SQL query derivation.

Data models are decoupled across layers using Data Transfer Objects (DTOs) to keep database entities private and avoid cyclic JSON references during serialization.

---

## Entity Relationship Model

The system operates around two main relational models:

* **Department:** Contains an auto-incremented primary key, name, and description.
* **Employee:** Contains an auto-incremented primary key, first name, last name, unique email, and a foreign key referencing the assigned department (`@ManyToOne` relationship).

---

## Getting Started

### Prerequisites

* Java 17 Development Kit (JDK) installed.
* MySQL Server running locally on port `3306`.
* Apache Maven installed (or use the included Maven wrapper `./mvnw`).

### Configuration

1. Clone the repository to your local machine:
```bash
git clone https://github.com/your-username/employee-management-system.git
cd employee-management-system

```


2. Configure your MySQL database settings in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

```


3. Ensure the target database exists in MySQL before launching:
```sql
CREATE DATABASE IF NOT EXISTS employee_db;

```



### Running the Application

Execute the following command in your terminal from the project root:

```bash
./mvnw spring-boot:run

```

The application will start on port `8080` by default (`http://localhost:8080`).

---

## API Endpoints

### Departments (`/api/departments`)

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/departments` | Create a new department |
| `GET` | `/api/departments/{id}` | Retrieve department by ID |
| `GET` | `/api/departments` | Retrieve all departments |
| `PUT` | `/api/departments/{id}` | Update an existing department |
| `DELETE` | `/api/departments/{id}` | Delete department by ID |

### Employees (`/api/employees`)

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/employees` | Create a new employee |
| `GET` | `/api/employees/{id}` | Retrieve employee by ID |
| `GET` | `/api/employees` | Retrieve all employees |
| `GET` | `/api/employees/department/{departmentId}` | Retrieve employees belonging to a department |
| `PUT` | `/api/employees/{id}` | Update an existing employee |
| `DELETE` | `/api/employees/{id}` | Delete employee by ID |

---

## Example Payload Formats

### Create Department (`POST /api/departments`)

```json
{
  "name": "Engineering",
  "description": "Software Development and Technical Operations"
}

```

### Create Employee (`POST /api/employees`)

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@company.com",
  "departmentId": 1
}

```
