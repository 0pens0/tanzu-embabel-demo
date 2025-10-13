package com.embabel.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
    
    public GenAiDemoController(Environment environment) {
        this.environment = environment;
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
            Map<String, Object> aiResponse = callGenAiService(taskDescription);
            
            // Create execution trace similar to Embabel agent
            Map<String, Object> executionTrace = createExecutionTrace(taskDescription, aiResponse);
            
            return ResponseEntity.ok(executionTrace);
        } catch (Exception e) {
            // Fallback to mock response if GenAI service fails
            return ResponseEntity.ok(createMockExecutionTrace(taskDescription));
        }
    }

    @GetMapping("/sample-tasks")
    public ResponseEntity<List<Map<String, String>>> getSampleTasks() {
        return ResponseEntity.ok(List.of(
            Map.of(
                "title", "Create a podcast series about sustainable technology with 10 episodes",
                "description", "Plan and produce a podcast series covering sustainable technology innovations",
                "category", "Content Creation"
            ),
            Map.of(
                "title", "Develop a mobile app for tracking personal fitness goals with social features",
                "description", "Build a fitness tracking app with social sharing and community features",
                "category", "Mobile Development"
            ),
            Map.of(
                "title", "Plan a marketing campaign for launching a new eco-friendly product line",
                "description", "Create a comprehensive marketing strategy for sustainable product launch",
                "category", "Business Strategy"
            ),
            Map.of(
                "title", "Organize a company retreat for 50 employees with team building activities",
                "description", "Plan logistics, activities, and budget for a comprehensive company retreat",
                "category", "Event Planning"
            )
        ));
    }

    @GetMapping("/scenarios")
    public ResponseEntity<List<Map<String, Object>>> getScenarios() {
        return ResponseEntity.ok(List.of(
            Map.of(
                "id", "podcast",
                "title", "Create a podcast series about sustainable technology with 10 episodes",
                "description", "Plan and produce a podcast series covering sustainable technology innovations",
                "category", "Content Creation",
                "complexity", "Complex",
                "estimatedSteps", 8,
                "expectedDuration", "3-4 weeks",
                "highlights", List.of(
                    "Uses ANALYST persona for research",
                    "Demonstrates multi-step breakdown",
                    "Shows dependency prioritization"
                ),
                "expectedActions", List.of("analyzeTask", "breakDownIntoSteps", "prioritizeSteps", "createExecutionPlan")
            ),
            Map.of(
                "id", "mobile-app",
                "title", "Develop a mobile app for tracking personal fitness goals with social features",
                "description", "Build a fitness tracking app with social sharing and community features",
                "category", "Mobile Development",
                "complexity", "Very Complex",
                "estimatedSteps", 12,
                "expectedDuration", "4-6 weeks",
                "highlights", List.of(
                    "Shows technical architecture planning",
                    "Demonstrates user experience considerations",
                    "Includes integration complexity"
                ),
                "expectedActions", List.of("analyzeTask", "breakDownIntoSteps", "prioritizeSteps", "createExecutionPlan")
            ),
            Map.of(
                "id", "marketing-campaign",
                "title", "Plan a marketing campaign for launching a new eco-friendly product line",
                "description", "Create a comprehensive marketing strategy for sustainable product launch",
                "category", "Business Strategy",
                "complexity", "Complex",
                "estimatedSteps", 10,
                "expectedDuration", "2-3 weeks",
                "highlights", List.of(
                    "Shows strategic thinking process",
                    "Demonstrates market analysis",
                    "Includes multi-channel planning"
                ),
                "expectedActions", List.of("analyzeTask", "breakDownIntoSteps", "prioritizeSteps", "createExecutionPlan")
            ),
            Map.of(
                "id", "company-retreat",
                "title", "Organize a company retreat for 50 employees with team building activities",
                "description", "Plan logistics, activities, and budget for a comprehensive company retreat",
                "category", "Event Planning",
                "complexity", "Moderate",
                "estimatedSteps", 6,
                "expectedDuration", "2-3 weeks",
                "highlights", List.of(
                    "Shows logistical planning",
                    "Demonstrates budget considerations",
                    "Includes activity coordination"
                ),
                "expectedActions", List.of("analyzeTask", "breakDownIntoSteps", "prioritizeSteps", "createExecutionPlan")
            )
        ));
    }

    @GetMapping("/action-details/{actionName}")
    public ResponseEntity<Map<String, Object>> getActionDetails(@PathVariable String actionName) {
        Map<String, Object> details = switch (actionName) {
            case "analyzeTask" -> Map.of(
                "name", "analyzeTask",
                "description", "Analyzes task complexity and requirements",
                "persona", "ANALYST",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.3,
                "purpose", "Determines task complexity and identifies key requirements",
                "codeExample", "@Action\nTask analyzeTask(UserInput userInput, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.3))\n        .withPromptContributor(ANALYST)\n        .createObject(\"Analyze this task...\", Task.class);\n}"
            );
            case "breakDownIntoSteps" -> Map.of(
                "name", "breakDownIntoSteps",
                "description", "Breaks down task into actionable steps",
                "persona", "ANALYST",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.4,
                "purpose", "Decomposes complex tasks into manageable, sequential steps",
                "codeExample", "@Action\nList<Step> breakDownIntoSteps(Task task, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.4))\n        .withPromptContributor(ANALYST)\n        .createObject(\"Break down into steps...\", List.class);\n}"
            );
            case "prioritizeSteps" -> Map.of(
                "name", "prioritizeSteps",
                "description", "Prioritizes steps based on dependencies",
                "persona", "PRIORITIZER",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.2,
                "purpose", "Orders steps by dependencies and critical path analysis",
                "codeExample", "@Action\nList<Step> prioritizeSteps(List<Step> steps, OperationContext context) {\n    return context.ai()\n        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.2))\n        .withPromptContributor(PRIORITIZER)\n        .createObject(\"Prioritize these steps...\", List.class);\n}"
            );
            case "createExecutionPlan" -> Map.of(
                "name", "createExecutionPlan",
                "description", "Creates final execution plan with time estimates",
                "persona", "ANALYST",
                "llmModel", "gpt-4o-mini",
                "temperature", 0.3,
                "purpose", "Synthesizes all components into a comprehensive execution plan",
                "codeExample", "@AchievesGoal(description = \"Execution plan created\")\n@Action\nExecutionPlan createExecutionPlan(Task task, List<Step> steps) {\n    return new ExecutionPlan(task, steps, summary, totalTime);\n}"
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

    private Map<String, Object> callGenAiService(String taskDescription) {
        try {
            // Get GenAI service credentials from environment
            String genAiBaseUrl = environment.getProperty("GENAI_BASE_URL");
            String genAiApiKey = environment.getProperty("GENAI_API_KEY");
            
            if (genAiBaseUrl == null || genAiApiKey == null) {
                System.out.println("GenAI service not configured, using mock response");
                return createMockAiResponse(taskDescription);
            }
            
            // Create the prompt for task breakdown
            String prompt = String.format("""
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
                """, taskDescription);
            
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
                    "actionName", "analyzeTask",
                    "stepNumber", 1,
                    "description", "Analyzing task complexity and requirements",
                    "reasoning", Map.of(
                        "thought", "Analyzing task to determine complexity and scope. Evaluating requirements, constraints, and potential challenges.",
                        "persona", "ANALYST",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.3,
                        "why", "Using ANALYST persona with low temperature for accurate complexity assessment and detailed analysis"
                    ),
                    "input", taskDescription,
                    "output", aiResponse.get("analysis"),
                    "duration", "1.5s",
                    "timestamp", Instant.now().minusSeconds(8).toString()
                ),
                Map.of(
                    "actionName", "breakDownIntoSteps",
                    "stepNumber", 2,
                    "description", "Breaking down task into actionable steps",
                    "reasoning", Map.of(
                        "thought", "Decomposing the analyzed task into specific, actionable steps. Considering dependencies and logical flow.",
                        "persona", "ANALYST",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.4,
                        "why", "Using ANALYST persona with slightly higher temperature for creative step generation while maintaining structure"
                    ),
                    "input", aiResponse.get("analysis"),
                    "output", aiResponse.get("steps"),
                    "duration", "2.0s",
                    "timestamp", Instant.now().minusSeconds(6).toString()
                ),
                Map.of(
                    "actionName", "prioritizeSteps",
                    "stepNumber", 3,
                    "description", "Prioritizing steps based on dependencies",
                    "reasoning", Map.of(
                        "thought", "Evaluating step dependencies and critical path. Determining optimal execution order for efficiency.",
                        "persona", "PRIORITIZER",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.2,
                        "why", "Using PRIORITIZER persona with very low temperature for precise dependency analysis and logical ordering"
                    ),
                    "input", "List of steps",
                    "output", "Prioritized execution plan",
                    "duration", "0.8s",
                    "timestamp", Instant.now().minusSeconds(4).toString()
                ),
                Map.of(
                    "actionName", "createExecutionPlan",
                    "stepNumber", 4,
                    "description", "Creating final execution plan with time estimates",
                    "reasoning", Map.of(
                        "thought", "Assembling all components into a comprehensive execution plan. Adding time estimates and resource considerations.",
                        "persona", "ANALYST",
                        "llmModel", "gpt-4o-mini",
                        "temperature", 0.3,
                        "why", "Using ANALYST persona for final synthesis and comprehensive plan creation with realistic time estimates"
                    ),
                    "input", "Prioritized steps",
                    "output", Map.of(
                        "summary", aiResponse.get("summary"),
                        "estimatedTime", aiResponse.get("estimatedTime"),
                        "steps", aiResponse.get("steps")
                    ),
                    "duration", "1.2s",
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
            "analysis", "Complex task requiring detailed planning",
            "steps", List.of(
                Map.of("description", "Research and gather requirements", "priority", 1),
                Map.of("description", "Create initial design", "priority", 2),
                Map.of("description", "Implement core functionality", "priority", 3),
                Map.of("description", "Testing and validation", "priority", 4),
                Map.of("description", "Deployment and monitoring", "priority", 5)
            ),
            "summary", "Comprehensive execution plan for: " + taskDescription,
            "estimatedTime", "2-3 weeks"
        ));
    }
}
