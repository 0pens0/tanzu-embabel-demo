package com.embabel.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GenAI Demo Application for Cloud Foundry deployment.
 * This version uses the GenAI service directly to demonstrate AI agent capabilities.
 */
@SpringBootApplication
public class GenAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenAiDemoApplication.class, args);
    }
}
