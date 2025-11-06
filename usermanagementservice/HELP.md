# API Security Integration Guide: JWT Implementation

This guide outlines the steps required to integrate JSON Web Token (JWT) based authentication and authorization into an existing Spring Boot REST API.

## Phase 1: Setup and Data Foundation

This phase establishes the project dependencies and the security-related data model.

### Step 1: Dependencies and Properties

1.  **Add Dependencies:** Include the following in your `pom.xml`:

    *   `spring-boot-starter-security`

    *   `jjwt-api`, `jjwt-impl`, and `jjwt-jackson` (for Java JWT handling).

##### Add  the Spring  starter packs
```declarative
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```
##### Add  the Jwt dependencies
```declarative
<!-- Add new jjwt dependencies -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.12.3</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
```

2.  **Configure JWT Keys:** Define the **JWT secret key** and **token expiration time** in your primary configuration file, `application.properties` (or `application.yml`). This is mandatory for token signing and validation.

```
  
        # Generate a secure Base64 key for production using  `openssl rand -base64 32` 
        genc.jwt.secret=${JWT_AUTH_KEY}
        # JWT Expiration (e.g., 15 minutes milliseconds)
        genc.jwt.expiration=900000
```

### Step 2: Data Model and Repositories

1.  **Security Entities:** Create the `User` and `Role` JPA entities, the `RoleType` enum, and the corresponding `UserRepo` and `RoleRepo` interfaces.

2.  **DTOs:** Define the following Data Transfer Objects (DTOs):

    *   `AuthRequestDTO`: For user login credentials (username/email and password).

    *   `AuthResponseDTO`: To return the generated JWT upon successful authentication.

    *   `RoleRequestDTO` / `RoleResponseDTO`: For managing role entities (if applicable).

    *   `UserDetailsImpl` (or similar): The implementation of Spring Security's `UserDetails` interface.


## Phase 2: Core Utilities and Services

This phase builds the foundational logic needed for token handling and user loading.

### Step 3: JWT Utilities and Custom User Service

1.  **Create `util` Package:** Place the **`JwtUtil.java`** class (handles token generation, validation, and claim extraction). 

2.  **User Details Service:** Implement **`CustomUserDetailsService`** (implements `UserDetailsService`) to fetch user data from the `UserRepo` and return the `UserDetailsImpl` object. 


## Phase 3: Security Configuration

This phase wires up the security filter chain and defines public access points.

### Step 4: Security Configuration and Filters

1.  **Create `config` and `filter` Packages.**

2.  **Define Entry Point:** Create **`CustomAuthenticationEntryPoint.java`** to handle 401 Unauthorized responses with a clean JSON body instead of a standard Spring error page. 

3.  **JWT Authentication Filter:** Create **`JwtAuthenticationFilter.java`** (extends `OncePerRequestFilter`). This filter will:

    *   Be skipped for public endpoints (login, register, swagger).

    *   Extract the JWT from the `Authorization` header for protected requests.

    *   Validate the token and set the `SecurityContextHolder`. 

4.  **Security Configuration:** Define **`SecurityConfig.java`** (the central configuration class) to:

    *   Disable CSRF, set session management to `STATELESS`.

    *   Define public paths (`/login`, `/register`) using `permitAll()`.

    *   Register the `CustomAuthenticationEntryPoint`.

    *   **Crucially**, add `JwtAuthenticationFilter` _before_ `UsernamePasswordAuthenticationFilter`.

5.  **OpenAPI Config:** Define the `OpenAPIConfig` to enable JWT authorization for Swagger UI (using `SecurityScheme` and `SecurityRequirement`). 


## Phase 4: API and Bootstrap

This phase completes the application layer integration.

### Step 5: Controllers, Services, and Seeding

1.  **Authentication Controller:** Create **`AuthController.java`** to expose the public endpoints:

    *   `POST /login`: Accepts `AuthRequestDTO`, authenticates the user using `AuthenticationManager`, and returns `AuthResponseDTO` with the JWT.

    *   `POST /register`: Handles new user registration. 

2.  **Role Service:** Implement the `RoleService` and `RoleServiceImpl` for managing roles (e.g., finding default roles during registration). 

3.  **Initial Data Seeding:** Implement logic in a component or event listener to ensure initial roles (e.g., `ADMIN`, `USER`) are persisted to the database upon application startup. 