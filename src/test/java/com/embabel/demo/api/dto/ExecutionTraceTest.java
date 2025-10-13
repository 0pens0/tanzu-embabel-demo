package com.embabel.demo.api.dto;

import com.embabel.demo.agent.TaskBreakdownAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExecutionTrace DTO.
 * Tests JSON serialization/deserialization and data structure integrity.
 */
class ExecutionTraceTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testExecutionTrace_Serialization() throws JsonProcessingException {
        // Arrange
        var taskDescription = "Test task";
        var startTime = Instant.now();
        var endTime = startTime.plusSeconds(5);
        var executionTimeMs = 5000L;

        var actionSteps = List.of(
                new ExecutionTrace.ActionStep(
                        "analyzeTask",
                        "Analyze the user's task",
                        taskDescription,
                        new TaskBreakdownAgent.Task("Test task", "Testing", "Simple"),
                        startTime,
                        1000L
                ),
                new ExecutionTrace.ActionStep(
                        "createExecutionPlan",
                        "Create final execution plan",
                        List.of(),
                        new TaskBreakdownAgent.ExecutionPlan(
                                new TaskBreakdownAgent.Task("Test task", "Testing", "Simple"),
                                List.of(),
                                "Test summary",
                                "1 day"
                        ),
                        endTime,
                        4000L
                )
        );

        var executionTrace = new ExecutionTrace(
                taskDescription,
                actionSteps,
                new TaskBreakdownAgent.ExecutionPlan(
                        new TaskBreakdownAgent.Task("Test task", "Testing", "Simple"),
                        List.of(),
                        "Test summary",
                        "1 day"
                ),
                startTime,
                endTime,
                executionTimeMs
        );

        // Act
        var json = objectMapper.writeValueAsString(executionTrace);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("taskDescription"));
        assertTrue(json.contains("actionSteps"));
        assertTrue(json.contains("finalResult"));
        assertTrue(json.contains("startTime"));
        assertTrue(json.contains("endTime"));
        assertTrue(json.contains("executionTimeMs"));
    }

    @Test
    void testExecutionTrace_Deserialization() throws JsonProcessingException {
        // Arrange
        var json = """
                {
                    "taskDescription": "Test task",
                    "actionSteps": [
                        {
                            "actionName": "analyzeTask",
                            "description": "Analyze the user's task",
                            "input": "Test task",
                            "output": null,
                            "timestamp": "2023-01-01T10:00:00.000Z",
                            "durationMs": 1000
                        }
                    ],
                    "finalResult": null,
                    "startTime": "2023-01-01T10:00:00.000Z",
                    "endTime": "2023-01-01T10:00:05.000Z",
                    "executionTimeMs": 5000
                }
                """;

        // Act
        var executionTrace = objectMapper.readValue(json, ExecutionTrace.class);

        // Assert
        assertNotNull(executionTrace);
        assertEquals("Test task", executionTrace.taskDescription());
        assertEquals(1, executionTrace.actionSteps().size());
        assertNull(executionTrace.finalResult());
        assertEquals(5000L, executionTrace.executionTimeMs());

        var actionStep = executionTrace.actionSteps().get(0);
        assertEquals("analyzeTask", actionStep.actionName());
        assertEquals("Analyze the user's task", actionStep.description());
        assertEquals("Test task", actionStep.input());
        assertNull(actionStep.output());
        assertEquals(1000L, actionStep.durationMs());
    }

    @Test
    void testActionStep_Serialization() throws JsonProcessingException {
        // Arrange
        var timestamp = Instant.now();
        var actionStep = new ExecutionTrace.ActionStep(
                "testAction",
                "Test action description",
                "input data",
                "output data",
                timestamp,
                2000L
        );

        // Act
        var json = objectMapper.writeValueAsString(actionStep);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("actionName"));
        assertTrue(json.contains("description"));
        assertTrue(json.contains("input"));
        assertTrue(json.contains("output"));
        assertTrue(json.contains("timestamp"));
        assertTrue(json.contains("durationMs"));
    }

    @Test
    void testActionStep_Deserialization() throws JsonProcessingException {
        // Arrange
        var json = """
                {
                    "actionName": "testAction",
                    "description": "Test action description",
                    "input": "input data",
                    "output": "output data",
                    "timestamp": "2023-01-01T10:00:00.000Z",
                    "durationMs": 2000
                }
                """;

        // Act
        var actionStep = objectMapper.readValue(json, ExecutionTrace.ActionStep.class);

        // Assert
        assertNotNull(actionStep);
        assertEquals("testAction", actionStep.actionName());
        assertEquals("Test action description", actionStep.description());
        assertEquals("input data", actionStep.input());
        assertEquals("output data", actionStep.output());
        assertEquals(2000L, actionStep.durationMs());
    }

    @Test
    void testExecutionTrace_WithComplexObjects() throws JsonProcessingException {
        // Arrange
        var task = new TaskBreakdownAgent.Task("Complex task", "Software Development", "Complex");
        var steps = List.of(
                new TaskBreakdownAgent.Step("Step 1", "First step", 1, "1 day", "Setup"),
                new TaskBreakdownAgent.Step("Step 2", "Second step", 2, "2 days", "Development")
        );
        var executionPlan = new TaskBreakdownAgent.ExecutionPlan(task, steps, "Test summary", "3 days");

        var actionStep = new ExecutionTrace.ActionStep(
                "createExecutionPlan",
                "Create final execution plan",
                steps,
                executionPlan,
                Instant.now(),
                3000L
        );

        var executionTrace = new ExecutionTrace(
                "Complex task",
                List.of(actionStep),
                executionPlan,
                Instant.now(),
                Instant.now(),
                5000L
        );

        // Act
        var json = objectMapper.writeValueAsString(executionTrace);
        
        // Note: We can't deserialize the complex objects back due to the ExecutionPlan's getContent() method
        // which is not a record field, so we just verify the serialization works
        assertNotNull(json);
        assertTrue(json.contains("Complex task"));
        assertTrue(json.contains("actionSteps"));
        assertTrue(json.contains("finalResult"));
    }

    @Test
    void testExecutionTrace_RecordEquality() {
        // Arrange
        var timestamp = Instant.now();
        var actionStep = new ExecutionTrace.ActionStep(
                "testAction",
                "Test description",
                "input",
                "output",
                timestamp,
                1000L
        );

        var actionStep1 = new ExecutionTrace.ActionStep(
                "testAction",
                "Test description",
                "input",
                "output",
                timestamp,
                1000L
        );

        var actionStep2 = new ExecutionTrace.ActionStep(
                "differentAction",
                "Test description",
                "input",
                "output",
                timestamp,
                1000L
        );

        // Act & Assert
        assertEquals(actionStep, actionStep1, "Equal action steps should be equal");
        assertNotEquals(actionStep, actionStep2, "Different action steps should not be equal");
        assertEquals(actionStep.hashCode(), actionStep1.hashCode(), "Equal objects should have equal hash codes");
    }

    @Test
    void testExecutionTrace_EmptyActionSteps() {
        // Arrange
        var executionTrace = new ExecutionTrace(
                "Test task",
                List.of(),
                null,
                Instant.now(),
                Instant.now(),
                0L
        );

        // Act & Assert
        assertNotNull(executionTrace);
        assertEquals("Test task", executionTrace.taskDescription());
        assertTrue(executionTrace.actionSteps().isEmpty());
        assertNull(executionTrace.finalResult());
        assertEquals(0L, executionTrace.executionTimeMs());
    }

    @Test
    void testExecutionTrace_JsonFormat() throws JsonProcessingException {
        // Arrange
        var timestamp = Instant.parse("2023-01-01T10:00:00.000Z");
        var executionTrace = new ExecutionTrace(
                "Test task",
                List.of(),
                null,
                timestamp,
                timestamp.plusSeconds(5),
                5000L
        );

        // Act
        var json = objectMapper.writeValueAsString(executionTrace);

        // Assert
        assertTrue(json.contains("\"startTime\":\"2023-01-01T10:00:00Z\""));
        assertTrue(json.contains("\"endTime\":\"2023-01-01T10:00:05Z\""));
        assertTrue(json.contains("\"executionTimeMs\":5000"));
    }
}
