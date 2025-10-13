package com.embabel.demo.api.dto;

import com.embabel.demo.agent.TaskBreakdownAgent;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing the execution trace of the TaskBreakdownAgent.
 * Shows each action that was executed and its results.
 */
public record ExecutionTrace(
        String taskDescription,
        List<ActionStep> actionSteps,
        TaskBreakdownAgent.ExecutionPlan finalResult,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Instant startTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Instant endTime,
        long executionTimeMs
) {
    
    /**
     * Represents a single action step in the execution trace
     */
    public record ActionStep(
            String actionName,
            String description,
            Object input,
            Object output,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            Instant timestamp,
            long durationMs
    ) {
    }
}