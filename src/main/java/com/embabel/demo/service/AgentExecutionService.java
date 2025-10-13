package com.embabel.demo.service;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.demo.agent.TaskBreakdownAgent;
import com.embabel.demo.api.dto.ExecutionTrace;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for executing the TaskBreakdownAgent and capturing execution traces.
 * This demonstrates how to programmatically invoke Embabel agents and track their execution.
 */
@Service
public class AgentExecutionService {

    private final AgentPlatform agentPlatform;

    public AgentExecutionService(AgentPlatform agentPlatform) {
        this.agentPlatform = agentPlatform;
    }

    /**
     * Execute the TaskBreakdownAgent and return both the result and execution trace.
     * This method demonstrates how to invoke an Embabel agent programmatically.
     */
    public ExecutionTrace executeTaskBreakdown(String taskDescription) {
        var startTime = Instant.now();
        var actionSteps = new ArrayList<ExecutionTrace.ActionStep>();

        try {
            // Create user input from the task description
            var userInput = new UserInput(taskDescription, startTime);

            // Execute the agent using AgentInvocation
            // This will automatically execute all @Action methods in sequence
            // until the @AchievesGoal method is reached
            var executionPlan = AgentInvocation
                    .create(agentPlatform, TaskBreakdownAgent.ExecutionPlan.class)
                    .invoke(userInput);

            var endTime = Instant.now();
            var executionTimeMs = endTime.toEpochMilli() - startTime.toEpochMilli();

            // For this demo, we'll create a simplified execution trace
            // In a real implementation, you might want to use AOP or other mechanisms
            // to capture detailed execution steps
            actionSteps.add(new ExecutionTrace.ActionStep(
                    "analyzeTask",
                    "Analyze the user's task to understand its nature and complexity",
                    userInput,
                    executionPlan.originalTask(),
                    startTime,
                    executionTimeMs / 4 // Simulated duration
            ));

            actionSteps.add(new ExecutionTrace.ActionStep(
                    "breakDownIntoSteps",
                    "Break down the task into specific, actionable steps",
                    executionPlan.originalTask(),
                    executionPlan.steps(),
                    startTime.plusSeconds(1),
                    executionTimeMs / 4
            ));

            actionSteps.add(new ExecutionTrace.ActionStep(
                    "prioritizeSteps",
                    "Prioritize and optimize the steps based on dependencies and impact",
                    executionPlan.steps(),
                    executionPlan.steps(),
                    startTime.plusSeconds(2),
                    executionTimeMs / 4
            ));

            actionSteps.add(new ExecutionTrace.ActionStep(
                    "createExecutionPlan",
                    "Create the final execution plan with summary and time estimates",
                    executionPlan.steps(),
                    executionPlan,
                    startTime.plusSeconds(3),
                    executionTimeMs / 4
            ));

            return new ExecutionTrace(
                    taskDescription,
                    actionSteps,
                    executionPlan,
                    startTime,
                    endTime,
                    executionTimeMs
            );

        } catch (Exception e) {
            var endTime = Instant.now();
            var executionTimeMs = endTime.toEpochMilli() - startTime.toEpochMilli();

            // Add error step to the trace
            actionSteps.add(new ExecutionTrace.ActionStep(
                    "error",
                    "Error occurred during execution: " + e.getMessage(),
                    taskDescription,
                    null,
                    endTime,
                    executionTimeMs
            ));

            return new ExecutionTrace(
                    taskDescription,
                    actionSteps,
                    null,
                    startTime,
                    endTime,
                    executionTimeMs
            );
        }
    }

    /**
     * Get framework information for the educational frontend
     */
    public FrameworkInfo getFrameworkInfo() {
        return new FrameworkInfo(
                "Embabel Agent Framework",
                "0.1.3",
                "Framework for authoring agentic flows on the JVM that seamlessly mix LLM-prompted interactions with code and domain models.",
                List.of(
                        new FrameworkFeature(
                                "@Agent",
                                "Defines a class as an Embabel agent",
                                "Marks the class as an agent that can perform actions and achieve goals"
                        ),
                        new FrameworkFeature(
                                "@Action",
                                "Methods that represent steps an agent can take",
                                "Each @Action method performs a specific task and can be chained together"
                        ),
                        new FrameworkFeature(
                                "@AchievesGoal",
                                "Methods that represent goal achievement",
                                "These methods are called when the agent has completed its objective"
                        ),
                        new FrameworkFeature(
                                "Domain Model",
                                "Strongly-typed objects that inform the agent's behavior",
                                "Uses Java records and classes to define the data structures the agent works with"
                        ),
                        new FrameworkFeature(
                                "Personas",
                                "Different LLM personalities for different tasks",
                                "Allows using different AI personalities optimized for specific types of work"
                        ),
                        new FrameworkFeature(
                                "LLM Mixing",
                                "Using different models and temperatures for different tasks",
                                "Enables cost-effective solutions by using the right model for each task"
                        )
                ),
                List.of(
                        "Sophisticated planning beyond finite state machines",
                        "Strong typing and object-oriented programming benefits",
                        "Built-in testability with unit and integration test support",
                        "Seamless Spring Framework integration",
                        "Platform abstraction for flexible deployment",
                        "Dynamic replanning based on execution results"
                )
        );
    }

    public record FrameworkInfo(
            String name,
            String version,
            String description,
            List<FrameworkFeature> features,
            List<String> benefits
    ) {
    }

    public record FrameworkFeature(
            String name,
            String shortDescription,
            String detailedDescription
    ) {
    }
}