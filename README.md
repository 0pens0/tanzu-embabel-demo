# Tanzu Embabel Demo

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Cloud Foundry](https://img.shields.io/badge/Cloud%20Foundry-0C97ED?style=for-the-badge&logo=cloudfoundry&logoColor=white)
![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)

Educational demo application showcasing the [Embabel AI agent framework](https://github.com/embabel) with interactive web frontend and Cloud Foundry deployment.

## 🤖 What is Embabel?

Embabel is a sophisticated AI agent framework for the JVM that enables developers to build intelligent applications combining Large Language Models with traditional programming paradigms. Created by the team behind Spring Framework, it provides:

- **Sophisticated Planning**: Goes beyond finite state machines with true AI-powered planning
- **Strong Typing**: Full object-oriented programming with Java/Kotlin type safety
- **Built-in Testability**: Comprehensive unit and integration testing support
- **Spring Integration**: Seamless integration with the Spring ecosystem
- **Platform Abstraction**: Clean separation between programming model and deployment platform

## 🚀 Features

This demo application showcases:

### TaskBreakdownAgent
A practical AI agent that demonstrates Embabel's core concepts:
- **@Agent**: Defines the class as an Embabel agent
- **@Action**: Methods representing steps the agent can take
- **@AchievesGoal**: Methods representing goal achievement
- **Domain Model**: Strongly-typed records (Task, Step, ExecutionPlan)
- **Personas**: Different LLM personalities for different tasks
- **LLM Mixing**: Using different temperatures for creative vs analytical tasks

### Interactive Web Frontend
- **Educational Content**: Comprehensive explanation of Embabel framework
- **Building Blocks**: Visual guide to @Agent, @Action, @AchievesGoal annotations
- **Benefits**: Detailed explanation of framework advantages
- **Live Demo**: Interactive task breakdown with real-time execution visualization
- **Code Examples**: Syntax-highlighted code snippets alongside explanations

### REST API
- **Agent Execution**: Execute the TaskBreakdownAgent programmatically
- **Execution Tracing**: Detailed trace of all actions performed
- **Framework Information**: API endpoints for educational content
- **Health Checks**: Cloud Foundry compatible health endpoints

### Cloud Foundry Deployment
- **manifest.yml**: Complete Cloud Foundry deployment configuration
- **Cloud Profiles**: Environment-specific configuration
- **Health Checks**: HTTP health check endpoint
- **Resource Optimization**: Memory and disk configuration for cloud deployment

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Frontend  │    │   REST API       │    │  TaskBreakdown  │
│   (HTML/CSS/JS) │◄──►│   Controller     │◄──►│     Agent       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │  AgentExecution  │    │   Embabel       │
                       │     Service      │    │   Framework     │
                       └──────────────────┘    └─────────────────┘
```

## 🛠️ Prerequisites

- **Java 21+**: Required for running the application
- **Maven 3.6+**: For building and dependency management
- **OpenAI API Key**: Required for LLM functionality
- **Cloud Foundry CLI** (optional): For cloud deployment

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/0pens0/tanzu-embabel-demo.git
cd tanzu-embabel-demo
```

### 2. Set Environment Variables

```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### 3. Build and Run

```bash
# Build the application
mvn clean package

# Run locally
java -jar target/tanzu-embabel-demo-0.1.0-SNAPSHOT.jar
```

### 4. Access the Application

- **Web Interface**: http://localhost:8080
- **API Health Check**: http://localhost:8080/api/health
- **Framework Info**: http://localhost:8080/api/framework-info

## 🌐 Cloud Foundry Deployment

### Prerequisites

1. **Cloud Foundry CLI**: Install from [Cloud Foundry CLI](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)
2. **Cloud Foundry Account**: Access to a Cloud Foundry environment
3. **API Key**: Set your OpenAI API key as an environment variable

### Deployment Steps

1. **Login to Cloud Foundry**:
   ```bash
   cf login -a https://api.your-cf-domain.com
   ```

2. **Set Environment Variables**:
   ```bash
   cf set-env tanzu-embabel-demo OPENAI_API_KEY "your-api-key"
   ```

3. **Deploy the Application**:
   ```bash
   # Build the application
   mvn clean package
   
   # Deploy using the manifest
   cf push
   ```

4. **Verify Deployment**:
   ```bash
   # Check application status
   cf apps
   
   # View application logs
   cf logs tanzu-embabel-demo --recent
   ```

### Customizing the Deployment

Edit `manifest.yml` to customize:
- **Application name**: Change the `name` field
- **Memory allocation**: Modify the `memory` field
- **Instance count**: Update the `instances` field
- **Routes**: Update the `routes` section with your domain
- **Services**: Add any required services (databases, etc.)

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Test Coverage

The unit tests demonstrate Embabel's testing capabilities:
- **FakeOperationContext**: Mock LLM interactions for isolated testing
- **FakePromptRunner**: Verify prompt construction and content
- **Domain Model Testing**: Test strongly-typed record creation
- **Action Method Testing**: Test individual @Action methods

### Example Test

```java
@Test
void testAnalyzeTask() {
    var context = FakeOperationContext.create();
    var userInput = new UserInput("Build a mobile app", Instant.now());
    context.expectResponse(new Task("Build a mobile app", "Software Development", "Complex"));
    
    var result = agent.analyzeTask(userInput, context);
    
    assertEquals("Software Development", result.category());
}
```

## 📚 API Documentation

### POST /api/breakdown-task

Execute the TaskBreakdownAgent with a task description.

**Request Body**:
```json
{
  "taskDescription": "Build a mobile app for recipe sharing"
}
```

**Response**:
```json
{
  "taskDescription": "Build a mobile app for recipe sharing",
  "actionSteps": [
    {
      "actionName": "analyzeTask",
      "description": "Analyze the user's task to understand its nature and complexity",
      "timestamp": "2024-01-01T12:00:00.000Z",
      "durationMs": 1500
    }
  ],
  "finalResult": {
    "originalTask": {
      "description": "Build a mobile app for recipe sharing",
      "category": "Software Development",
      "complexity": "Complex"
    },
    "steps": [
      {
        "title": "Define requirements and features",
        "description": "Gather requirements and define core features",
        "priority": 1,
        "estimatedTime": "1 week",
        "category": "Planning"
      }
    ],
    "summary": "A comprehensive plan to build a mobile recipe sharing app",
    "totalEstimatedTime": "8-12 weeks"
  },
  "executionTimeMs": 5000
}
```

### GET /api/framework-info

Get information about the Embabel framework.

**Response**:
```json
{
  "name": "Embabel Agent Framework",
  "version": "0.1.3",
  "description": "Framework for authoring agentic flows on the JVM...",
  "features": [
    {
      "name": "@Agent",
      "shortDescription": "Defines a class as an Embabel agent",
      "detailedDescription": "Marks the class as an agent that can perform actions and achieve goals"
    }
  ],
  "benefits": [
    "Sophisticated planning beyond finite state machines",
    "Strong typing and object-oriented programming benefits"
  ]
}
```

### GET /api/health

Health check endpoint for monitoring and Cloud Foundry.

**Response**:
```json
{
  "status": "UP",
  "service": "Tanzu Embabel Demo",
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Task breakdown agent configuration
task.maxSteps=8
task.maxStepDescriptionLength=150

# Embabel configuration
embabel.models.default-llm=gpt-4o-mini
embabel.agent.platform.ranking.llm=gpt-4o-mini

# Logging
logging.level.com.embabel=DEBUG
```

### Cloud Configuration

Cloud-specific settings in `application-cloud.yml`:

```yaml
server:
  port: ${PORT:8080}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 🎯 Usage Examples

### Basic Task Breakdown

1. Open the web interface at http://localhost:8080
2. Enter a task description like "Plan a company retreat"
3. Click "Break Down Task"
4. Watch the agent execute each step:
   - **Analyze Task**: Determines complexity and category
   - **Break Down**: Creates actionable steps
   - **Prioritize**: Optimizes step order
   - **Create Plan**: Generates final execution plan

### Sample Tasks to Try

- "Build a mobile app for recipe sharing"
- "Plan a company retreat for 50 people"
- "Create a marketing strategy for a new product launch"
- "Develop a training program for new employees"
- "Design a home renovation project"

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Embabel Framework](https://github.com/embabel) - The AI agent framework this demo showcases
- [Spring Framework](https://spring.io/) - The foundation for enterprise Java applications
- [Cloud Foundry](https://www.cloudfoundry.org/) - The cloud platform for deployment

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/0pens0/tanzu-embabel-demo/issues)
- **Documentation**: [Embabel Documentation](https://github.com/embabel/embabel-agent)
- **Examples**: [Embabel Examples](https://github.com/embabel/embabel-agent-examples)

---

**Built with ❤️ using Embabel Framework**