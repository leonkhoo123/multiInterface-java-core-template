# Project Development Guidelines

## Technology Stack
- **Language**: Java 25
- **Framework**: Spring Boot

## Coding Standards & Best Practices

### 1. Spring Java Approach
- **Dependency Injection**: Prefer **constructor injection** over field injection (`@Autowired` on fields) to ensure immutability and easier testing.
- **Annotations**: Correctly use Spring stereotypes (`@Service`, `@Repository`, `@RestController`, `@Component`).
- **Data Access**: Use Spring Data JPA/Repositories. Avoid raw JDBC unless necessary for performance.
- **Security**: Integrate Spring Security for robust authentication and authorization.

### 2. Production-Ready Practices
- **SOLID Principles**: Adhere to SOLID principles. Ensure classes have a single responsibility.
- **DTO Pattern**: Always use **Data Transfer Objects (DTOs)** for API requests and responses. Never expose Entity classes directly in the Controller.
- **Input Validation**: Use Bean Validation (`@Valid`, `@NotNull`, `@Size`) in Controllers/DTOs.
- **Configuration**: Externalize configuration (credentials, URLs) to `application.properties`. Do not hardcode sensitive data.
- **Immutability**: Use `final` fields where possible, especially for injected dependencies.

### 3. Logging Strategy
- **Framework**: Use SLF4J (`private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`).
- **Levels**:
    - **INFO**: Log significant business events (e.g., "User [id] logged in", "Payment processed for order [id]"). **Ensure these are present for traceability.**
    - **ERROR**: Log all exceptions with stack traces. Include context (e.g., "Failed to process request for user [id]: [error message]").
    - **DEBUG**: Use for granular details useful for development (e.g., payload contents, specific flow steps).
    - **WARN**: Use for unexpected but recoverable situations.
- **No System.out**: Never use `System.out.println` or `e.printStackTrace()`.

### 4. Error Handling
- **Global Handling**: Use `@RestControllerAdvice` to handle exceptions globally.
- **Standard Response**: Return a consistent error structure (e.g., `ErrorResponse` object with status, error code, and message).
- **Custom Exceptions**: Create specific runtime exceptions (e.g., `ResourceNotFoundException`, `BusinessValidationException`) rather than throwing generic `RuntimeException` and handle it in GlobalExceptionHandler.

### 5. Code Structure
- **Controller**: Create interface with openAPI documentation, then @RestController implementation to handles HTTP requests, validation, and maps DTOs. Delegates business logic to Service.
- **Service**: Contains all business logic. Transactional boundaries (`@Transactional`) should be defined here.
- **Repository**: Interface for database operations.

### 6. Testing
- **Unit Tests**: Use JUnit 5 and Mockito for Service layer tests.
- **Integration Tests**: Use `@SpringBootTest` and `MockMvc` for Controller tests.

## Instructions for AI Assistant
When generating code:
1.  Follow the **Spring Boot** conventions strictly.
2.  Include reasonable **INFO level logs** for the start and end of major operations.
3.  Ensure code is **production-ready** (handles nulls, validates inputs, handles exceptions).
4.  Provide concise explanations for design choices.
