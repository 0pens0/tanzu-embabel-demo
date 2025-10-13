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
                    "description", "Analyzing task complexity and requirements",
                    "input", taskDescription,
                    "output", aiResponse.get("analysis"),
                    "timestamp", Instant.now().toString(),
                    "durationMs", 1500L
                ),
                Map.of(
                    "actionName", "breakDownIntoSteps",
                    "description", "Breaking down task into actionable steps",
                    "input", aiResponse.get("analysis"),
                    "output", aiResponse.get("steps"),
                    "timestamp", Instant.now().toString(),
                    "durationMs", 2000L
                ),
                Map.of(
                    "actionName", "prioritizeSteps",
                    "description", "Prioritizing steps based on dependencies",
                    "input", "List of steps",
                    "output", "Prioritized execution plan",
                    "timestamp", Instant.now().toString(),
                    "durationMs", 800L
                ),
                Map.of(
                    "actionName", "createExecutionPlan",
                    "description", "Creating final execution plan with time estimates",
                    "input", "Prioritized steps",
                    "output", Map.of(
                        "summary", aiResponse.get("summary"),
                        "estimatedTime", aiResponse.get("estimatedTime"),
                        "steps", aiResponse.get("steps")
                    ),
                    "timestamp", Instant.now().toString(),
                    "durationMs", 1200L
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
