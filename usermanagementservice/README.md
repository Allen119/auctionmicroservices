### To start the application from commandprompt 
`java -Dspring.profiles.active=dev -jar target\user-mangement-service-0.0.1-SNAPSHOT.jar`

## To  generate  jar  by skipping tests
`mvn clean package -DskipTests`


### Steps  to add  jwt  security  to application
Note: Root package should be in org.genc.appname Spring  security steps


Step 1: Add spring security starter pack and Jwt dependencies
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

Step 2: Add the User Role Entity,RoleType enum & RoleRepo , UserRepo

Step 3: Create config, filter ,security and util packages and place the respective (OpenAPIConfig,SecurityConfig,JWTAutheticationFilter, CustomAuthenticationEntryPoint,JwtUtil) 
files

Step 4: Add the UseDetailsImpl class CustomUserDetailsService, Authcontroller,AuthRequestDTO

Step 5: Add the CustomUserDetails & AuthResponse in DTO package

Step 6: Add the RoleService, RoleServiceImpl, RoleRequestDTO, RoleResponseDTO & custom Exceptions

Step 7: Set the seed data in startup

Step 8: Use the openssl keys in app.properties
`openssl rand -base64 32`
