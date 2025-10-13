package com.embabel.demo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for task breakdown requests
 */
public record TaskRequest(
        @NotBlank(message = "Task description is required")
        @Size(max = 1000, message = "Task description must be less than 1000 characters")
        String taskDescription
) {
}