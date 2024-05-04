# TuringSec App in Progress

## Overview
Our startup is planning to receive an investment of 4000 AZN as it won the 3rd place in the competition sponsored by UNEC Incubators. This project aims to facilitate communication between Companies and Hackers. 
In this app there are 3 roles `(HACKER, COMPANY, ADMIN)`. 
They can send reports if any bugs are detected. Every company can send programs for hiring hackers, etc. The app is in progress...(There will be more features )


> [!IMPORTANT]
> Register and login pages for company and hackers, company registers and messages sent to admins if they approve, they sent to generated password to company via mail.


> [!NOTE]
> In every push to this project automatically build and extract jar then it deploys spring-boot-container into my public docker repository. Only pulling this container and setting up proper configuration you can easily use this app, send requests. This defined in ci.yml. </br>
In every request I send 2 images, one updates latest version, the other one specifies image version based on git commit. You can use latest verson of this image with only pulling ->` docker pull kamil571/turingsec_spring_boot:latest `
 </br>I configured swagger in 5000 port you can test this project in your local machine.


### Tech Stack:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Docker
- H2 db
- CI
- Maven
- Mapstruct
- javax.mail
- JWT, etc.
