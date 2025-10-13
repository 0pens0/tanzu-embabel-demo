# Embabel Framework Demo Walkthrough
## Step-by-Step Guide to Showcase AI Agent Development with Java

### 🎯 Demo Overview
This walkthrough demonstrates how the Embabel framework enables developers to build sophisticated AI agents using familiar Java programming patterns, Spring Boot integration, and enterprise-grade features.

---

## 📋 Pre-Demo Setup

### 1. Start the Application
```bash
# Navigate to the project directory
cd /path/to/tanzu-embabel-demo

# Start the Spring Boot application
mvn spring-boot:run

# Or if you have the JAR built
java -jar target/tanzu-embabel-demo-0.1.0-SNAPSHOT.jar
```

### 2. Open the Demo Interface
- Navigate to `http://localhost:8080` in your browser
- You should see the Embabel Framework Demo homepage

---

## 🚀 Demo Walkthrough Steps

### Step 1: Introduction to Embabel Framework (2-3 minutes)

**What to Show:**
- The hero section explaining Embabel's purpose
- Key differentiators from other agent frameworks
- Spring Framework heritage and enterprise focus

**Key Points to Emphasize:**
- "Framework for authoring agentic flows on the JVM"
- "Seamlessly mix LLM-prompted interactions with code and domain models"
- Created by the Spring Framework team
- Enterprise-ready with strong typing and testability

**Demo Script:**
> "Embabel represents a new paradigm in AI agent development. Unlike other frameworks that rely on configuration files or string-based approaches, Embabel brings the full power of Java's type system and Spring's dependency injection to AI agent development."

### Step 2: Building Blocks Deep Dive (5-7 minutes)

**What to Show:**
Navigate to the "Building Blocks" section and explain each component:

#### 2.1 @Agent Annotation
- **Code Example:** Show the `@Agent` annotation
- **Key Point:** "This is just a regular Spring bean with an annotation"
- **Demo:** Hover over the code block to highlight it

#### 2.2 @Action Methods
- **Code Example:** Show the `@Action` method signature
- **Key Point:** "Actions are just regular Java methods that can be chained"
- **Demo:** Explain how actions flow from one to another

#### 2.3 @AchievesGoal
- **Code Example:** Show the goal achievement method
- **Key Point:** "This represents the final objective of the agent"
- **Demo:** Explain how this differs from regular actions

#### 2.4 Domain Models (Java Records)
- **Code Example:** Show the `Task` and `Step` records
- **Key Point:** "Strongly-typed, immutable data structures"
- **Demo:** Emphasize compile-time safety and IDE support

#### 2.5 Personas
- **Code Example:** Show the `Persona` configuration
- **Key Point:** "Different AI personalities for different tasks"
- **Demo:** Explain how this optimizes AI behavior

#### 2.6 LLM Mixing
- **Code Example:** Show the `LlmOptions` configuration
- **Key Point:** "Use the right model for each task"
- **Demo:** Explain cost optimization and capability matching

**Demo Script:**
> "Each of these building blocks is a first-class citizen in the Java ecosystem. You get full IDE support, compile-time error checking, and the ability to refactor with confidence. This isn't configuration-driven development - this is real programming."

### Step 3: Benefits Showcase (3-4 minutes)

**What to Show:**
Navigate to the "Why Choose Embabel?" section and highlight key benefits:

#### 3.1 Sophisticated Planning
- **Key Point:** "Goes beyond finite state machines"
- **Demo:** Explain dynamic replanning capabilities

#### 3.2 Strong Typing & OOP
- **Key Point:** "Full object-oriented programming with type safety"
- **Demo:** Emphasize IDE support and refactoring capabilities

#### 3.3 Built-in Testability
- **Key Point:** "Test your agents like any other Spring component"
- **Demo:** Mention the `FakeOperationContext` for unit testing

#### 3.4 Spring Integration
- **Key Point:** "Seamless integration with Spring ecosystem"
- **Demo:** Explain dependency injection, AOP, and enterprise features

**Demo Script:**
> "These aren't just marketing points - they're real advantages that make a difference in production. You can debug your agents with the same tools you use for any Java application. You can write unit tests. You can integrate with existing enterprise infrastructure."

### Step 4: Interactive Demo - Task Breakdown Agent (8-10 minutes)

**What to Show:**
Navigate to the "Interactive Demo" section and demonstrate the live agent:

#### 4.1 Sample Task Selection
- **Demo:** Click on a sample task (e.g., "Build a mobile app for recipe sharing")
- **Key Point:** "Let's see how the agent handles a complex, real-world task"

#### 4.2 Agent Execution Visualization
- **Demo:** Click "Break Down Task" and watch the progress
- **Key Point:** "Watch as the agent executes each @Action method in sequence"
- **Show:** The four-step execution process:
  1. **analyzeTask** - Analyzing task and determining complexity
  2. **breakDownIntoSteps** - Breaking down into actionable steps  
  3. **prioritizeSteps** - Prioritizing steps based on dependencies
  4. **createExecutionPlan** - Creating final execution plan

#### 4.3 Results Analysis
- **Demo:** Show the generated execution plan
- **Key Point:** "Notice the structured output with priorities, time estimates, and categories"
- **Show:** The original task analysis, summary, and detailed steps

#### 4.4 Code Behind the Magic
- **Demo:** Scroll down to show the actual agent code
- **Key Point:** "This is the actual Java code that just executed"
- **Show:** How the annotations map to the execution steps

**Demo Script:**
> "What you just saw wasn't a pre-programmed response. The agent analyzed the task, determined its complexity, broke it down into actionable steps, prioritized them based on dependencies, and created a comprehensive execution plan. All of this happened through the power of LLM integration with strongly-typed Java code."

### Step 5: Advanced Features Demonstration (5-7 minutes)

#### 5.1 Try Different Task Types
- **Demo:** Try different sample tasks to show versatility:
  - "Plan a company retreat for 50 people" (Event Planning)
  - "Create a marketing strategy for a new product launch" (Business Strategy)
  - "Develop a training program for new employees" (HR/Education)

#### 5.2 Show Agent Adaptability
- **Key Point:** "The same agent handles different domains intelligently"
- **Demo:** Compare results across different task types
- **Show:** How the agent adapts its analysis and recommendations

#### 5.3 Highlight Enterprise Features
- **Key Point:** "This runs in production with enterprise-grade features"
- **Demo:** Mention Spring Boot Actuator endpoints
- **Show:** Health checks, metrics, and monitoring capabilities

**Demo Script:**
> "The same agent code handles completely different domains. It's not hardcoded for any specific use case. The AI provides the intelligence, while Java provides the structure, type safety, and enterprise integration."

### Step 6: Technical Deep Dive (Optional - 5-10 minutes)

#### 6.1 Show the Actual Code
- **Demo:** Open the source code in an IDE
- **Show:** The `TaskBreakdownAgent.java` file
- **Key Point:** "This is production-ready Java code"

#### 6.2 Explain the Architecture
- **Show:** How `@Agent`, `@Action`, and `@AchievesGoal` work together
- **Demo:** The domain model records (`Task`, `Step`, `ExecutionPlan`)
- **Key Point:** "Everything is strongly typed and testable"

#### 6.3 Demonstrate Testing
- **Demo:** Show the unit tests
- **Key Point:** "You can test agents like any other Java code"
- **Show:** The `FakeOperationContext` and mocked LLM responses

**Demo Script:**
> "This isn't just a demo - this is production code. You can see the full test suite, the Spring Boot configuration, and the enterprise features. This is how you build AI agents in the real world."

---

## 🎯 Key Demo Messages

### 1. **Java-First Approach**
- "Embabel brings AI agent development into the Java ecosystem"
- "You get all the benefits of Java: type safety, IDE support, testing, refactoring"
- "No more configuration files or string-based approaches"

### 2. **Enterprise Ready**
- "Built by the Spring Framework team with enterprise needs in mind"
- "Seamless integration with existing Spring applications"
- "Production-ready with monitoring, health checks, and observability"

### 3. **Developer Experience**
- "Write agents like you write any other Java code"
- "Full IDE support with autocomplete, refactoring, and debugging"
- "Comprehensive testing support with familiar tools"

### 4. **AI Integration**
- "Sophisticated AI planning beyond finite state machines"
- "Multiple LLM support with cost optimization"
- "Persona-based AI behavior for different use cases"

### 5. **Real-World Applicability**
- "Not just a toy - this is production-ready technology"
- "Handles complex, multi-step tasks intelligently"
- "Adapts to different domains without code changes"

---

## 🛠️ Troubleshooting Tips

### Common Issues:
1. **Application won't start:** Check if port 8080 is available
2. **Demo doesn't work:** Ensure the backend is running and accessible
3. **LLM errors:** The demo uses fake LLM responses in test mode

### Fallback Options:
- If the live demo fails, show the static code examples
- Use the pre-built sample responses to demonstrate the concept
- Focus on the code structure and annotations

---

## 📝 Demo Conclusion

**Closing Message:**
> "Embabel represents the future of AI agent development - where the power of AI meets the reliability and structure of enterprise Java. You get the best of both worlds: intelligent, adaptive behavior from AI, combined with the type safety, testability, and enterprise integration that Java developers expect.

> This isn't just another AI framework - it's a new way to think about building intelligent applications. With Embabel, you can build AI agents that are as reliable and maintainable as any other enterprise Java application."

**Next Steps:**
- Point to GitHub repository for examples
- Mention documentation and getting started guides
- Encourage questions about specific use cases

---

## 🎬 Demo Timing Guide

| Section | Duration | Key Focus |
|---------|----------|-----------|
| Introduction | 2-3 min | Framework overview and positioning |
| Building Blocks | 5-7 min | Technical concepts and code examples |
| Benefits | 3-4 min | Value proposition and differentiators |
| Interactive Demo | 8-10 min | Live agent execution and results |
| Advanced Features | 5-7 min | Versatility and enterprise features |
| Technical Deep Dive | 5-10 min | Code walkthrough and testing (optional) |
| **Total** | **28-41 min** | **Comprehensive framework showcase** |

This walkthrough provides a complete demonstration of Embabel's capabilities while maintaining focus on the key value propositions: Java-first development, enterprise readiness, and sophisticated AI integration.
