# Todo Service

This is a simple RESTful service for managing a TODO list.

## Service Description

The Todo service provides a simple API for creating, retrieving, and updating todo items. It is built with Java and Spring Boot and uses an in-memory H2 database.

### Features:
- Create a new todo item with a description and due date.
- Retrieve a paginated list of all todo items.
- Filter the list of todos by their status.
- Retrieve a single todo item by its ID.
- Update a todo item's description and/or status.
- A scheduled task runs periodically to mark overdue tasks as `past due`.

### Assumptions:
- The service uses an in-memory H2 database, so all data will be lost upon restart.
- Timestamps are handled in UTC.
- Newly created todos have their statuses as `not done`.
- Only the schedule task can change a todo's status to `past due`.
- The combination of a todo's description, status and due datetime make it unique.

## Tech Stack

- **Java**: The service is written in Java and requires JDK 25.
- **Spring Boot**: The core framework for building the application.
  - **Spring Web MVC**: For building the RESTful API.
  - **Spring Data JPA**: For data persistence and repository support.
  - **Spring Bean Validation**: For request payload validation.
  - **Spring scheduling**: For scheduled task. 
- **H2 Database**: An in-memory database.
- **Flyway**: For database schema migrations.
- **Lombok**: To reduce boilerplate code (e.g., getters, setters, constructors).
- **Maven**: For dependency management and building the project.


## How-To Guide

### Prerequisites

- Java 25 JDK
- Maven 3.x
- Docker (for containerized execution)

### Build and Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/hollaemor/tb-todo.git
    ```

2.  **Build the application:**
    Use the Maven wrapper to build the project. This will also run the tests.
    ```bash
    ./mvnw clean package
    ```

3.  **Run the application:**
    You can run the application using the Spring Boot Maven plugin:
    ```bash
    ./mvnw spring-boot:run
    ```
    Alternatively, you can run the packaged JAR file:
    ```bash
    java -jar target/todo-0.0.1-SNAPSHOT.jar
    ```
    The service will be available at `http://localhost:8080`. ( A different port can be specified by setting the `TB_TODO_SERVER_PORT` environment variable.)

### Run Tests

To execute the test suite, run the following command:
```bash
./mvnw test
```

### Run with Docker

The project includes a `Dockerfile` for containerizing the application.

1.  **Build the Docker image:**
    ```bash
    docker build -t todo-app .
    ```

2.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 todo-app
    ```
    The service will be available at `http://localhost:8080`.
