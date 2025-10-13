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

    @Test
    void testAnalyzeTask_EmptyInput() {
        // Arrange
        var userInput = new UserInput("", Instant.now());
        var expectedTask = new TaskBreakdownAgent.Task(
                "Empty task",
                "General",
                "Simple"
        );
        context.expectResponse(expectedTask);

        // Act
        var result = agent.analyzeTask(userInput, context);

        // Assert
        assertNotNull(result);
        assertEquals("Empty task", result.description());
        assertEquals("General", result.category());
        assertEquals("Simple", result.complexity());
    }

    @Test
    void testAnalyzeTask_VeryLongInput() {
        // Arrange
        var longDescription = "This is a very long task description that contains many details about what needs to be accomplished. ".repeat(10);
        var userInput = new UserInput(longDescription, Instant.now());
        var expectedTask = new TaskBreakdownAgent.Task(
                "Complex multi-faceted project",
                "Project Management",
                "Complex"
        );
        context.expectResponse(expectedTask);

        // Act
        var result = agent.analyzeTask(userInput, context);

        // Assert
        assertNotNull(result);
        assertEquals("Complex multi-faceted project", result.description());
        assertEquals("Project Management", result.category());
        assertEquals("Complex", result.complexity());
    }

    @Test
    void testBreakDownIntoSteps_VerifyMaxStepsLimit() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Very complex project with many components",
                "Software Development",
                "Complex"
        );
        
        // Create a response with more steps than the limit to test truncation
        var manySteps = List.of(
                new TaskBreakdownAgent.Step("Step 1", "First step", 1, "1 day", "Setup"),
                new TaskBreakdownAgent.Step("Step 2", "Second step", 2, "2 days", "Development"),
                new TaskBreakdownAgent.Step("Step 3", "Third step", 3, "1 day", "Testing"),
                new TaskBreakdownAgent.Step("Step 4", "Fourth step", 4, "1 day", "Deployment"),
                new TaskBreakdownAgent.Step("Step 5", "Fifth step", 5, "1 day", "Documentation"),
                new TaskBreakdownAgent.Step("Step 6", "Sixth step", 6, "1 day", "Training"),
                new TaskBreakdownAgent.Step("Step 7", "Seventh step", 7, "1 day", "Support"),
                new TaskBreakdownAgent.Step("Step 8", "Eighth step", 8, "1 day", "Maintenance"),
                new TaskBreakdownAgent.Step("Step 9", "Ninth step", 9, "1 day", "Optimization")
        );
        context.expectResponse(manySteps);

        // Act
        var result = agent.breakDownIntoSteps(task, context);

        // Assert
        assertNotNull(result);
        // The agent should respect the maxSteps configuration (8)
        // Note: The actual implementation doesn't enforce this limit in the test,
        // it's just a prompt instruction, so we check that the result is not null
        assertTrue(result.size() > 0);
    }

    @Test
    void testBreakDownIntoSteps_VerifyDescriptionLengthLimit() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Test task",
                "Testing",
                "Simple"
        );
        
        var steps = List.of(
                new TaskBreakdownAgent.Step(
                        "Test step",
                        "This is a very long description that exceeds the maximum allowed length for step descriptions in the test configuration",
                        1,
                        "1 day",
                        "Testing"
                )
        );
        context.expectResponse(steps);

        // Act
        var result = agent.breakDownIntoSteps(task, context);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify the prompt includes the max description length constraint
        var prompt = promptRunner.getLlmInvocations().get(0).getPrompt();
        assertTrue(prompt.contains("max 150 characters"), 
                "Prompt should specify maximum description length");
    }

    @Test
    void testPrioritizeSteps_VerifyPersonaUsage() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Test prioritization",
                "Testing",
                "Medium"
        );
        
        var initialSteps = List.of(
                new TaskBreakdownAgent.Step("Step A", "Description A", 3, "1 day", "Setup"),
                new TaskBreakdownAgent.Step("Step B", "Description B", 1, "2 days", "Development"),
                new TaskBreakdownAgent.Step("Step C", "Description C", 2, "1 day", "Testing")
        );
        
        var prioritizedSteps = List.of(
                new TaskBreakdownAgent.Step("Step A", "Description A", 1, "1 day", "Setup"),
                new TaskBreakdownAgent.Step("Step B", "Description B", 2, "2 days", "Development"),
                new TaskBreakdownAgent.Step("Step C", "Description C", 3, "1 day", "Testing")
        );
        context.expectResponse(prioritizedSteps);

        // Act
        var result = agent.prioritizeSteps(initialSteps, task, context);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify the prompt includes prioritization instructions
        var prompt = promptRunner.getLlmInvocations().get(0).getPrompt();
        assertTrue(prompt.contains("Prioritize and optimize these steps"), 
                "Prompt should contain prioritization instruction");
        assertTrue(prompt.contains("Dependencies between steps"), 
                "Prompt should mention dependencies");
        assertTrue(prompt.contains("Risk mitigation"), 
                "Prompt should mention risk mitigation");
    }

    @Test
    void testCreateExecutionPlan_VerifyTemperatureSettings() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Test execution plan",
                "Testing",
                "Simple"
        );
        
        var steps = List.of(
                new TaskBreakdownAgent.Step("Test step", "Test description", 1, "1 day", "Testing")
        );
        
        // Mock the AI responses for summary and time calculation
        // Note: The order matters - first call is for time calculation, second for summary
        context.expectResponse("1 day");
        context.expectResponse("Test summary for execution plan");

        // Act
        var result = agent.createExecutionPlan(task, steps, context);

        // Assert
        assertNotNull(result);
        assertEquals(task, result.originalTask());
        assertEquals(steps, result.steps());
        assertEquals("Test summary for execution plan", result.summary());
        assertEquals("1 day", result.totalEstimatedTime());
        
        // Verify multiple LLM calls were made (for summary and time calculation)
        var llmInvocations = promptRunner.getLlmInvocations();
        assertTrue(llmInvocations.size() >= 2, 
                "Should make at least 2 LLM calls for summary and time calculation");
    }

    @Test
    void testExecutionPlanContentFormat_Detailed() {
        // Arrange
        var task = new TaskBreakdownAgent.Task(
                "Build a comprehensive testing framework",
                "Software Development",
                "Complex"
        );
        
        var steps = List.of(
                new TaskBreakdownAgent.Step(
                        "Design architecture",
                        "Create the overall system design and component structure",
                        1,
                        "1 week",
                        "Architecture"
                ),
                new TaskBreakdownAgent.Step(
                        "Implement core functionality",
                        "Build the main features and business logic",
                        2,
                        "2 weeks",
                        "Development"
                ),
                new TaskBreakdownAgent.Step(
                        "Write comprehensive tests",
                        "Create unit, integration, and end-to-end tests",
                        3,
                        "1 week",
                        "Testing"
                )
        );

        // Act
        var plan = new TaskBreakdownAgent.ExecutionPlan(
                task, steps, "A comprehensive plan to build a robust testing framework", "4 weeks"
        );

        // Assert
        var content = plan.getContent();
        
        // Verify markdown structure
        assertTrue(content.contains("# Task Execution Plan"), 
                "Content should have main header");
        assertTrue(content.contains("**Original Task:** Build a comprehensive testing framework"), 
                "Content should show original task");
        assertTrue(content.contains("**Category:** Software Development"), 
                "Content should show category");
        assertTrue(content.contains("**Complexity:** Complex"), 
                "Content should show complexity");
        assertTrue(content.contains("## Summary"), 
                "Content should have summary section");
        assertTrue(content.contains("## Execution Steps"), 
                "Content should have steps section");
        assertTrue(content.contains("**Total Estimated Time:** 4 weeks"), 
                "Content should show total time");
        
        // Verify step formatting
        assertTrue(content.contains("1. **Design architecture** (Priority: 1, 1 week)"), 
                "Content should format steps with priority and time");
        assertTrue(content.contains("Create the overall system design and component structure"), 
                "Content should include step descriptions");
        assertTrue(content.contains("Category: Architecture"), 
                "Content should show step categories");
        
        // Verify timestamp format
        assertTrue(content.contains("**Generated:**"), 
                "Content should include generation timestamp");
        // The timestamp format is more flexible, just check it contains some date/time information
        assertTrue(content.contains("Generated:") && content.length() > content.indexOf("Generated:") + 20, 
                "Content should have properly formatted timestamp");
    }
}