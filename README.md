# TuringSec App in Progress

## Overview
This project aims to facilitate communication between Companies and Hackers. 
In this app there are 3 roles `(HACKER, COMPANY, ADMIN)`. 
They can send reports if any bugs are detected. Every company can send programs for hiring hackers, etc. The app is in progress...(There will be more features )


> [!IMPORTANT]
> Register and login pages for company and hackers, company registers and messages sent to admins if they approve, they sent to generated password to company via mail.


> [!NOTE]
> In every push to this project automatically build and extract jar then it deploys spring-boot-container into my public docker repository. Only pulling this container and setting up proper configuration you can easily use this app, send requests. This defined in ci.yml. </br>
In every request I send 2 images, one updates latest version, the other one specifies image version based on git commit. You can use latest verson of this image with only pulling ->` docker pull kamil571/turingsec_spring_boot:latest `
 </br>I configured swagger in 5000 port you can test this project in your local machine.


## Key Features

- **Role-based security** with JWT
- **Real-time messaging** via WebSockets (STOMP)
- **Bug report system** with file uploading
- **REST API** with Swagger 
- **CI/CD pipeline**: GitHub Actions build the JAR, create Docker images, and push to Docker Hub
- **Dual Docker image tagging**: `latest` and versioned by Git commit
- **Email notifications** via `javax.mail`
- Integrated **PostgreSQL** and **H2** databases
- Deployed on **AWS EC2** and **RDS**


### Tech Stack:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Websocket(STOMP)
- Docker -> For local development
- AWS(ec2,rds) -> For api deploy
- H2, Postgres
- CI
- Maven
- Mapstruct
- javax.mail
- JWT, etc.


