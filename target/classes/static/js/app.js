// Main application JavaScript for Embabel Demo - Step-based Architecture

class EmbabelDemo {
    constructor() {
        this.stepManager = new StepManager();
        this.scenarioManager = new ScenarioManager();
        this.currentScenario = null;
        this.executionData = null;
        this.actionTimeline = [];
        
        // DOM elements
        this.taskInput = document.getElementById('taskInput');
        this.executeCustomButton = document.getElementById('executeCustomButton');
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadScenarios();
        
        // Make instance globally available
        window.embabelDemo = this;
    }

    setupEventListeners() {
        // Custom task execution
        if (this.executeCustomButton) {
            this.executeCustomButton.addEventListener('click', () => this.executeCustomTask());
        }
        
        if (this.taskInput) {
            this.taskInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && e.ctrlKey) {
                    this.executeCustomTask();
                }
            });
        }
    }

    async loadScenarios() {
        try {
            // Load scenarios from the scenario manager
            const scenarioCards = document.getElementById('scenario-cards');
            if (scenarioCards) {
                this.scenarioManager.renderScenarioGrid('scenario-cards');
            }
        } catch (error) {
            console.error('Failed to load scenarios:', error);
        }
    }

    async selectScenario(scenario) {
        this.currentScenario = scenario;
        
        // Update task context
        this.updateTaskContext(scenario);
        
        // Complete task selection step
        this.stepManager.completeTaskSelection(scenario);
        
        // Move to execution step
        this.stepManager.goToStep(2);
        
        // Execute the task
        await this.executeTask(scenario.title);
    }

    async executeCustomTask() {
        const taskDescription = this.taskInput?.value.trim();
        
        if (!taskDescription) {
            this.showError('Please enter a task description');
            return;
        }

        // Create custom scenario
        const customScenario = this.scenarioManager.createCustomTask(
            taskDescription,
            'Custom task created by user'
        );
        
        await this.selectScenario(customScenario);
    }

    async executeTask(taskDescription) {
        this.setLoading(true);
        this.clearActionTimeline();
        this.showExecutionMetrics();

        try {
            const response = await fetch('/api/task-breakdown', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ taskDescription })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const executionTrace = await response.json();
            console.log('Execution trace received:', executionTrace);
            
            this.executionData = executionTrace;
            
            // Display each action step with timing
            if (executionTrace.actionSteps) {
                await this.displayActionSteps(executionTrace.actionSteps);
            }
            
            // Complete execution step
            this.stepManager.completeExecution(executionTrace);
            
            // Move to results step
            this.stepManager.goToStep(3);
            
        } catch (error) {
            console.error('Error executing task:', error);
            this.showError('Failed to execute task. Please try again.');
        } finally {
            this.setLoading(false);
        }
    }

    async displayActionSteps(actionSteps) {
        const timeline = document.getElementById('action-timeline');
        if (!timeline) return;

        timeline.innerHTML = '';

        for (let i = 0; i < actionSteps.length; i++) {
            const action = actionSteps[i];
            
            // Create step element
            const stepElement = this.createActionStepElement(action, i + 1);
            timeline.appendChild(stepElement);
            
            // Show step with delay
            await this.delay(1500);
            this.showActionStep(stepElement, action);
            
            // Update execution metrics
            this.updateExecutionMetrics(action, i + 1, actionSteps.length);
        }
    }

    createActionStepElement(action, stepNumber) {
        const stepDiv = document.createElement('div');
        stepDiv.className = 'action-step bg-gray-50 border border-gray-200 rounded-lg p-6 mb-4 opacity-50 transition-all duration-500';
        stepDiv.innerHTML = `
            <div class="flex items-start">
                <div class="step-number flex items-center justify-center w-8 h-8 rounded-full bg-gray-300 text-gray-600 font-semibold mr-4">
                    ${stepNumber}
                </div>
                <div class="flex-1">
                    <div class="flex items-center justify-between mb-3">
                        <h4 class="text-lg font-semibold text-gray-900">${this.formatActionName(action.actionName)}</h4>
                        <span class="text-xs font-mono bg-blue-100 text-blue-800 px-2 py-1 rounded">@Action</span>
                    </div>
                    
                    <div class="space-y-3">
                        <div class="thinking-section">
                            <div class="flex items-center mb-2">
                                <span class="text-lg mr-2">💭</span>
                                <span class="font-medium text-gray-700">Thinking:</span>
                            </div>
                            <div class="thinking-content bg-white p-3 rounded border-l-4 border-blue-500 text-sm text-gray-600">
                                ${action.reasoning?.thought || 'Processing...'}
                            </div>
                        </div>
                        
                        <div class="reasoning-section">
                            <div class="flex items-center mb-2">
                                <span class="text-lg mr-2">🧠</span>
                                <span class="font-medium text-gray-700">Reasoning:</span>
                            </div>
                            <div class="reasoning-content bg-white p-3 rounded border-l-4 border-purple-500 text-sm text-gray-600">
                                ${action.reasoning?.why || 'Using AI to process this step...'}
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="input-section">
                                <div class="flex items-center mb-2">
                                    <span class="text-lg mr-2">📥</span>
                                    <span class="font-medium text-gray-700">Input:</span>
                                </div>
                                <div class="input-content bg-white p-3 rounded border text-sm text-gray-600">
                                    ${this.formatInputOutput(action.input)}
                                </div>
                            </div>
                            
                            <div class="output-section">
                                <div class="flex items-center mb-2">
                                    <span class="text-lg mr-2">📤</span>
                                    <span class="font-medium text-gray-700">Output:</span>
                                </div>
                                <div class="output-content bg-white p-3 rounded border text-sm text-gray-600">
                                    ${this.formatInputOutput(action.output)}
                                </div>
                            </div>
                        </div>
                        
                        <div class="flex items-center justify-between pt-3 border-t">
                            <div class="flex items-center space-x-4 text-xs text-gray-500">
                                <span><strong>Persona:</strong> ${action.reasoning?.persona || 'AI Agent'}</span>
                                <span><strong>Model:</strong> ${action.reasoning?.llmModel || 'gpt-4o-mini'}</span>
                                <span><strong>Temp:</strong> ${action.reasoning?.temperature || '0.7'}</span>
                            </div>
                            <div class="duration text-xs text-gray-500">
                                <strong>Duration:</strong> ${action.duration || '1.5s'}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        return stepDiv;
    }

    showActionStep(stepElement, action) {
        stepElement.classList.remove('opacity-50');
        stepElement.classList.add('opacity-100', 'bg-blue-50', 'border-blue-200');
        
        // Animate the step number
        const stepNumber = stepElement.querySelector('.step-number');
        stepNumber.classList.remove('bg-gray-300', 'text-gray-600');
        stepNumber.classList.add('bg-blue-500', 'text-white');
        stepNumber.textContent = '✓';
        
        // Scroll to the step
        stepElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    clearActionTimeline() {
        const timeline = document.getElementById('action-timeline');
        if (timeline) {
            timeline.innerHTML = '<div class="text-center text-gray-500 py-8">Preparing agent execution...</div>';
        }
    }

    updateTaskContext(scenario) {
        const taskContext = document.getElementById('task-context');
        if (!taskContext) return;

        taskContext.innerHTML = `
            <div class="space-y-4">
                <div>
                    <h4 class="font-semibold text-gray-900 mb-2">Task Description</h4>
                    <p class="text-sm text-gray-600">${scenario.title}</p>
                </div>
                
                <div>
                    <h4 class="font-semibold text-gray-900 mb-2">Category</h4>
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        ${scenario.category}
                    </span>
                </div>
                
                <div>
                    <h4 class="font-semibold text-gray-900 mb-2">Complexity</h4>
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${scenario.getComplexityColor()}">
                        ${scenario.complexity}
                    </span>
                </div>
                
                <div>
                    <h4 class="font-semibold text-gray-900 mb-2">Expected Steps</h4>
                    <p class="text-sm text-gray-600">${scenario.estimatedSteps} actions</p>
                </div>
                
                <div>
                    <h4 class="font-semibold text-gray-900 mb-2">Duration</h4>
                    <p class="text-sm text-gray-600">${scenario.expectedDuration}</p>
                </div>
            </div>
        `;
    }

    showExecutionMetrics() {
        const metrics = document.getElementById('execution-metrics');
        if (!metrics) return;

        metrics.innerHTML = `
            <div class="metric-card bg-blue-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">0</div>
                <div class="text-sm text-gray-600">Actions Completed</div>
            </div>
            <div class="metric-card bg-green-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-green-600">0s</div>
                <div class="text-sm text-gray-600">Total Time</div>
            </div>
            <div class="metric-card bg-purple-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-purple-600">1</div>
                <div class="text-sm text-gray-600">Personas Used</div>
            </div>
            <div class="metric-card bg-orange-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-orange-600">1</div>
                <div class="text-sm text-gray-600">LLM Models</div>
            </div>
        `;
    }

    updateExecutionMetrics(action, currentStep, totalSteps) {
        const metrics = document.getElementById('execution-metrics');
        if (!metrics) return;

        const completedCard = metrics.querySelector('.metric-card:first-child .text-2xl');
        if (completedCard) {
            completedCard.textContent = currentStep;
        }
    }

    formatActionName(actionName) {
        return actionName
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    }

    formatInputOutput(data) {
        if (typeof data === 'string') {
            return data.length > 100 ? data.substring(0, 100) + '...' : data;
        }
        if (typeof data === 'object') {
            return JSON.stringify(data, null, 2).substring(0, 100) + '...';
        }
        return String(data);
    }

    setLoading(loading) {
        if (this.executeCustomButton) {
            this.executeCustomButton.disabled = loading;
            if (loading) {
                this.executeCustomButton.innerHTML = '<div class="spinner"></div>Processing...';
            } else {
                this.executeCustomButton.textContent = 'Execute Custom Task';
            }
        }
    }

    showError(message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4';
        errorDiv.textContent = message;
        
        // Remove existing error messages
        const existingErrors = document.querySelectorAll('.error');
        existingErrors.forEach(error => error.remove());
        
        // Insert error message
        const container = document.getElementById('custom-task-input');
        if (container) {
            container.insertBefore(errorDiv, container.firstChild);
        }
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }

    delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // Educational content loading
    loadEducationalContent() {
        this.loadCodeExamples();
        this.loadArchitectureDiagram();
    }

    loadCodeExamples() {
        const codeExamples = document.getElementById('code-examples');
        if (!codeExamples) return;

        codeExamples.innerHTML = `
            <div class="space-y-4">
                <div class="code-example">
                    <h4 class="font-semibold mb-2">@Action Method</h4>
                    <div class="bg-gray-900 rounded p-3 text-sm overflow-x-auto">
                        <pre><code class="language-java">@Action
Task analyzeTask(UserInput userInput, OperationContext context) {
    return context.ai()
        .withLlm(LlmOptions.withAutoLlm().withTemperature(0.3))
        .withPromptContributor(ANALYST)
        .createObject("Analyze this task...", Task.class);
}</code></pre>
                    </div>
                </div>
                
                <div class="code-example">
                    <h4 class="font-semibold mb-2">Domain Model</h4>
                    <div class="bg-gray-900 rounded p-3 text-sm overflow-x-auto">
                        <pre><code class="language-java">public record Task(
    String description,
    String category,
    String complexity
) {}</code></pre>
                    </div>
                </div>
            </div>
        `;
        
        // Re-highlight code
        if (window.hljs) {
            hljs.highlightAll();
        }
    }

    loadArchitectureDiagram() {
        const diagram = document.getElementById('architecture-diagram');
        if (!diagram) return;

        diagram.innerHTML = `
            <div class="space-y-4">
                <div class="architecture-flow">
                    <div class="flex items-center justify-between">
                        <div class="text-center">
                            <div class="w-16 h-16 bg-blue-100 rounded-lg flex items-center justify-center mb-2">
                                <span class="text-2xl">🎯</span>
                            </div>
                            <div class="text-sm font-medium">Task Input</div>
                        </div>
                        <div class="text-gray-400">→</div>
                        <div class="text-center">
                            <div class="w-16 h-16 bg-green-100 rounded-lg flex items-center justify-center mb-2">
                                <span class="text-2xl">🤖</span>
                            </div>
                            <div class="text-sm font-medium">Agent</div>
                        </div>
                        <div class="text-gray-400">→</div>
                        <div class="text-center">
                            <div class="w-16 h-16 bg-purple-100 rounded-lg flex items-center justify-center mb-2">
                                <span class="text-2xl">📋</span>
                            </div>
                            <div class="text-sm font-medium">Execution Plan</div>
                        </div>
                    </div>
                </div>
                
                <div class="mt-6">
                    <h4 class="font-semibold mb-3">Key Components</h4>
                    <ul class="space-y-2 text-sm">
                        <li class="flex items-center">
                            <span class="w-2 h-2 bg-blue-500 rounded-full mr-2"></span>
                            <strong>@Agent:</strong> Defines the agent class
                        </li>
                        <li class="flex items-center">
                            <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                            <strong>@Action:</strong> Individual processing steps
                        </li>
                        <li class="flex items-center">
                            <span class="w-2 h-2 bg-purple-500 rounded-full mr-2"></span>
                            <strong>@AchievesGoal:</strong> Final objectives
                        </li>
                        <li class="flex items-center">
                            <span class="w-2 h-2 bg-orange-500 rounded-full mr-2"></span>
                            <strong>Domain Models:</strong> Type-safe data structures
                        </li>
                    </ul>
                </div>
            </div>
        `;
    }
}

// Initialize the demo when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new EmbabelDemo();
});