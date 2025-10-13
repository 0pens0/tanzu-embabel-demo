package com.embabel.demo.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.domain.library.HasContent;
import com.embabel.agent.prompt.persona.Persona;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;
import com.embabel.common.core.types.Timestamped;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TaskBreakdownAgent demonstrates Embabel's core concepts:
 * 
 * - @Agent: Defines this class as an Embabel agent
 * - @Action: Methods that represent steps the agent can take
 * - @AchievesGoal: Methods that represent the final goal achievement
 * - Domain Model: Strongly-typed records (Task, Step, ExecutionPlan)
 * - Personas: Different LLM personalities for different tasks
 * - LLM Mixing: Using different temperatures for creative vs analytical tasks
 */
@Agent(description = "Break down user tasks into actionable steps and create execution plans")
@Profile("!test")
public class TaskBreakdownAgent {

    /**
     * Domain Model: Strongly-typed record representing a user's task
     */
    public record Task(
            String description,
            String category,
            String complexity
    ) {
    }

    /**
     * Domain Model: Individual step in the execution plan
     */
    public record Step(
            String title,
            String description,
            int priority,
            String estimatedTime,
            String category
    ) {
    }

    /**
     * Domain Model: Final execution plan with all steps
     */
    public record ExecutionPlan(
            Task originalTask,
            List<Step> steps,
            String summary,
            String totalEstimatedTime
    ) implements HasContent, Timestamped {

        @Override
        @NonNull
        public Instant getTimestamp() {
            return Instant.now();
        }

        @Override
        @NonNull
        public String getContent() {
            var timestamp = getTimestamp().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm"));
            
            var stepsText = steps.stream()
                    .map(step -> String.format("""
                            %d. **%s** (Priority: %d, %s)
                               %s
                               Category: %s
                            """, 
                            step.priority(), step.title(), step.priority(), 
                            step.estimatedTime(), step.description(), step.category()))
                    .reduce("", (a, b) -> a + b + "\n");

            return String.format("""
                    # Task Execution Plan
                    
                    **Original Task:** %s
                    **Category:** %s | **Complexity:** %s
                    
                    ## Summary
                    %s
                    
                    ## Execution Steps
                    %s
                    
                    **Total Estimated Time:** %s
                    **Generated:** %s
                    """,
                    originalTask.description(),
                    originalTask.category(),
                    originalTask.complexity(),
                    summary,
                    stepsText,
                    totalEstimatedTime,
                    timestamp
            ).trim();
        }
    }

    // Personas for different types of analysis
    private static final RoleGoalBackstory ANALYST = RoleGoalBackstory
            .withRole("Project Management Analyst")
            .andGoal("Break down complex tasks into manageable, actionable steps")
            .andBackstory("Expert in project management with 10+ years of experience in software development and business analysis");

    private static final Persona PRIORITIZER = new Persona(
            "Task Prioritizer",
            "Senior Project Manager",
            "Analytical and strategic",
            "Help teams focus on high-impact activities and manage dependencies"
    );

    private final int maxSteps;
    private final int maxStepDescriptionLength;

    public TaskBreakdownAgent(
            @Value("${task.maxSteps:8}") int maxSteps,
            @Value("${task.maxStepDescriptionLength:150}") int maxStepDescriptionLength
    ) {
        this.maxSteps = maxSteps;
        this.maxStepDescriptionLength = maxStepDescriptionLength;
    }

    /**
     * @Action: Analyze the user's task to understand its nature and complexity
     */
    @Action
    Task analyzeTask(UserInput userInput, OperationContext context) {
        return context.ai()
                .withLlm(LlmOptions
                        .withAutoLlm()
                        .withTemperature(0.3) // Lower temperature for analytical tasks
                )
                .withPromptContributor(ANALYST)
                .createObject(String.format("""
                        Analyze the following user task and categorize it.
                        
                        User Input: "%s"
                        
                        Please provide:
                        1. A clear, concise description of the task
                        2. A category (e.g., "Software Development", "Business Planning", "Learning", "Creative Project", "Research", "Administrative")
                        3. Complexity level (Simple, Medium, Complex)
                        
                        Be specific and actionable in your analysis.
                        """, userInput.getContent().trim()), Task.class);
    }

    /**
     * @Action: Break down the task into specific, actionable steps
     */
    @Action
    List<Step> breakDownIntoSteps(Task task, OperationContext context) {
        return context.ai()
                .withLlm(LlmOptions
                        .withAutoLlm()
                        .withTemperature(0.4) // Slightly higher for creative step generation
                )
                .withPromptContributor(ANALYST)
                .createObject(String.format("""
                        Break down this task into specific, actionable steps.
                        
                        Task: %s
                        Category: %s
                        Complexity: %s
                        
                        Create %d or fewer steps that are:
                        - Specific and actionable
                        - In logical order
                        - Have clear deliverables
                        - Include time estimates
                        - Categorized appropriately
                        
                        Each step should have:
                        - title: Clear, concise action
                        - description: What needs to be done (max %d characters)
                        - priority: 1 (highest) to %d (lowest)
                        - estimatedTime: Realistic time estimate
                        - category: Sub-category of the step
                        """, 
                        task.description(), task.category(), task.complexity(),
                        maxSteps, maxStepDescriptionLength, maxSteps), 
                        List.class);
    }

    /**
     * @Action: Prioritize and optimize the steps based on dependencies and impact
     */
    @Action
    List<Step> prioritizeSteps(List<Step> steps, Task task, OperationContext context) {
        return context.ai()
                .withLlm(LlmOptions
                        .withAutoLlm()
                        .withTemperature(0.2) // Very low temperature for analytical prioritization
                )
                .withPromptContributor(PRIORITIZER)
                .createObject(String.format("""
                        Prioritize and optimize these steps for the task: "%s"
                        
                        Current steps:
                        %s
                        
                        Please reorder and adjust priorities considering:
                        - Dependencies between steps
                        - Risk mitigation
                        - Resource availability
                        - Impact on overall success
                        - Time constraints
                        
                        Return the optimized list with updated priorities (1 = highest priority).
                        """, 
                        task.description(),
                        steps.stream()
                                .map(step -> String.format("- %s (Priority: %d, %s)", 
                                        step.title(), step.priority(), step.estimatedTime()))
                                .reduce("", (a, b) -> a + "\n" + b)), 
                        List.class);
    }

    /**
     * @AchievesGoal: Create the final execution plan
     * This method represents the goal achievement and is exported for external access
     */
    @AchievesGoal(
            description = "A comprehensive execution plan has been created with prioritized steps",
            export = @Export(remote = true, name = "createExecutionPlan")
    )
    @Action
    ExecutionPlan createExecutionPlan(
            Task task, 
            List<Step> prioritizedSteps, 
            OperationContext context
    ) {
        // Calculate total estimated time
        var totalTime = context.ai()
                .withLlm(LlmOptions
                        .withAutoLlm()
                        .withTemperature(0.1) // Very low for calculation
                )
                .generateText(String.format("""
                        Calculate the total estimated time for these steps:
                        
                        %s
                        
                        Provide a realistic total time estimate considering:
                        - Some steps can be done in parallel
                        - Buffer time for unexpected issues
                        - Dependencies between steps
                        
                        Return only the time estimate (e.g., "2-3 weeks", "3-4 days", "1-2 months").
                        """, 
                        prioritizedSteps.stream()
                                .map(step -> String.format("- %s: %s", step.title(), step.estimatedTime()))
                                .reduce("", (a, b) -> a + "\n" + b)));

        // Generate summary
        var summary = context.ai()
                .withLlm(LlmOptions
                        .withAutoLlm()
                        .withTemperature(0.3)
                )
                .withPromptContributor(ANALYST)
                .generateText(String.format("""
                        Create a brief executive summary for this execution plan:
                        
                        Task: %s
                        Steps: %d
                        Total Time: %s
                        
                        Provide a 2-3 sentence summary highlighting:
                        - Key objectives
                        - Main phases or milestones
                        - Critical success factors
                        """, 
                        task.description(), 
                        prioritizedSteps.size(), 
                        totalTime));

        return new ExecutionPlan(
                task,
                prioritizedSteps,
                summary.trim(),
                totalTime.trim()
        );
    }
}