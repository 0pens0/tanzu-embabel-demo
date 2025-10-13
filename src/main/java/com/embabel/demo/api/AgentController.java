package com.embabel.demo.api;

import com.embabel.demo.api.dto.ExecutionTrace;
import com.embabel.demo.api.dto.TaskRequest;
import com.embabel.demo.service.AgentExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for the Embabel demo application.
 * Provides endpoints for:
 * - Executing the TaskBreakdownAgent
 * - Getting framework information
 * - Health checks
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow CORS for frontend
public class AgentController {

    private final AgentExecutionService agentExecutionService;

    public AgentController(AgentExecutionService agentExecutionService) {
        this.agentExecutionService = agentExecutionService;
    }

    /**
     * Execute the TaskBreakdownAgent with the provided task description.
     * Returns both the execution result and a trace of all actions performed.
     */
    @PostMapping("/breakdown-task")
    public ResponseEntity<ExecutionTrace> breakdownTask(@Valid @RequestBody TaskRequest request) {
        try {
            var executionTrace = agentExecutionService.executeTaskBreakdown(request.taskDescription());
            return ResponseEntity.ok(executionTrace);
        } catch (Exception e) {
            // Return error trace
            var errorTrace = new ExecutionTrace(
                    request.taskDescription(),
                    List.of(new ExecutionTrace.ActionStep(
                            "error",
                            "Error occurred: " + e.getMessage(),
                            request.taskDescription(),
                            null,
                            java.time.Instant.now(),
                            0
                    )),
                    null,
                    java.time.Instant.now(),
                    java.time.Instant.now(),
                    0
            );
            return ResponseEntity.internalServerError().body(errorTrace);
        }
    }

    /**
     * Get information about the Embabel framework for the educational frontend.
     */
    @GetMapping("/framework-info")
    public ResponseEntity<AgentExecutionService.FrameworkInfo> getFrameworkInfo() {
        return ResponseEntity.ok(agentExecutionService.getFrameworkInfo());
    }

    /**
     * Health check endpoint for Cloud Foundry and monitoring.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Tanzu Embabel Demo",
                "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * Get sample task suggestions for the demo.
     */
    @GetMapping("/sample-tasks")
    public ResponseEntity<List<String>> getSampleTasks() {
        var sampleTasks = List.of(
                "Build a mobile app for recipe sharing",
                "Plan a company retreat for 50 people",
                "Create a marketing strategy for a new product launch",
                "Develop a training program for new employees",
                "Design a home renovation project",
                "Launch an e-commerce website",
                "Organize a charity fundraising event",
                "Implement a customer feedback system",
                "Create a content marketing calendar",
                "Plan a software migration project"
        );
        return ResponseEntity.ok(sampleTasks);
    }
}