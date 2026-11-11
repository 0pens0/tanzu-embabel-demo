package com.embabel.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.embabel.demo.service.CodeGenerationService;
import com.embabel.demo.util.ZipUtility;

/**
 * GenAI Demo Controller for Cloud Foundry deployment.
 * Provides AI-powered task breakdown using the GenAI service.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GenAiDemoController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;
    private final CodeGenerationService codeGenerationService;
    
    @Autowired
    public GenAiDemoController(Environment environment, CodeGenerationService codeGenerationService) {
        this.environment = environment;
        this.codeGenerationService = codeGenerationService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Embabel Demo with GenAI",
            "version", "0.1.0-SNAPSHOT",
            "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/framework-info")
    public ResponseEntity<Map<String, Object>> getFrameworkInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Embabel Framework",
            "version", "0.1.3",
            "description", "Framework for authoring agentic flows on the JVM",
            "features", List.of(
                "Java-first approach with full type safety",
                "Spring Boot integration",
                "Sophisticated AI planning",
                "Built-in testability",
                "Enterprise-ready features"
            ),
            "buildingBlocks", Map.of(
                "@Agent", "Define agents as Spring beans",
                "@Action", "Chain actions with type safety",
                "@AchievesGoal", "Define final objectives",
                "Domain Models", "Java records for structured data",
                "Personas", "Different AI personalities",
                "LLM Mixing", "Use the right model for each task"
            )
        ));
    }

    @PostMapping("/task-breakdown")
    public ResponseEntity<Map<String, Object>> breakDownTask(@RequestBody Map<String, String> request) {
        String taskDescription = request.get("taskDescription");
        
        try {
            // Use GenAI service to break down the task
            Map<String, Object> aiResponse = callGenAiService(taskDescription, "BREAK_DOWN_STEPS");
            
            // Create execution trace similar to Embabel agent
            Map<String, Object> executionTrace = createExecutionTrace(taskDescription, aiResponse);
            
            return ResponseEntity.ok(executionTrace);
        } catch (Exception e) {
            // Fallback to mock response if GenAI service fails
            return ResponseEntity.ok(createMockExecutionTrace(taskDescription));
        }
    }

    @PostMapping("/generate-code")
    public ResponseEntity<Map<String, Object>> generateCode(@RequestBody Map<String, String> request) {
        String taskDescription = request.get("taskDescription");
        
        try {
            // Step 1: Analyze requirements with detailed reasoning
            Map<String, Object> requirements = analyzeApplicationRequirements(taskDescription);
            
            // Step 2: Design architecture
            Map<String, Object> architecture = defineApplicationArchitecture(requirements);
            
            // Step 3: Break down into implementation steps
            List<Map<String, Object>> steps = breakDownIntoImplementationSteps(architecture);
            
            // Step 4: Generate project structure
            Map<String, Object> projectStructure = generateProjectStructure(architecture);
            
            // Step 5: Generate actual code using GenAI for each file
            Map<String, String> generatedFiles = codeGenerationService.generateSpringBootProject(
                requirements, architecture, steps
            );
            
            // Return trace with code preview (not full files, just summary)
            return ResponseEntity.ok(createCodeGenerationTrace(
                taskDescription, requirements, architecture, steps, generatedFiles
            ));
        } catch (Exception e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(createMockCodeGenerationTrace(taskDescription));
        }
    }

    @PostMapping("/download-code")
    public ResponseEntity<byte[]> downloadCode(@RequestBody Map<String, Object> codeData) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> files = (Map<String, String>) codeData.get("files");
            String projectName = (String) codeData.get("projectName");
            
            if (files == null || projectName == null) {
                return ResponseEntity.badRequest().build();
            }
            
            byte[] zipBytes = ZipUtility.createProjectZip(files, ZipUtility.sanitizeProjectName(projectName));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(
                ContentDisposition.attachment()
                    .filename(ZipUtility.sanitizeProjectName(projectName) + ".zip")
                    .build()
            );
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);
        } catch (Exception e) {
            System.err.println("Error creating ZIP file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sample-tasks")
    public ResponseEntity<List<Map<String, String>>> getSampleTasks() {
        return ResponseEntity.ok(List.of(
            Map.of(
                "title", "Create a REST API for a blog platform with CRUD operations",
                "description", "Spring Boot REST API with posts, comments, and user management",
                "category", "Web Application"
            ),
            Map.of(
                "title", "Build a todo list web app with Spring Boot backend",
                "description", "Full-stack todo application with REST API and HTML frontend",
                "category", "Web Application"
            ),
            Map.of(
                "title", "Create a product catalog API with search functionality",
                "description", "E-commerce product catalog with filtering, pagination, and search",
                "category", "Web Application"
            ),
            Map.of(
                "title", "Build a user management system with authentication",
                "description", "Spring Boot application with user registration, login, and profile management",
                "category", "Web Application"
            )
        ));
    }

    @GetMapping("/scenarios")
    public ResponseEntity<List<Map<String, Object>>> getScenarios() {
        return ResponseEntity.ok(List.of(
            Map.of(
                "id", "blog-api",
                "title", "Create a REST API for a blog platform with CRUD operations",
                "description", "Spring Boot REST API with posts, comments, and user management",
                "category", "Web Application",
                "complexity", "Moderate",
                "estimatedSteps", 8,
                "expectedDuration", "2-3 days",
                "highlights", List.of(
                    "Spring Boot REST API development",
                    "JPA entities and repositories",
                    "CRUD operations implementation",
                    "JSON API responses"
                ),
                "expectedActions", List.of("analyzeApplicationRequirements", "defineApplicationArchitecture", "breakDownIntoImplementationSteps", "generateProjectStructure", "generateSourceCode")
            ),
            Map.of(
                "id", "todo-app",
                "title", "Build a todo list web app with Spring Boot backend",
                "description", "Full-stack todo application with REST API and HTML frontend",
                "category", "Web Application",
                "complexity", "Moderate",
                "estimatedSteps", 10,
                "expectedDuration", "3-4 days",
                "highlights", List.of(
                    "Full-stack Spring Boot application",
                    "REST API with frontend integration",
                    "Database persistence",
                    "Modern web UI"
                ),
                "expectedActions", List.of("analyzeApplicationRequirements", "defineApplicationArchitecture", "breakDownIntoImplementationSteps", "generateProjectStructure", "generateSourceCode")
            ),
            Map.of(
                "id", "product-catalog",
                "title", "Create a product catalog API with search functionality",
                "description", "E-commerce product catalog with filtering, pagination, and search",
                "category", "Web Application",
                "complexity", "Complex",
                "estimatedSteps", 12,
                "expectedDuration", "4-5 days",
                "highlights", List.of(
                    "Advanced REST API features",
                    "Search and filtering capabilities",
                    "Pagination implementation",
                    "Database relationships"
                ),
                "expectedActions", List.of("analyzeApplicationRequirements", "defineApplicationArchitecture", "breakDownIntoImplementationSteps", "generateProjectStructure", "generateSourceCode")
            ),
            Map.of(
                "id", "user-management",
                "title", "Build a user management system with authentication",
                "description", "Spring Boot application with user registration, login, and profile management",
                "category", "Web Application",
                "complexity", "Complex",
                "estimatedSteps", 14,
                "expectedDuration", "5-6 days",
                "highlights", List.of(
                    "User authentication system",
                    "Security implementation",
                    "Profile management",
                    "Session handling"
                ),
                "expectedActions", List.of("analyzeApplicationRequirements", "defineApplicationArchitecture", "breakDownIntoImplementationSteps", "generateProjectStructure", "generateSourceCode")
            )
        ));
    }

    @GetMapping("/action-details/{actionName}")
    public ResponseEntity<Map<String, Object>> getActionDetails(@PathVariable String actionName) {
        Map<String, Object> details = switch (actionName) {
            case "analyzeApplicationRequirements" -> Map.of(
                "name", "analyzeApplicationRequirements",
                "description", "Analyzes web application requirements and technical specifications",
                "persona", "SOLUTION_ARCHITECT",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.3,
                "purpose", "Extracts technical requirements, identifies entities, API endpoints, and architecture needs",
                "codeExample", "@Action\nRequirements analyzeApplicationRequirements(String taskDescription, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.3))\n        .withPromptContributor(SOLUTION_ARCHITECT)\n        .createObject(\"Analyze web app requirements...\", Requirements.class);\n}"
            );
            case "defineApplicationArchitecture" -> Map.of(
                "name", "defineApplicationArchitecture",
                "description", "Defines Spring Boot application architecture and technology stack",
                "persona", "SOLUTION_ARCHITECT",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.2,
                "purpose", "Determines project structure, dependencies, and architectural patterns",
                "codeExample", "@Action\nArchitecture defineApplicationArchitecture(Requirements requirements, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.2))\n        .withPromptContributor(SOLUTION_ARCHITECT)\n        .createObject(\"Define architecture...\", Architecture.class);\n}"
            );
            case "breakDownIntoImplementationSteps" -> Map.of(
                "name", "breakDownIntoImplementationSteps",
                "description", "Breaks down architecture into detailed implementation steps",
                "persona", "SOFTWARE_ENGINEER",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.4,
                "purpose", "Creates specific, actionable implementation steps with code generation tasks",
                "codeExample", "@Action\nList<ImplementationStep> breakDownIntoImplementationSteps(Architecture architecture, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.4))\n        .withPromptContributor(SOFTWARE_ENGINEER)\n        .createObject(\"Break down implementation...\", List.class);\n}"
            );
            case "generateProjectStructure" -> Map.of(
                "name", "generateProjectStructure",
                "description", "Generates Maven project structure and configuration files",
                "persona", "DEVOPS_ENGINEER",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.1,
                "purpose", "Creates project structure, pom.xml, and configuration files",
                "codeExample", "@Action\nProjectStructure generateProjectStructure(Architecture architecture, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.1))\n        .withPromptContributor(DEVOPS_ENGINEER)\n        .createObject(\"Generate project structure...\", ProjectStructure.class);\n}"
            );
            case "generateSourceCode" -> Map.of(
                "name", "generateSourceCode",
                "description", "Generates actual Java/Spring Boot code, HTML, CSS, and JavaScript files",
                "persona", "SENIOR_DEVELOPER",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.3,
                "purpose", "Creates production-ready Spring Boot controllers, services, entities, and frontend files",
                "codeExample", "@AchievesGoal(description = \"Complete Spring Boot application generated\")\n@Action\nMap<String, String> generateSourceCode(ProjectStructure structure, List<ImplementationStep> steps) {\n    return codeGenerationService.generateSpringBootProject(structure, steps);\n}"
            );
            default -> Map.of(
                "name", actionName,
                "description", "Unknown action",
                "persona", "AI Agent",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.7,
                "purpose", "Generic action processing",
                "codeExample", "@Action\nObject processAction(Object input, OperationContext context) {\n    return context.ai().createObject(\"Process...\", Object.class);\n}"
            );
        };
        
        return ResponseEntity.ok(details);
    }

    @GetMapping("/domain-model")
    public ResponseEntity<Map<String, Object>> getDomainModel() {
        return ResponseEntity.ok(Map.of(
            "models", List.of(
                Map.of(
                    "name", "Task",
                    "type", "record",
                    "description", "Represents the input task to be analyzed",
                    "fields", List.of(
                        Map.of("name", "description", "type", "String", "description", "Task description"),
                        Map.of("name", "category", "type", "String", "description", "Task category"),
                        Map.of("name", "complexity", "type", "String", "description", "Task complexity level")
                    )
                ),
                Map.of(
                    "name", "Step",
                    "type", "record",
                    "description", "Represents an individual action step",
                    "fields", List.of(
                        Map.of("name", "description", "type", "String", "description", "Step description"),
                        Map.of("name", "priority", "type", "int", "description", "Step priority order")
                    )
                ),
                Map.of(
                    "name", "ExecutionPlan",
                    "type", "record",
                    "description", "Final execution plan with all components",
                    "fields", List.of(
                        Map.of("name", "summary", "type", "String", "description", "Plan summary"),
                        Map.of("name", "estimatedTime", "type", "String", "description", "Estimated completion time"),
                        Map.of("name", "steps", "type", "List<Step>", "description", "Ordered list of execution steps")
                    )
                )
            ),
            "relationships", List.of(
                Map.of("from", "Task", "to", "Step", "relationship", "decomposes into"),
                Map.of("from", "Step", "to", "ExecutionPlan", "relationship", "composes"),
                Map.of("from", "Task", "to", "ExecutionPlan", "relationship", "results in")
            )
        ));
    }

    // New agent action methods for code generation
    
    private Map<String, Object> analyzeApplicationRequirements(String taskDescription) {
        Map<String, Object> aiResponse = callGenAiService(taskDescription, "ANALYZE_REQUIREMENTS");
        return aiResponse;
    }
    
    private Map<String, Object> defineApplicationArchitecture(Map<String, Object> requirements) {
        String requirementsJson = requirements.toString();
        Map<String, Object> aiResponse = callGenAiService(requirementsJson, "DESIGN_ARCHITECTURE");
        return aiResponse;
    }
    
    private List<Map<String, Object>> breakDownIntoImplementationSteps(Map<String, Object> architecture) {
        String architectureJson = architecture.toString();
        Map<String, Object> aiResponse = callGenAiService(architectureJson, "BREAK_DOWN_STEPS");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) aiResponse.get("steps");
        return steps != null ? steps : List.of();
    }
    
    private Map<String, Object> generateProjectStructure(Map<String, Object> architecture) {
        String architectureJson = architecture.toString();
        Map<String, Object> aiResponse = callGenAiService(architectureJson, "GENERATE_PROJECT_STRUCTURE");
        return aiResponse;
    }

    private Map<String, Object> callGenAiService(String taskDescription) {
        return callGenAiService(taskDescription, "BREAK_DOWN_STEPS");
    }
    
    private Map<String, Object> callGenAiService(String taskDescription, String promptType) {
        try {
            // Get GenAI service credentials from environment
            String genAiBaseUrl = environment.getProperty("GENAI_BASE_URL");
            String genAiApiKey = environment.getProperty("GENAI_API_KEY");
            
            if (genAiBaseUrl == null || genAiApiKey == null) {
                System.out.println("GenAI service not configured, using mock response");
                return createMockAiResponse(taskDescription);
            }
            
            // Create the prompt based on prompt type
            String prompt = buildPromptForType(taskDescription, promptType);
            
            // Prepare the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(genAiApiKey);
            
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 1000
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
                    
                    // Parse the JSON content from the AI response
                    return objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
                }
            }
            
            System.out.println("GenAI API call failed, using mock response");
            return createMockAiResponse(taskDescription);
            
        } catch (Exception e) {
            System.err.println("Error calling GenAI service: " + e.getMessage());
            e.printStackTrace();
            return createMockAiResponse(taskDescription);
        }
    }
    
    private Map<String, Object> createMockAiResponse(String taskDescription) {
        // Generate different responses based on task content
        String analysis;
        List<Map<String, Object>> steps;
        String summary;
        String estimatedTime;
        
        if (taskDescription.toLowerCase().contains("podcast")) {
            analysis = "Podcast creation requires content planning, technical setup, and production coordination";
            steps = List.of(
                Map.of("description", "Research podcast topics and target audience", "priority", 1),
                Map.of("description", "Plan episode structure and content calendar", "priority", 2),
                Map.of("description", "Set up recording equipment and software", "priority", 3),
                Map.of("description", "Record and edit first few episodes", "priority", 4),
                Map.of("description", "Create podcast artwork and branding", "priority", 5),
                Map.of("description", "Set up hosting platform and distribution", "priority", 6),
                Map.of("description", "Launch and promote the podcast", "priority", 7),
                Map.of("description", "Monitor performance and gather feedback", "priority", 8)
            );
            summary = "Comprehensive podcast series development plan with sustainable technology focus";
            estimatedTime = "3-4 weeks";
        } else if (taskDescription.toLowerCase().contains("mobile app") || taskDescription.toLowerCase().contains("app")) {
            analysis = "Mobile app development involves design, development, testing, and deployment phases";
            steps = List.of(
                Map.of("description", "Define app requirements and user stories", "priority", 1),
                Map.of("description", "Create wireframes and UI/UX design", "priority", 2),
                Map.of("description", "Set up development environment and architecture", "priority", 3),
                Map.of("description", "Implement core features and functionality", "priority", 4),
                Map.of("description", "Integrate social features and APIs", "priority", 5),
                Map.of("description", "Implement user authentication and data storage", "priority", 6),
                Map.of("description", "Conduct thorough testing and bug fixes", "priority", 7),
                Map.of("description", "Prepare for app store submission", "priority", 8),
                Map.of("description", "Deploy to app stores and monitor performance", "priority", 9),
                Map.of("description", "Gather user feedback and iterate", "priority", 10)
            );
            summary = "Complete mobile app development lifecycle with social features";
            estimatedTime = "4-6 weeks";
        } else if (taskDescription.toLowerCase().contains("marketing") || taskDescription.toLowerCase().contains("campaign")) {
            analysis = "Marketing campaign requires market research, strategy development, and multi-channel execution";
            steps = List.of(
                Map.of("description", "Conduct market research and competitor analysis", "priority", 1),
                Map.of("description", "Define target audience and buyer personas", "priority", 2),
                Map.of("description", "Develop campaign messaging and positioning", "priority", 3),
                Map.of("description", "Create marketing materials and content", "priority", 4),
                Map.of("description", "Set up digital marketing channels", "priority", 5),
                Map.of("description", "Plan PR and media outreach strategy", "priority", 6),
                Map.of("description", "Execute campaign across all channels", "priority", 7),
                Map.of("description", "Monitor performance and adjust strategy", "priority", 8),
                Map.of("description", "Analyze results and ROI", "priority", 9),
                Map.of("description", "Plan follow-up and retention strategies", "priority", 10)
            );
            summary = "Comprehensive marketing campaign for eco-friendly product launch";
            estimatedTime = "2-3 weeks";
        } else if (taskDescription.toLowerCase().contains("retreat") || taskDescription.toLowerCase().contains("company")) {
            analysis = "Company retreat planning involves logistics, activities, and budget management";
            steps = List.of(
                Map.of("description", "Set retreat objectives and budget", "priority", 1),
                Map.of("description", "Research and book venue and accommodations", "priority", 2),
                Map.of("description", "Plan team building activities and agenda", "priority", 3),
                Map.of("description", "Arrange transportation and logistics", "priority", 4),
                Map.of("description", "Coordinate catering and meals", "priority", 5),
                Map.of("description", "Send invitations and collect RSVPs", "priority", 6),
                Map.of("description", "Prepare materials and equipment", "priority", 7),
                Map.of("description", "Execute retreat and manage activities", "priority", 8),
                Map.of("description", "Gather feedback and evaluate success", "priority", 9)
            );
            summary = "Complete company retreat planning and execution for 50 employees";
            estimatedTime = "2-3 weeks";
        } else {
            // Generic response for custom tasks
            analysis = "Complex task requiring systematic planning and execution";
            steps = List.of(
                Map.of("description", "Analyze requirements and constraints", "priority", 1),
                Map.of("description", "Research best practices and resources", "priority", 2),
                Map.of("description", "Create detailed project plan", "priority", 3),
                Map.of("description", "Gather necessary resources and tools", "priority", 4),
                Map.of("description", "Execute core implementation", "priority", 5),
                Map.of("description", "Test and validate results", "priority", 6),
                Map.of("description", "Refine and optimize", "priority", 7),
                Map.of("description", "Document and deliver final results", "priority", 8)
            );
            summary = "Systematic execution plan for: " + taskDescription;
            estimatedTime = "2-4 weeks";
        }
        
        return Map.of(
            "analysis", analysis,
            "steps", steps,
            "summary", summary,
            "estimatedTime", estimatedTime
        );
    }

    private Map<String, Object> createExecutionTrace(String taskDescription, Map<String, Object> aiResponse) {
        return Map.of(
            "taskDescription", taskDescription,
            "actionSteps", List.of(
                Map.of(
                    "actionName", "analyzeApplicationRequirements",
                    "stepNumber", 1,
                    "description", "Analyzing web application requirements and technical specifications",
                    "reasoning", Map.of(
                        "thought", "Examining the task to identify: application type (REST API, full-stack, microservice), required entities and relationships, API endpoints needed, data persistence requirements, authentication needs, frontend requirements, and integration points.",
                        "persona", "SOLUTION_ARCHITECT",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.3,
                        "why", "Using SOLUTION_ARCHITECT persona with low temperature for precise technical analysis and requirement extraction. This ensures we capture all necessary components for a production-ready Spring Boot application.",
                        "considerations", List.of(
                            "Spring Boot version compatibility",
                            "Database choice (H2, PostgreSQL, MySQL)",
                            "REST API design patterns",
                            "Security requirements",
                            "Frontend technology stack"
                        )
                    ),
                    "input", taskDescription,
                    "output", aiResponse.get("analysis"),
                    "duration", "2.5s",
                    "timestamp", Instant.now().minusSeconds(8).toString()
                ),
                Map.of(
                    "actionName", "defineApplicationArchitecture",
                    "stepNumber", 2,
                    "description", "Defining Spring Boot application architecture and technology stack",
                    "reasoning", Map.of(
                        "thought", "Designing the overall application architecture including: layered architecture (controllers, services, repositories), dependency management, configuration structure, database schema design, and integration patterns.",
                        "persona", "SOLUTION_ARCHITECT",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.2,
                        "why", "Using SOLUTION_ARCHITECT persona with very low temperature for consistent architectural decisions and adherence to Spring Boot best practices.",
                        "considerations", List.of(
                            "Spring Boot layered architecture",
                            "Dependency injection patterns",
                            "Database access patterns (JPA/Hibernate)",
                            "Configuration management",
                            "Error handling strategies"
                        )
                    ),
                    "input", aiResponse.get("analysis"),
                    "output", aiResponse.get("architecture"),
                    "duration", "2.0s",
                    "timestamp", Instant.now().minusSeconds(6).toString()
                ),
                Map.of(
                    "actionName", "breakDownIntoImplementationSteps",
                    "stepNumber", 3,
                    "description", "Breaking down architecture into detailed implementation steps",
                    "reasoning", Map.of(
                        "thought", "Creating specific, actionable implementation steps with clear dependencies. Each step includes code generation tasks, file creation requirements, and testing considerations.",
                        "persona", "SOFTWARE_ENGINEER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.4,
                        "why", "Using SOFTWARE_ENGINEER persona with moderate temperature for practical implementation planning while maintaining technical accuracy.",
                        "considerations", List.of(
                            "Implementation order and dependencies",
                            "Code generation requirements",
                            "File structure and organization",
                            "Testing and validation steps",
                            "Documentation needs"
                        )
                    ),
                    "input", aiResponse.get("architecture"),
                    "output", aiResponse.get("steps"),
                    "duration", "1.8s",
                    "timestamp", Instant.now().minusSeconds(4).toString()
                ),
                Map.of(
                    "actionName", "generateProjectStructure",
                    "stepNumber", 4,
                    "description", "Generating Maven project structure and configuration files",
                    "reasoning", Map.of(
                        "thought", "Creating the complete project structure including Maven configuration (pom.xml), application properties, directory structure, and initial configuration files.",
                        "persona", "DEVOPS_ENGINEER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.1,
                        "why", "Using DEVOPS_ENGINEER persona with very low temperature for precise project structure and configuration management.",
                        "considerations", List.of(
                            "Maven dependency management",
                            "Project directory structure",
                            "Configuration file setup",
                            "Build and packaging requirements",
                            "Environment-specific configurations"
                        )
                    ),
                    "input", aiResponse.get("steps"),
                    "output", "Project structure and configuration",
                    "duration", "1.0s",
                    "timestamp", Instant.now().minusSeconds(2).toString()
                )
            ),
            "finalResult", Map.of(
                "summary", aiResponse.get("summary"),
                "estimatedTime", aiResponse.get("estimatedTime"),
                "steps", aiResponse.get("steps")
            ),
            "startTime", Instant.now().minusSeconds(10).toString(),
            "endTime", Instant.now().toString(),
            "executionTimeMs", 5500L
        );
    }

    private Map<String, Object> createMockExecutionTrace(String taskDescription) {
        return createExecutionTrace(taskDescription, Map.of(
            "analysis", "Web application requirements analysis: " + taskDescription,
            "architecture", "Spring Boot layered architecture with REST API, service layer, and data access layer",
            "steps", List.of(
                Map.of("description", "Set up Spring Boot project structure", "priority", 1),
                Map.of("description", "Create entity models and repositories", "priority", 2),
                Map.of("description", "Implement service layer with business logic", "priority", 3),
                Map.of("description", "Create REST controllers with API endpoints", "priority", 4),
                Map.of("description", "Add frontend HTML/CSS/JavaScript files", "priority", 5),
                Map.of("description", "Configure database and test the application", "priority", 6)
            ),
            "summary", "Complete Spring Boot application implementation plan for: " + taskDescription,
            "estimatedTime", "2-3 hours"
        ));
    }
    
    private String buildPromptForType(String input, String promptType) {
        return switch (promptType) {
            case "ANALYZE_REQUIREMENTS" -> String.format("""
                You are a senior solution architect. Analyze the following web application task and extract detailed technical requirements.
                
                Task: %s
                
                Please provide a JSON response with the following structure:
                {
                    "projectName": "Extracted project name",
                    "applicationType": "REST API, Full-stack Web App, or Microservice",
                    "entities": ["Entity1", "Entity2", "Entity3"],
                    "apiEndpoints": ["GET /api/entity1", "POST /api/entity1", "PUT /api/entity1/{id}", "DELETE /api/entity1/{id}"],
                    "databaseRequirements": "Database type and schema requirements",
                    "frontendRequirements": "HTML/CSS/JS requirements",
                    "authenticationNeeded": true/false,
                    "complexity": "Simple, Moderate, or Complex",
                    "estimatedFiles": 15,
                    "technologies": ["Spring Boot", "JPA", "H2 Database", "HTML", "CSS", "JavaScript"]
                }
                
                Focus on:
                1. Identifying all entities and their relationships
                2. Determining required API endpoints
                3. Assessing frontend complexity
                4. Identifying authentication needs
                5. Estimating project scope
                """, input);
                
            case "DESIGN_ARCHITECTURE" -> String.format("""
                You are a solution architect. Design the technical architecture for a Spring Boot application based on these requirements:
                
                Requirements: %s
                
                Please provide a JSON response with the following structure:
                {
                    "architecture": "Layered Architecture, MVC, or Microservices",
                    "layers": ["Controller", "Service", "Repository", "Entity"],
                    "dependencies": ["spring-boot-starter-web", "spring-boot-starter-data-jpa", "h2", "spring-boot-starter-test"],
                    "projectStructure": {
                        "src/main/java/com/example/controller": "REST controllers",
                        "src/main/java/com/example/service": "Business logic services",
                        "src/main/java/com/example/entity": "JPA entities",
                        "src/main/java/com/example/repository": "Data repositories",
                        "src/main/resources/static": "Frontend files"
                    },
                    "databaseSchema": "Database design and relationships",
                    "apiDesign": "REST API design patterns",
                    "securityConsiderations": "Authentication and authorization approach"
                }
                
                Focus on:
                1. Spring Boot best practices
                2. Clean architecture principles
                3. Scalable design patterns
                4. Security considerations
                """, input);
                
            case "BREAK_DOWN_STEPS" -> String.format("""
                You are a senior software engineer. Break down the following architecture into detailed implementation steps.
                
                Architecture: %s
                
                Please provide a JSON response with the following structure:
                {
                    "steps": [
                        {
                            "stepNumber": 1,
                            "description": "Create Maven project structure with pom.xml",
                            "category": "Project Setup",
                            "estimatedTime": "30 minutes",
                            "filesToGenerate": ["pom.xml", "src/main/java/com/example/Application.java"],
                            "dependencies": []
                        },
                        {
                            "stepNumber": 2,
                            "description": "Create JPA entities with proper annotations",
                            "category": "Data Layer",
                            "estimatedTime": "45 minutes",
                            "filesToGenerate": ["src/main/java/com/example/entity/*.java"],
                            "dependencies": [1]
                        }
                    ],
                    "totalEstimatedTime": "4-6 hours",
                    "complexity": "Moderate"
                }
                
                Focus on:
                1. Logical implementation order
                2. Dependency management between steps
                3. Realistic time estimates
                4. File generation requirements
                """, input);
                
            case "GENERATE_PROJECT_STRUCTURE" -> String.format("""
                You are a DevOps engineer. Generate the complete project structure for a Spring Boot application.
                
                Architecture: %s
                
                Please provide a JSON response with the following structure:
                {
                    "projectName": "sanitized-project-name",
                    "directoryStructure": {
                        "src/main/java/com/example": "Main Java source directory",
                        "src/main/resources": "Configuration and static resources",
                        "src/test/java": "Test source directory"
                    },
                    "mavenDependencies": ["spring-boot-starter-web", "spring-boot-starter-data-jpa", "h2"],
                    "configurationFiles": ["application.properties", "pom.xml"],
                    "sourceFiles": ["Application.java", "controllers/*.java", "services/*.java", "entities/*.java"]
                }
                
                Focus on:
                1. Maven standard directory layout
                2. Spring Boot conventions
                3. Proper package structure
                4. Configuration management
                """, input);
                
            default -> String.format("""
                You are an expert project analyst. Break down the following task into specific, actionable steps.
                
                Task: %s
                
                Please provide a JSON response with the following structure:
                {
                    "analysis": "Brief analysis of the task complexity and requirements",
                    "steps": [
                        {"description": "Specific actionable step", "priority": 1},
                        {"description": "Another specific step", "priority": 2}
                    ],
                    "summary": "Brief summary of the execution plan",
                    "estimatedTime": "Realistic time estimate (e.g., '2-3 weeks', '1 month')"
                }
                
                Make sure the steps are:
                1. Specific and actionable
                2. In logical order
                3. Appropriate for the task complexity
                4. Realistic in scope
                """, input);
        };
    }
    
    private Map<String, Object> createCodeGenerationTrace(
            String taskDescription, 
            Map<String, Object> requirements, 
            Map<String, Object> architecture, 
            List<Map<String, Object>> steps, 
            Map<String, String> generatedFiles) {
        
        return Map.of(
            "taskDescription", taskDescription,
            "projectName", requirements.getOrDefault("projectName", "generated-spring-app"),
            "actionSteps", List.of(
                Map.of(
                    "actionName", "analyzeApplicationRequirements",
                    "stepNumber", 1,
                    "description", "Analyzing web application requirements and technical specifications",
                    "reasoning", Map.of(
                        "thought", "Examining the task to identify: application type (REST API, full-stack, microservice), required entities and relationships, API endpoints needed, data persistence requirements, authentication needs, frontend requirements, and integration points.",
                        "persona", "SOLUTION_ARCHITECT",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.3,
                        "why", "Using SOLUTION_ARCHITECT persona with low temperature for precise technical analysis and requirement extraction. This ensures we capture all necessary components for a production-ready Spring Boot application.",
                        "considerations", List.of(
                            "Spring Boot version compatibility",
                            "Database choice (H2, PostgreSQL, MySQL)",
                            "REST API design patterns",
                            "Security requirements",
                            "Frontend technology stack"
                        )
                    ),
                    "input", taskDescription,
                    "output", requirements,
                    "duration", "2.5s",
                    "timestamp", Instant.now().minusSeconds(20).toString()
                ),
                Map.of(
                    "actionName", "defineApplicationArchitecture",
                    "stepNumber", 2,
                    "description", "Designing Spring Boot application architecture and technology stack",
                    "reasoning", Map.of(
                        "thought", "Based on the requirements analysis, determining the optimal architecture pattern, technology stack, project structure, and design patterns. Considering scalability, maintainability, and Spring Boot best practices.",
                        "persona", "SOLUTION_ARCHITECT",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.2,
                        "why", "Using SOLUTION_ARCHITECT persona with very low temperature for precise architectural decisions. This ensures consistent, production-ready architecture following Spring Boot conventions.",
                        "considerations", List.of(
                            "Layered architecture vs microservices",
                            "Dependency injection patterns",
                            "Database access patterns",
                            "API design principles",
                            "Security architecture"
                        )
                    ),
                    "input", requirements,
                    "output", architecture,
                    "duration", "3.0s",
                    "timestamp", Instant.now().minusSeconds(16).toString()
                ),
                Map.of(
                    "actionName", "breakDownIntoImplementationSteps",
                    "stepNumber", 3,
                    "description", "Breaking down architecture into detailed implementation steps",
                    "reasoning", Map.of(
                        "thought", "Converting the architectural design into specific, actionable implementation steps. Considering dependencies between components, optimal development order, and realistic time estimates for each step.",
                        "persona", "SOFTWARE_ENGINEER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.4,
                        "why", "Using SOFTWARE_ENGINEER persona with moderate temperature for creative problem-solving while maintaining logical structure. This balances innovation with practical implementation considerations.",
                        "considerations", List.of(
                            "Step dependencies and order",
                            "File generation requirements",
                            "Testing strategy",
                            "Code quality standards",
                            "Development workflow"
                        )
                    ),
                    "input", architecture,
                    "output", steps,
                    "duration", "2.8s",
                    "timestamp", Instant.now().minusSeconds(12).toString()
                ),
                Map.of(
                    "actionName", "generateProjectStructure",
                    "stepNumber", 4,
                    "description", "Generating Maven project structure and configuration files",
                    "reasoning", Map.of(
                        "thought", "Creating the complete Maven project structure with proper directory layout, dependencies, and configuration files. Ensuring compliance with Spring Boot conventions and Maven best practices.",
                        "persona", "DEVOPS_ENGINEER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.1,
                        "why", "Using DEVOPS_ENGINEER persona with very low temperature for precise, consistent project structure generation. This ensures standard Maven layout and proper dependency management.",
                        "considerations", List.of(
                            "Maven directory structure",
                            "Dependency versions",
                            "Build configuration",
                            "Resource management",
                            "Test structure"
                        )
                    ),
                    "input", architecture,
                    "output", Map.of("projectStructure", "Generated", "files", List.of("pom.xml", "application.properties")),
                    "duration", "1.5s",
                    "timestamp", Instant.now().minusSeconds(8).toString()
                ),
                Map.of(
                    "actionName", "generateSourceCode",
                    "stepNumber", 5,
                    "description", "Generating actual Java/Spring Boot code, HTML, CSS, and JavaScript files",
                    "reasoning", Map.of(
                        "thought", "Using GenAI to generate production-ready Spring Boot code including controllers, services, entities, repositories, and frontend files. Ensuring code follows Spring Boot best practices, proper annotations, and clean architecture principles.",
                        "persona", "SENIOR_DEVELOPER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.3,
                        "why", "Using SENIOR_DEVELOPER persona with low temperature for consistent, high-quality code generation. This ensures production-ready code with proper Spring Boot annotations and best practices.",
                        "considerations", List.of(
                            "Spring Boot annotations",
                            "Clean code principles",
                            "Error handling",
                            "REST API conventions",
                            "Frontend integration"
                        )
                    ),
                    "input", steps,
                    "output", Map.of("generatedFiles", generatedFiles.size(), "fileTypes", List.of("Java", "HTML", "CSS", "JavaScript", "XML", "Properties")),
                    "duration", "8.0s",
                    "timestamp", Instant.now().minusSeconds(4).toString()
                )
            ),
            "generatedFiles", generatedFiles,
            "finalResult", Map.of(
                "projectName", requirements.getOrDefault("projectName", "generated-spring-app"),
                "totalFiles", generatedFiles.size(),
                "fileTypes", List.of("Java", "HTML", "CSS", "JavaScript", "XML", "Properties"),
                "estimatedBuildTime", "5-10 minutes",
                "readyToRun", true
            ),
            "startTime", Instant.now().minusSeconds(25).toString(),
            "endTime", Instant.now().toString(),
            "executionTimeMs", 18000L
        );
    }
    
    private Map<String, Object> createMockCodeGenerationTrace(String taskDescription) {
        return createCodeGenerationTrace(
            taskDescription,
            Map.of(
                "projectName", "mock-spring-app",
                "applicationType", "REST API",
                "entities", List.of("User", "Post"),
                "complexity", "Moderate"
            ),
            Map.of(
                "architecture", "Layered Architecture",
                "layers", List.of("Controller", "Service", "Repository", "Entity")
            ),
            List.of(
                Map.of("stepNumber", 1, "description", "Create Maven project structure", "category", "Project Setup"),
                Map.of("stepNumber", 2, "description", "Generate JPA entities", "category", "Data Layer"),
                Map.of("stepNumber", 3, "description", "Create REST controllers", "category", "API Layer")
            ),
            Map.of(
                "pom.xml", "<?xml version=\"1.0\"?>...",
                "Application.java", "package com.example;...",
                "index.html", "<!DOCTYPE html>..."
            )
        );
    }
}
