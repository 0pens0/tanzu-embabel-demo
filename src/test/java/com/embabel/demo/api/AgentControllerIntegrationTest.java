package com.embabel.demo.api;

import com.embabel.demo.api.dto.ExecutionTrace;
import com.embabel.demo.api.dto.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AgentController REST endpoints.
 * Tests the full HTTP request/response cycle with Spring Boot context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBreakdownTask_ValidRequest() throws Exception {
        // Arrange
        var request = new TaskRequest("Build a simple web application");
        var requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/breakdown-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskDescription").value("Build a simple web application"))
                .andExpect(jsonPath("$.actionSteps").isArray())
                .andExpect(jsonPath("$.actionSteps").isNotEmpty())
                .andExpect(jsonPath("$.finalResult").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.executionTimeMs").isNumber())
                .andReturn();

        // Verify the response can be deserialized
        var responseJson = result.getResponse().getContentAsString();
        var executionTrace = objectMapper.readValue(responseJson, ExecutionTrace.class);
        
        assertNotNull(executionTrace);
        assertEquals("Build a simple web application", executionTrace.taskDescription());
        assertFalse(executionTrace.actionSteps().isEmpty());
        assertNotNull(executionTrace.finalResult());
        assertTrue(executionTrace.executionTimeMs() > 0);
    }

    @Test
    void testBreakdownTask_EmptyDescription() throws Exception {
        // Arrange
        var request = new TaskRequest("");
        var requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/breakdown-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBreakdownTask_TooLongDescription() throws Exception {
        // Arrange
        var longDescription = "a".repeat(1001); // Exceeds 1000 character limit
        var request = new TaskRequest(longDescription);
        var requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/breakdown-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBreakdownTask_InvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/breakdown-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFrameworkInfo() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/framework-info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Embabel Agent Framework"))
                .andExpect(jsonPath("$.version").value("0.1.3"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.features").isArray())
                .andExpect(jsonPath("$.benefits").isArray())
                .andReturn();

        // Verify the response structure
        var responseJson = result.getResponse().getContentAsString();
        var frameworkInfo = objectMapper.readValue(responseJson, 
                com.embabel.demo.service.AgentExecutionService.FrameworkInfo.class);
        
        assertNotNull(frameworkInfo);
        assertEquals("Embabel Agent Framework", frameworkInfo.name());
        assertEquals("0.1.3", frameworkInfo.version());
        assertFalse(frameworkInfo.features().isEmpty());
        assertFalse(frameworkInfo.benefits().isEmpty());
    }

    @Test
    void testGetHealth() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Tanzu Embabel Demo"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetSampleTasks() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/sample-tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andReturn();

        // Verify the response can be deserialized
        var responseJson = result.getResponse().getContentAsString();
        var sampleTasks = objectMapper.readValue(responseJson, String[].class);
        
        assertNotNull(sampleTasks);
        assertTrue(sampleTasks.length > 0);
        assertTrue(sampleTasks[0].length() > 0);
    }

    @Test
    void testBreakdownTask_ComplexTask() throws Exception {
        // Arrange
        var request = new TaskRequest("Create a comprehensive marketing strategy for launching a new AI-powered productivity tool targeting enterprise customers");
        var requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/breakdown-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify the response structure
        var responseJson = result.getResponse().getContentAsString();
        var executionTrace = objectMapper.readValue(responseJson, ExecutionTrace.class);
        
        assertNotNull(executionTrace);
        assertNotNull(executionTrace.finalResult());
        assertNotNull(executionTrace.finalResult().originalTask());
        assertFalse(executionTrace.finalResult().steps().isEmpty());
        assertNotNull(executionTrace.finalResult().summary());
        assertNotNull(executionTrace.finalResult().totalEstimatedTime());
    }

    @Test
    void testCorsHeaders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/health")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
