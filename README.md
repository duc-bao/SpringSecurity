# Demo Restfull API Security
## Overview
- The purpose is to understand what the core of JWT is, how a miniature spring security configuration works,understand how I configure all the HTTP security for our application, if you are still confused about this, watch it to understand how works, I have a pretty solid explanation of the core parts(for Vietnamese people).
-  The ones I use in this test are java, postgres DB, Spring boot, Spring Security, Lombox,JSON Web Tokens (JWT),BCrypt, JPA, Hibernate ...
## Features
* Authentication: is the process of verifying the identity of a user. It ensures that the entity is who or what it claims to be, typically through methods like passwords, biometrics, or security tokens. Authentication is a critical component of security, as it helps prevent unauthorized access to resources and information.
* Authorization : is the process of determining whether a user, device, or system has permission to access certain resources or perform specific actions.Authorization helps enforce access control policies and prevent unauthorized activities within a system.
## Set request body as raw with JSON payload
### PostRequest Auth Login
 * {
  "username" : "user",
  "password" : "1"
  }
## Postman
### Get information from demo-controller
* Get infor with endpoint permitAll
![img.png](img.png)
* Get infor with auth access is User 
![img_1.png](img_1.png)
# Explain
## SecurityFilterChain
![img_2.png](img_2.png)
## JWTAuthenticationFilter
![img_3.png](img_3.png)
