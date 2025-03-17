# Rol Cable Network

## Overview

The Rol Cable Network is a comprehensive cable network management system built with **Spring Boot** and **Thymeleaf**. This project helps cable network operators manage subscriptions, track income and expenses, generate account reports, and manage consumers and their connections.

## Features

- **Subscription Management**: 
  - Allows tracking of bill collections and settlements.
  - Supports **auto-renewal** of subscriptions or **manual renewal** by the admin or employee with renew access.

- **Bill Generation**: Automatically generates bills based on active subscriptions.

- **Consumer Management**: 
  - Enables adding, editing, and deleting consumers along with managing their connections.

- **Role-Based Access Control (RBAC)**: 
  - Admin employees can assign, modify, and remove roles for other employees to manage access levels across the system.

- **Account Management**: 
  - Includes separate reports for income, expenses, and overall account management, providing valuable insights into the financial health of the business.

## Technologies Used

- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf, HTML, CSS, Javascript, JQuery
- **Database**: MySQL
- **Others**: Hibernate, JPA

## Installation

### Prerequisites

Ensure you have the following installed:

- JDK 11 or higher
- Maven
- MySQL Server
- Git

### Steps to Run Locally

1. Clone the repository:

    ```bash
    git clone https://github.com/meer34/rol_cable_network.git
    cd rol_cable_network
    ```

2. Create a MySQL database called `rolcable`:

    ```sql
    CREATE DATABASE rolcable;
    ```

3. Update the `application.properties` file with your MySQL credentials and connection details.

4. Build and run the project:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

5. Access the application at `http://localhost:8080`.

## Configuration

The application uses MySQL as the database. Here are the default configurations in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rolcable
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
