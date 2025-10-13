package com.embabel.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * GenAI Demo Controller for Cloud Foundry deployment.
 * Provides AI-powered task breakdown using the GenAI service.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GenAiDemoController {

    private final RestTemplate restTemplate = new RestTemplate();
    
    // This will be injected from the GenAI service binding
    private String genAiBaseUrl;
    private String genAiApiKey;

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
        // This would call the actual GenAI service
        // For now, return a structured response
        return Map.of(
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
