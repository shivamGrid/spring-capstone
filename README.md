**Overview**

This project implements a Shopping Cart System with essential features such as user registration, product management, 
cart operations, and order processing. It uses a RESTful API architecture and integrates with MySQL for database storage.

Database: MySQL

Build Tool: Maven

API Documentation: Swagger UI

**Configure MySQL Database**

Start the MySQL server.

Create a new database:

CREATE DATABASE **db_name**;

Update the application.properties file with your MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/**db_name**

spring.datasource.username=**<your-username>**
spring.datasource.password=**<your-password>**
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create

**3. Build the Project**

Run the following command to build the project:

**mvn clean install**

**4. Run the Application**

**Swagger UI:** http://localhost:8080/swagger-ui/index.html


