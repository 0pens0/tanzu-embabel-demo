package com.embabel.demo.service;

import com.embabel.demo.api.dto.ExecutionTrace;
import com.embabel.demo.agent.TaskBreakdownAgent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AgentExecutionService.
 * Tests the full agent execution flow with mocked LLM responses.
 */
@SpringBootTest
@ActiveProfiles("test")
class AgentExecutionServiceIntegrationTest {

    @Autowired
    private AgentExecutionService agentExecutionService;

    @Test
    void testExecuteTaskBreakdown_SimpleTask() {
        // Arrange
        var taskDescription = "Plan a team meeting";

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);

        // Assert
        assertNotNull(executionTrace);
        assertEquals(taskDescription, executionTrace.taskDescription());
        assertNotNull(executionTrace.actionSteps());
        assertFalse(executionTrace.actionSteps().isEmpty());
        assertNotNull(executionTrace.finalResult());
        assertNotNull(executionTrace.startTime());
        assertNotNull(executionTrace.endTime());
        assertTrue(executionTrace.executionTimeMs() > 0);

        // Verify action steps
        var actionSteps = executionTrace.actionSteps();
        assertEquals(4, actionSteps.size());
        
        // Check that all expected action steps are present
        var stepNames = actionSteps.stream()
                .map(ExecutionTrace.ActionStep::actionName)
                .toList();
        
        assertTrue(stepNames.contains("analyzeTask"));
        assertTrue(stepNames.contains("breakDownIntoSteps"));
        assertTrue(stepNames.contains("prioritizeSteps"));
        assertTrue(stepNames.contains("createExecutionPlan"));

        // Verify final result structure
        var finalResult = executionTrace.finalResult();
        assertNotNull(finalResult.originalTask());
        assertNotNull(finalResult.steps());
        assertFalse(finalResult.steps().isEmpty());
        assertNotNull(finalResult.summary());
        assertNotNull(finalResult.totalEstimatedTime());
        assertNotNull(finalResult.getTimestamp());
        assertNotNull(finalResult.getContent());
    }

    @Test
    void testExecuteTaskBreakdown_ComplexTask() {
        // Arrange
        var taskDescription = "Develop a comprehensive customer relationship management system with AI-powered analytics";

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);

        // Assert
        assertNotNull(executionTrace);
        assertEquals(taskDescription, executionTrace.taskDescription());
        assertNotNull(executionTrace.finalResult());
        
        // Verify the task was analyzed correctly
        var originalTask = executionTrace.finalResult().originalTask();
        assertNotNull(originalTask.description());
        assertNotNull(originalTask.category());
        assertNotNull(originalTask.complexity());
        
        // Verify steps were generated
        var steps = executionTrace.finalResult().steps();
        assertFalse(steps.isEmpty());
        assertTrue(steps.size() <= 5); // Based on test configuration maxSteps
        
        // Verify each step has required fields
        for (var step : steps) {
            assertNotNull(step.title());
            assertNotNull(step.description());
            assertTrue(step.priority() > 0);
            assertNotNull(step.estimatedTime());
            assertNotNull(step.category());
            assertTrue(step.description().length() <= 100); // Based on test configuration
        }
    }

    @Test
    void testExecuteTaskBreakdown_ContentFormat() {
        // Arrange
        var taskDescription = "Create a mobile app";

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);

        // Assert
        var content = executionTrace.finalResult().getContent();
        assertNotNull(content);
        assertTrue(content.contains("# Task Execution Plan"));
        assertTrue(content.contains("**Original Task:**"));
        assertTrue(content.contains("**Category:**"));
        assertTrue(content.contains("**Complexity:**"));
        assertTrue(content.contains("## Summary"));
        assertTrue(content.contains("## Execution Steps"));
        assertTrue(content.contains("**Total Estimated Time:**"));
        assertTrue(content.contains("**Generated:**"));
    }

    @Test
    void testExecuteTaskBreakdown_EmptyTask() {
        // Arrange
        var taskDescription = "";

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);

        // Assert
        assertNotNull(executionTrace);
        assertEquals(taskDescription, executionTrace.taskDescription());
        // Should still have action steps even for empty input
        assertNotNull(executionTrace.actionSteps());
        assertFalse(executionTrace.actionSteps().isEmpty());
    }

    @Test
    void testGetFrameworkInfo() {
        // Act
        var frameworkInfo = agentExecutionService.getFrameworkInfo();

        // Assert
        assertNotNull(frameworkInfo);
        assertEquals("Embabel Agent Framework", frameworkInfo.name());
        assertEquals("0.1.3", frameworkInfo.version());
        assertNotNull(frameworkInfo.description());
        assertFalse(frameworkInfo.features().isEmpty());
        assertFalse(frameworkInfo.benefits().isEmpty());

        // Verify feature structure
        for (var feature : frameworkInfo.features()) {
            assertNotNull(feature.name());
            assertNotNull(feature.shortDescription());
            assertNotNull(feature.detailedDescription());
        }

        // Verify benefits
        for (var benefit : frameworkInfo.benefits()) {
            assertNotNull(benefit);
            assertFalse(benefit.trim().isEmpty());
        }
    }

    @Test
    void testExecuteTaskBreakdown_ExecutionTraceTiming() {
        // Arrange
        var taskDescription = "Test timing";
        var startTime = System.currentTimeMillis();

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);
        var endTime = System.currentTimeMillis();

        // Assert
        assertNotNull(executionTrace.startTime());
        assertNotNull(executionTrace.endTime());
        assertTrue(executionTrace.endTime().isAfter(executionTrace.startTime()));
        assertTrue(executionTrace.executionTimeMs() > 0);
        assertTrue(executionTrace.executionTimeMs() <= (endTime - startTime + 1000)); // Allow some tolerance
    }

    @Test
    void testExecuteTaskBreakdown_ActionStepTiming() {
        // Arrange
        var taskDescription = "Test action step timing";

        // Act
        var executionTrace = agentExecutionService.executeTaskBreakdown(taskDescription);

        // Assert
        var actionSteps = executionTrace.actionSteps();
        assertFalse(actionSteps.isEmpty());
        
        // Verify each action step has timing information
        for (var step : actionSteps) {
            assertNotNull(step.timestamp());
            assertTrue(step.durationMs() >= 0);
        }
        
        // Verify steps are in chronological order
        for (int i = 1; i < actionSteps.size(); i++) {
            var previousStep = actionSteps.get(i - 1);
            var currentStep = actionSteps.get(i);
            assertTrue(currentStep.timestamp().isAfter(previousStep.timestamp()) || 
                      currentStep.timestamp().equals(previousStep.timestamp()));
        }
    }
}
