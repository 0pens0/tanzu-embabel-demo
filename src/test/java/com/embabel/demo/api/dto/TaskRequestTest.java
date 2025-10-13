package com.embabel.demo.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskRequest DTO validation.
 * Tests the validation annotations and constraints.
 */
class TaskRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTaskRequest() {
        // Arrange
        var taskRequest = new TaskRequest("Build a mobile application");

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertTrue(violations.isEmpty(), "Valid task request should have no violations");
    }

    @Test
    void testTaskRequest_NullDescription() {
        // Arrange
        var taskRequest = new TaskRequest(null);

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertFalse(violations.isEmpty(), "Null description should have violations");
        assertEquals(1, violations.size());
        
        var violation = violations.iterator().next();
        assertEquals("taskDescription", violation.getPropertyPath().toString());
        assertEquals("Task description is required", violation.getMessage());
    }

    @Test
    void testTaskRequest_EmptyDescription() {
        // Arrange
        var taskRequest = new TaskRequest("");

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertFalse(violations.isEmpty(), "Empty description should have violations");
        assertEquals(1, violations.size());
        
        var violation = violations.iterator().next();
        assertEquals("taskDescription", violation.getPropertyPath().toString());
        assertEquals("Task description is required", violation.getMessage());
    }

    @Test
    void testTaskRequest_BlankDescription() {
        // Arrange
        var taskRequest = new TaskRequest("   ");

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertFalse(violations.isEmpty(), "Blank description should have violations");
        assertEquals(1, violations.size());
        
        var violation = violations.iterator().next();
        assertEquals("taskDescription", violation.getPropertyPath().toString());
        assertEquals("Task description is required", violation.getMessage());
    }

    @Test
    void testTaskRequest_DescriptionTooLong() {
        // Arrange
        var longDescription = "a".repeat(1001); // Exceeds 1000 character limit
        var taskRequest = new TaskRequest(longDescription);

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertFalse(violations.isEmpty(), "Description too long should have violations");
        assertEquals(1, violations.size());
        
        var violation = violations.iterator().next();
        assertEquals("taskDescription", violation.getPropertyPath().toString());
        assertEquals("Task description must be less than 1000 characters", violation.getMessage());
    }

    @Test
    void testTaskRequest_DescriptionAtMaxLength() {
        // Arrange
        var maxLengthDescription = "a".repeat(1000); // Exactly 1000 characters
        var taskRequest = new TaskRequest(maxLengthDescription);

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertTrue(violations.isEmpty(), "Description at max length should be valid");
    }

    @Test
    void testTaskRequest_DescriptionJustUnderMaxLength() {
        // Arrange
        var justUnderMaxDescription = "a".repeat(999); // 999 characters
        var taskRequest = new TaskRequest(justUnderMaxDescription);

        // Act
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

        // Assert
        assertTrue(violations.isEmpty(), "Description just under max length should be valid");
    }

    @Test
    void testTaskRequest_ValidDescriptions() {
        // Test various valid descriptions
        var validDescriptions = new String[]{
                "Simple task",
                "Build a comprehensive customer relationship management system",
                "Plan and execute a company-wide digital transformation initiative",
                "Create a mobile app for recipe sharing with social features",
                "Develop an AI-powered analytics dashboard for business intelligence",
                "Organize a charity fundraising event for local community support"
        };

        for (var description : validDescriptions) {
            // Arrange
            var taskRequest = new TaskRequest(description);

            // Act
            Set<ConstraintViolation<TaskRequest>> violations = validator.validate(taskRequest);

            // Assert
            assertTrue(violations.isEmpty(), 
                    "Description '" + description + "' should be valid");
        }
    }

    @Test
    void testTaskRequest_RecordEquality() {
        // Arrange
        var description = "Test task description";
        var taskRequest1 = new TaskRequest(description);
        var taskRequest2 = new TaskRequest(description);
        var taskRequest3 = new TaskRequest("Different description");

        // Act & Assert
        assertEquals(taskRequest1, taskRequest2, "Equal descriptions should create equal objects");
        assertNotEquals(taskRequest1, taskRequest3, "Different descriptions should create different objects");
        assertEquals(taskRequest1.hashCode(), taskRequest2.hashCode(), "Equal objects should have equal hash codes");
    }

    @Test
    void testTaskRequest_ToString() {
        // Arrange
        var description = "Test task description";
        var taskRequest = new TaskRequest(description);

        // Act
        var toString = taskRequest.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("TaskRequest"));
        assertTrue(toString.contains(description));
    }
}
