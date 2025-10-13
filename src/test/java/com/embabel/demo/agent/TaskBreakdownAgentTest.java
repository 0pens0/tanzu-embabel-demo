package com.embabel.demo.agent;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.unit.FakeOperationContext;
import com.embabel.agent.testing.unit.FakePromptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskBreakdownAgent demonstrating Embabel's testing capabilities.
 * 
 * These tests show how to:
 * - Use FakeOperationContext for isolated unit testing
 * - Mock LLM responses with FakePromptRunner
 * - Test individual @Action methods
 * - Verify prompt content and structure
 * - Test domain model creation and validation
 */
class TaskBreakdownAgentTest {

    private TaskBreakdownAgent agent;
    private FakeOperationContext context;
    private FakePromptRunner promptRunner;

    @BeforeEach
    void setUp() {
        agent = new TaskBreakdownAgent(8, 150);
        context = FakeOperationContext.create();
        promptRunner = (FakePromptRunner) context.promptRunner();
    }

    @Test
    void testAnalyzeTask() {
        // Arrange
        var userInput = new UserInput("Build a mobile app for recipe sharing", Instant.now());
        var expectedTask = new TaskBreakdownAgent.Task(
                "Build a mobile app for recipe sharing",
                "Software Development",
                "Complex"
        );
        context.expectResponse(expectedTask);

        // Act
        var result = agent.analyzeTask(userInput, context);

        // Assert
        assertNotNull(result);
        assertEquals("Build a mobile app for recipe sharing", result.description());
        assertEquals("Software Development", result.category());
        assertEquals("Complex", result.complexity());

        // Verify the prompt was constructed correctly
        var llmInvocations = promptRunner.getLlmInvocations();
        assertEquals(1, llmInvocations.size());
        
        var prompt = llmInvocations.get(0).getPrompt();
        assertTrue(prompt.contains("Build a mobile app for recipe sharing"), 
                "Prompt should contain the user input");
        assertTrue(prompt.contains("Analyze the following user task"), 
                "Prompt should contain analysis instruction");
    }

    @Test
    void testBreakDownIntoSteps() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Plan a company retreat",
                "Business Planning",
                "Medium"
        );
        
        var expectedSteps = List.of(
                new TaskBreakdownAgent.Step(
                        "Define objectives and budget",
                        "Establish clear goals and allocate budget for the retreat",
                        1,
                        "2-3 days",
                        "Planning"
                ),
                new TaskBreakdownAgent.Step(
                        "Choose location and dates",
                        "Research and select appropriate venue and timing",
                        2,
                        "1 week",
                        "Logistics"
                )
        );
        context.expectResponse(expectedSteps);

        // Act
        var result = agent.breakDownIntoSteps(task, context);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Define objectives and budget", result.get(0).title());
        assertEquals(1, result.get(0).priority());

        // Verify the prompt
        var prompt = promptRunner.getLlmInvocations().get(0).getPrompt();
        assertTrue(prompt.contains("Plan a company retreat"), 
                "Prompt should contain the task description");
        assertTrue(prompt.contains("Break down this task into specific, actionable steps"), 
                "Prompt should contain breakdown instruction");
        assertTrue(prompt.contains("8 or fewer steps"), 
                "Prompt should specify maximum steps");
    }

    @Test
    void testPrioritizeSteps() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Launch a new product",
                "Product Development",
                "Complex"
        );
        
        var initialSteps = List.of(
                new TaskBreakdownAgent.Step("Market research", "Research target market", 3, "1 week", "Research"),
                new TaskBreakdownAgent.Step("Product development", "Build the product", 1, "4 weeks", "Development"),
                new TaskBreakdownAgent.Step("Marketing campaign", "Create marketing materials", 2, "2 weeks", "Marketing")
        );
        
        var prioritizedSteps = List.of(
                new TaskBreakdownAgent.Step("Market research", "Research target market", 1, "1 week", "Research"),
                new TaskBreakdownAgent.Step("Product development", "Build the product", 2, "4 weeks", "Development"),
                new TaskBreakdownAgent.Step("Marketing campaign", "Create marketing materials", 3, "2 weeks", "Marketing")
        );
        context.expectResponse(prioritizedSteps);

        // Act
        var result = agent.prioritizeSteps(initialSteps, task, context);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify prioritization (market research should be first)
        assertEquals("Market research", result.get(0).title());
        assertEquals(1, result.get(0).priority());

        // Verify the prompt
        var prompt = promptRunner.getLlmInvocations().get(0).getPrompt();
        assertTrue(prompt.contains("Launch a new product"), 
                "Prompt should contain the task description");
        assertTrue(prompt.contains("Prioritize and optimize these steps"), 
                "Prompt should contain prioritization instruction");
    }

    @Test
    void testCreateExecutionPlan() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Organize a charity event",
                "Event Planning",
                "Medium"
        );
        
        var steps = List.of(
                new TaskBreakdownAgent.Step("Set goals and budget", "Define event objectives", 1, "3 days", "Planning"),
                new TaskBreakdownAgent.Step("Book venue", "Find and reserve location", 2, "1 week", "Logistics")
        );
        
        // Mock the AI responses for summary and time calculation
        context.expectResponse("A comprehensive plan to organize a successful charity event with clear objectives and logistics.");
        context.expectResponse("2-3 weeks");

        // Act
        var result = agent.createExecutionPlan(task, steps, context);

        // Assert
        assertNotNull(result);
        assertEquals(task, result.originalTask());
        assertEquals(steps, result.steps());
        assertNotNull(result.summary());
        assertNotNull(result.totalEstimatedTime());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getContent());

        // Verify the content format
        var content = result.getContent();
        assertTrue(content.contains("Task Execution Plan"), 
                "Content should contain plan title");
        assertTrue(content.contains("Organize a charity event"), 
                "Content should contain original task");
        assertTrue(content.contains("Set goals and budget"), 
                "Content should contain step titles");
    }

    @Test
    void testExecutionPlanContentFormat() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Test task",
                "Testing",
                "Simple"
        );
        
        var steps = List.of(
                new TaskBreakdownAgent.Step("Step 1", "First step", 1, "1 day", "Setup"),
                new TaskBreakdownAgent.Step("Step 2", "Second step", 2, "2 days", "Execution")
        );

        // Act
        var plan = new TaskBreakdownAgent.ExecutionPlan(
                task, steps, "Test summary", "3 days"
        );

        // Assert
        var content = plan.getContent();
        assertTrue(content.contains("# Task Execution Plan"), 
                "Content should have markdown header");
        assertTrue(content.contains("**Original Task:** Test task"), 
                "Content should show original task");
        assertTrue(content.contains("**Category:** Testing"), 
                "Content should show category");
        assertTrue(content.contains("**Complexity:** Simple"), 
                "Content should show complexity");
        assertTrue(content.contains("## Summary"), 
                "Content should have summary section");
        assertTrue(content.contains("## Execution Steps"), 
                "Content should have steps section");
        assertTrue(content.contains("**Total Estimated Time:** 3 days"), 
                "Content should show total time");
    }

    @Test
    void testAgentConfiguration() {
        // Test with custom configuration
        var customAgent = new TaskBreakdownAgent(5, 100);
        
        // This test verifies that the agent can be configured with different parameters
        assertNotNull(customAgent);
        
        // The actual configuration values are used in the prompts,
        // so we can't easily test them without mocking the entire context
        // But we can verify the agent is created successfully
    }
}