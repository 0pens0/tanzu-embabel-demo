package com.embabel.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating Spring Boot project code using GenAI.
 */
@Service
public class CodeGenerationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;
    
    @Autowired
    public CodeGenerationService(Environment environment) {
        this.environment = environment;
    }
    
    /**
     * Generates a complete Spring Boot project based on requirements.
     * 
     * @param requirements Application requirements
     * @param architecture Application architecture
     * @param steps Implementation steps
     * @return Map of file paths to file contents
     */
    public Map<String, String> generateSpringBootProject(
            Map<String, Object> requirements, 
            Map<String, Object> architecture, 
            List<Map<String, Object>> steps) {
        
        Map<String, String> files = new HashMap<>();
        
        // Generate pom.xml with dependencies
        files.put("pom.xml", generatePomXml(requirements, architecture));
        
        // Generate application.properties
        files.put("src/main/resources/application.properties", generateProperties(requirements));
        
        // Generate main application class
        files.put("src/main/java/com/example/Application.java", generateMainApplicationClass(requirements));
        
        // Generate controllers, services, entities based on requirements
        files.putAll(generateJavaClasses(requirements, architecture));
        
        // Generate frontend files (HTML/CSS/JS)
        files.putAll(generateFrontendFiles(requirements));
        
        // Generate README
        files.put("README.md", generateReadme(requirements, architecture));
        
        return files;
    }
    
    private String generatePomXml(Map<String, Object> requirements, Map<String, Object> architecture) {
        String prompt = String.format("""
            Generate a complete pom.xml file for a Spring Boot project with the following requirements:
            
            Requirements: %s
            Architecture: %s
            
            Include these dependencies:
            - Spring Boot Web Starter
            - Spring Boot Data JPA
            - H2 Database (for development)
            - Spring Boot Test
            - Jackson for JSON processing
            
            Use Spring Boot 3.5.6 and Java 21.
            Group ID: com.example
            Artifact ID: %s
            
            Return only the XML content, no markdown formatting.
            """, 
            requirements, 
            architecture,
            sanitizeProjectName((String) requirements.get("projectName"))
        );
        
        return callGenAiForCode(prompt, "pom.xml");
    }
    
    private String generateProperties(Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate application.properties for a Spring Boot application with these requirements:
            
            %s
            
            Include:
            - Server port configuration
            - H2 database configuration for development
            - JPA/Hibernate settings
            - Logging configuration
            
            Return only the properties content, no markdown formatting.
            """, requirements);
        
        return callGenAiForCode(prompt, "properties");
    }
    
    private String generateMainApplicationClass(Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate a Spring Boot main application class for:
            
            %s
            
            Class name: Application
            Package: com.example
            Include proper Spring Boot annotations and main method.
            
            Return only the Java code, no markdown formatting.
            """, requirements);
        
        return callGenAiForCode(prompt, "java");
    }
    
    private Map<String, String> generateJavaClasses(Map<String, Object> requirements, Map<String, Object> architecture) {
        Map<String, String> files = new HashMap<>();
        
        // Generate controllers
        @SuppressWarnings("unchecked")
        List<String> entities = (List<String>) requirements.get("entities");
        if (entities != null) {
            for (String entity : entities) {
                String controllerCode = generateController(entity, requirements);
                files.put("src/main/java/com/example/controller/" + entity + "Controller.java", controllerCode);
                
                String serviceCode = generateService(entity, requirements);
                files.put("src/main/java/com/example/service/" + entity + "Service.java", serviceCode);
                
                String entityCode = generateEntity(entity, requirements);
                files.put("src/main/java/com/example/entity/" + entity + ".java", entityCode);
            }
        }
        
        return files;
    }
    
    private String generateController(String entityName, Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate a Spring Boot REST controller for entity: %s
            
            Requirements: %s
            
            Include:
            - @RestController annotation
            - CRUD operations (GET, POST, PUT, DELETE)
            - Proper HTTP status codes
            - Request/Response mapping
            - Error handling
            
            Return only the Java code, no markdown formatting.
            """, entityName, requirements);
        
        return callGenAiForCode(prompt, "java");
    }
    
    private String generateService(String entityName, Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate a Spring Boot service class for entity: %s
            
            Requirements: %s
            
            Include:
            - @Service annotation
            - Business logic methods
            - Repository injection
            - Proper exception handling
            
            Return only the Java code, no markdown formatting.
            """, entityName, requirements);
        
        return callGenAiForCode(prompt, "java");
    }
    
    private String generateEntity(String entityName, Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate a JPA entity class for: %s
            
            Requirements: %s
            
            Include:
            - @Entity annotation
            - @Id and @GeneratedValue for primary key
            - Proper field mappings
            - Constructors
            - Getters and setters
            
            Return only the Java code, no markdown formatting.
            """, entityName, requirements);
        
        return callGenAiForCode(prompt, "java");
    }
    
    private Map<String, String> generateFrontendFiles(Map<String, Object> requirements) {
        Map<String, String> files = new HashMap<>();
        
        // Generate HTML
        String htmlCode = generateHtml(requirements);
        files.put("src/main/resources/static/index.html", htmlCode);
        
        // Generate CSS
        String cssCode = generateCss(requirements);
        files.put("src/main/resources/static/css/style.css", cssCode);
        
        // Generate JavaScript
        String jsCode = generateJavaScript(requirements);
        files.put("src/main/resources/static/js/app.js", jsCode);
        
        return files;
    }
    
    private String generateHtml(Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate a complete HTML page for a Spring Boot web application with these requirements:
            
            %s
            
            Include:
            - Modern, responsive design
            - Bootstrap or Tailwind CSS
            - Interactive elements
            - Proper form handling
            - Clean, professional layout
            
            Return only the HTML code, no markdown formatting.
            """, requirements);
        
        return callGenAiForCode(prompt, "html");
    }
    
    private String generateCss(Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate CSS styles for a Spring Boot web application with these requirements:
            
            %s
            
            Include:
            - Modern, clean design
            - Responsive layout
            - Professional color scheme
            - Interactive elements styling
            - Mobile-friendly design
            
            Return only the CSS code, no markdown formatting.
            """, requirements);
        
        return callGenAiForCode(prompt, "css");
    }
    
    private String generateJavaScript(Map<String, Object> requirements) {
        String prompt = String.format("""
            Generate JavaScript for a Spring Boot web application with these requirements:
            
            %s
            
            Include:
            - REST API calls to backend
            - Form handling and validation
            - Dynamic content updates
            - Error handling
            - Modern ES6+ syntax
            
            Return only the JavaScript code, no markdown formatting.
            """, requirements);
        
        return callGenAiForCode(prompt, "javascript");
    }
    
    private String generateReadme(Map<String, Object> requirements, Map<String, Object> architecture) {
        String prompt = String.format("""
            Generate a comprehensive README.md for a Spring Boot project with these details:
            
            Requirements: %s
            Architecture: %s
            
            Include:
            - Project description
            - Prerequisites
            - Installation instructions
            - Running the application
            - API documentation
            - Project structure
            - Technologies used
            
            Return only the markdown content, no code blocks.
            """, requirements, architecture);
        
        return callGenAiForCode(prompt, "markdown");
    }
    
    private String callGenAiForCode(String prompt, String fileType) {
        try {
            // Get GenAI service credentials from environment
            String genAiBaseUrl = environment.getProperty("GENAI_BASE_URL");
            String genAiApiKey = environment.getProperty("GENAI_API_KEY");
            
            if (genAiBaseUrl == null || genAiApiKey == null) {
                return generateFallbackCode(fileType);
            }
            
            // Prepare the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(genAiApiKey);
            
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3, // Lower temperature for more consistent code
                "max_tokens", 2000
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Make the API call
            String url = genAiBaseUrl + "/v1/chat/completions";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse the response
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> firstChoice = (Map<String, Object>) choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String content = (String) message.get("content");
                    
                    return content.trim();
                }
            }
            
            return generateFallbackCode(fileType);
            
        } catch (Exception e) {
            System.err.println("Error calling GenAI service for code generation: " + e.getMessage());
            return generateFallbackCode(fileType);
        }
    }
    
    private String generateFallbackCode(String fileType) {
        return switch (fileType) {
            case "pom.xml" -> """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                         http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    
                    <parent>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-parent</artifactId>
                        <version>3.5.6</version>
                        <relativePath/>
                    </parent>
                    
                    <groupId>com.example</groupId>
                    <artifactId>generated-app</artifactId>
                    <version>1.0.0</version>
                    <packaging>jar</packaging>
                    
                    <properties>
                        <java.version>21</java.version>
                    </properties>
                    
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-data-jpa</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.h2database</groupId>
                            <artifactId>h2</artifactId>
                            <scope>runtime</scope>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-test</artifactId>
                            <scope>test</scope>
                        </dependency>
                    </dependencies>
                    
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-maven-plugin</artifactId>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """;
            case "properties" -> """
                server.port=8080
                spring.datasource.url=jdbc:h2:mem:testdb
                spring.datasource.driverClassName=org.h2.Driver
                spring.datasource.username=sa
                spring.datasource.password=password
                spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
                spring.jpa.hibernate.ddl-auto=create-drop
                spring.h2.console.enabled=true
                logging.level.com.example=DEBUG
                """;
            case "java" -> """
                package com.example;
                
                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;
                
                @SpringBootApplication
                public class Application {
                    public static void main(String[] args) {
                        SpringApplication.run(Application.class, args);
                    }
                }
                """;
            default -> "// Generated code placeholder";
        };
    }
    
    private String sanitizeProjectName(String projectName) {
        if (projectName == null) return "generated-app";
        return projectName
            .toLowerCase()
            .replaceAll("[^a-z0-9\\-]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
}
