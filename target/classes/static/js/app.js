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

        // Navigation buttons
        const backToExecutionBtn = document.getElementById('back-to-execution');
        if (backToExecutionBtn) {
            backToExecutionBtn.addEventListener('click', () => {
                this.stepManager.goToStep(2);
            });
        }
        
        const learnMoreBtn = document.getElementById('learn-more');
        if (learnMoreBtn) {
            learnMoreBtn.addEventListener('click', () => {
                this.stepManager.goToStep(4);
            });
        }
        
        const viewDetailedPlanBtn = document.getElementById('view-detailed-plan');
        if (viewDetailedPlanBtn) {
            viewDetailedPlanBtn.addEventListener('click', () => {
                this.showDetailedPlan();
            });
        }
        
        const backToResultsBtn = document.getElementById('back-to-results');
        if (backToResultsBtn) {
            backToResultsBtn.addEventListener('click', () => {
                this.stepManager.goToStep(3);
            });
        }
        
        // Implementation details buttons (delegated event listener)
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('implementation-details-btn')) {
                const actionName = e.target.dataset.action;
                this.showImplementationDetails(actionName);
            }
        });
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
            
            // Display results immediately
            this.displayExecutionPlan(executionTrace.finalResult);
            this.displayComparisonView(this.currentScenario, executionTrace.finalResult);
            
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
                            <div class="flex items-center space-x-2">
                                <div class="duration text-xs text-gray-500">
                                    <strong>Duration:</strong> ${action.duration || '1.5s'}
                                </div>
                                <button class="implementation-details-btn text-xs bg-blue-100 hover:bg-blue-200 text-blue-800 px-2 py-1 rounded transition-colors" 
                                        data-action="${action.actionName}">
                                    View Details
                                </button>
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
            return `<div class="formatted-output string-output">${data}</div>`;
        }
        
        if (Array.isArray(data)) {
            // Check if it's an array of steps (has description and priority fields)
            if (data.length > 0 && data[0].description && data[0].priority) {
                return this.formatStepsArray(data);
            }
            // Regular array
            return this.formatArray(data);
        }
        
        if (typeof data === 'object' && data !== null) {
            return this.formatObject(data);
        }
        
        return `<div class="formatted-output string-output">${String(data)}</div>`;
    }

    formatStepsArray(steps) {
        const stepsHtml = steps.map(step => `
            <div class="step-item">
                <div class="step-header">
                    <span class="step-priority">${step.priority}.</span>
                    <span class="step-description">${step.description}</span>
                </div>
            </div>
        `).join('');
        
        return `<div class="formatted-output steps-output">
            <div class="steps-list">${stepsHtml}</div>
        </div>`;
    }

    formatArray(array) {
        const itemsHtml = array.map((item, index) => `
            <div class="array-item">
                <span class="array-index">${index + 1}.</span>
                <span class="array-value">${this.formatArrayItem(item)}</span>
            </div>
        `).join('');
        
        return `<div class="formatted-output array-output">
            <div class="array-list">${itemsHtml}</div>
        </div>`;
    }

    formatArrayItem(item) {
        if (typeof item === 'string') {
            return item;
        }
        if (typeof item === 'object') {
            return JSON.stringify(item, null, 2);
        }
        return String(item);
    }

    formatObject(obj) {
        const entries = Object.entries(obj);
        const entriesHtml = entries.map(([key, value]) => `
            <div class="object-entry">
                <span class="object-key">${key}:</span>
                <span class="object-value">${this.formatObjectValue(value)}</span>
            </div>
        `).join('');
        
        return `<div class="formatted-output object-output">
            <div class="object-entries">${entriesHtml}</div>
        </div>`;
    }

    formatObjectValue(value) {
        if (typeof value === 'string') {
            return value;
        }
        if (Array.isArray(value)) {
            return `[${value.length} items]`;
        }
        if (typeof value === 'object') {
            return `{${Object.keys(value).length} properties}`;
        }
        return String(value);
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

    async showImplementationDetails(actionName) {
        try {
            const response = await fetch(`/api/action-details/${actionName}`);
            const details = await response.json();
            this.createImplementationModal(details);
        } catch (error) {
            console.error('Failed to load implementation details:', error);
            this.showError('Failed to load implementation details');
        }
    }

    createImplementationModal(details) {
        // Remove existing modal
        const existingModal = document.getElementById('implementation-modal');
        if (existingModal) {
            existingModal.remove();
        }

        const modal = document.createElement('div');
        modal.id = 'implementation-modal';
        modal.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
        modal.innerHTML = `
            <div class="bg-white rounded-lg shadow-xl max-w-4xl w-full mx-4 max-h-[90vh] overflow-y-auto">
                <div class="p-6">
                    <div class="flex items-center justify-between mb-6">
                        <h2 class="text-2xl font-bold text-gray-900">Implementation Details: ${this.formatActionName(details.name)}</h2>
                        <button class="close-modal text-gray-400 hover:text-gray-600 text-2xl font-bold">&times;</button>
                    </div>
                    
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        <!-- Left Column: Action Information -->
                        <div class="space-y-4">
                            <div class="bg-blue-50 p-4 rounded-lg">
                                <h3 class="font-semibold text-blue-900 mb-2">Action Information</h3>
                                <div class="space-y-2 text-sm">
                                    <div><strong>Name:</strong> ${details.name}</div>
                                    <div><strong>Description:</strong> ${details.description}</div>
                                    <div><strong>Purpose:</strong> ${details.purpose}</div>
                                </div>
                            </div>
                            
                            <div class="bg-green-50 p-4 rounded-lg">
                                <h3 class="font-semibold text-green-900 mb-2">AI Configuration</h3>
                                <div class="space-y-2 text-sm">
                                    <div><strong>Persona:</strong> ${details.persona}</div>
                                    <div><strong>LLM Model:</strong> ${details.llmModel}</div>
                                    <div><strong>Temperature:</strong> ${details.temperature}</div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Right Column: Code Example -->
                        <div>
                            <div class="bg-gray-50 p-4 rounded-lg">
                                <h3 class="font-semibold text-gray-900 mb-3">Code Implementation</h3>
                                <div class="bg-gray-900 rounded p-4 overflow-x-auto">
                                    <pre><code class="language-java">${details.codeExample}</code></pre>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mt-6 flex justify-end">
                        <button class="close-modal bg-gray-500 hover:bg-gray-600 text-white px-6 py-2 rounded-lg font-medium transition-colors">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // Add event listeners for closing modal
        const closeButtons = modal.querySelectorAll('.close-modal');
        closeButtons.forEach(button => {
            button.addEventListener('click', () => {
                modal.remove();
            });
        });

        // Close modal when clicking outside
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.remove();
            }
        });

        // Re-highlight code
        if (window.hljs) {
            hljs.highlightAll();
        }
    }

    showDetailedPlan() {
        // Hide all steps and show detailed plan
        this.stepManager.hideAllSteps();
        const detailedPlanStep = document.getElementById('step-detailed-plan');
        if (detailedPlanStep) {
            detailedPlanStep.classList.remove('hidden');
            detailedPlanStep.classList.add('active');
        }
        
        // Load detailed plan content
        this.loadDetailedPlanContent();
    }

    loadDetailedPlanContent() {
        if (!this.executionData || !this.executionData.finalResult) return;

        const executionPlan = this.executionData.finalResult;
        
        // Load plan overview
        this.loadPlanOverview(executionPlan);
        
        // Load detailed timeline
        this.loadDetailedTimeline(executionPlan);
        
        // Load step dependencies
        this.loadStepDependencies(executionPlan);
        
        // Load resource requirements
        this.loadResourceRequirements(executionPlan);
    }

    loadPlanOverview(executionPlan) {
        const planOverview = document.getElementById('plan-overview');
        if (!planOverview) return;

        planOverview.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div class="bg-blue-50 p-4 rounded-lg">
                    <h4 class="font-semibold text-blue-900 mb-2">📋 Plan Summary</h4>
                    <p class="text-blue-800 text-sm">${executionPlan.summary}</p>
                </div>
                <div class="bg-green-50 p-4 rounded-lg">
                    <h4 class="font-semibold text-green-900 mb-2">⏱️ Estimated Duration</h4>
                    <p class="text-green-800 text-lg font-semibold">${executionPlan.estimatedTime}</p>
                </div>
                <div class="bg-purple-50 p-4 rounded-lg">
                    <h4 class="font-semibold text-purple-900 mb-2">📝 Total Steps</h4>
                    <p class="text-purple-800 text-lg font-semibold">${executionPlan.steps.length} Steps</p>
                </div>
            </div>
        `;
    }

    loadDetailedTimeline(executionPlan) {
        const timelineContainer = document.getElementById('timeline-container');
        if (!timelineContainer) return;

        const timelineHtml = executionPlan.steps.map((step, index) => {
            const estimatedDays = this.estimateStepDuration(step, executionPlan.steps.length);
            const startDay = index === 0 ? 1 : index * estimatedDays + 1;
            const endDay = startDay + estimatedDays - 1;
            
            return `
                <div class="timeline-item flex items-start mb-6">
                    <div class="timeline-marker flex-shrink-0 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold text-sm mr-4">
                        ${step.priority}
                    </div>
                    <div class="timeline-content flex-1 bg-gray-50 p-4 rounded-lg">
                        <div class="flex items-center justify-between mb-2">
                            <h4 class="font-semibold text-gray-900">${step.description}</h4>
                            <span class="text-sm text-gray-500">Day ${startDay}-${endDay}</span>
                        </div>
                        <div class="flex items-center space-x-4 text-sm text-gray-600">
                            <span><strong>Priority:</strong> ${step.priority}</span>
                            <span><strong>Duration:</strong> ${estimatedDays} day${estimatedDays > 1 ? 's' : ''}</span>
                            <span><strong>Status:</strong> <span class="text-yellow-600">Pending</span></span>
                        </div>
                        <div class="mt-2">
                            <div class="w-full bg-gray-200 rounded-full h-2">
                                <div class="bg-blue-500 h-2 rounded-full" style="width: 0%"></div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        timelineContainer.innerHTML = `
            <div class="timeline">
                ${timelineHtml}
            </div>
        `;
    }

    loadStepDependencies(executionPlan) {
        const dependenciesContainer = document.getElementById('dependencies-container');
        if (!dependenciesContainer) return;

        const dependenciesHtml = executionPlan.steps.map((step, index) => {
            const dependencies = this.getStepDependencies(step, index, executionPlan.steps);
            
            return `
                <div class="dependency-item bg-white border border-gray-200 rounded-lg p-4 mb-4">
                    <div class="flex items-center justify-between mb-3">
                        <h4 class="font-semibold text-gray-900">Step ${step.priority}: ${step.description}</h4>
                        <span class="text-sm text-gray-500">Priority ${step.priority}</span>
                    </div>
                    <div class="space-y-2">
                        <div><strong>Dependencies:</strong></div>
                        ${dependencies.length > 0 ? 
                            dependencies.map(dep => `<div class="text-sm text-gray-600 ml-4">• ${dep}</div>`).join('') :
                            '<div class="text-sm text-gray-500 ml-4">No dependencies - can start immediately</div>'
                        }
                    </div>
                </div>
            `;
        }).join('');

        dependenciesContainer.innerHTML = dependenciesHtml;
    }

    loadResourceRequirements(executionPlan) {
        const resourcesContainer = document.getElementById('resources-container');
        if (!resourcesContainer) return;

        const resources = this.estimateResourceRequirements(executionPlan);

        resourcesContainer.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="bg-yellow-50 p-4 rounded-lg">
                    <h4 class="font-semibold text-yellow-900 mb-3">👥 Human Resources</h4>
                    <div class="space-y-2 text-sm">
                        <div><strong>Project Manager:</strong> 1 person</div>
                        <div><strong>Team Members:</strong> ${resources.teamSize} people</div>
                        <div><strong>Total Effort:</strong> ${resources.totalEffort} person-days</div>
                    </div>
                </div>
                <div class="bg-indigo-50 p-4 rounded-lg">
                    <h4 class="font-semibold text-indigo-900 mb-3">🛠️ Tools & Technology</h4>
                    <div class="space-y-2 text-sm">
                        <div><strong>Primary Tools:</strong> ${resources.primaryTools.join(', ')}</div>
                        <div><strong>Budget Estimate:</strong> ${resources.budgetEstimate}</div>
                        <div><strong>Risk Level:</strong> <span class="text-${resources.riskLevel === 'Low' ? 'green' : resources.riskLevel === 'Medium' ? 'yellow' : 'red'}-600">${resources.riskLevel}</span></div>
                    </div>
                </div>
            </div>
        `;
    }

    estimateStepDuration(step, totalSteps) {
        // Simple estimation based on step priority and total steps
        const baseDuration = Math.max(1, Math.ceil(14 / totalSteps)); // Assume 2 weeks total
        return Math.max(1, baseDuration - (step.priority - 1) * 0.5);
    }

    getStepDependencies(step, index, allSteps) {
        // Simple dependency logic - higher priority steps depend on lower priority ones
        const dependencies = [];
        if (index > 0) {
            dependencies.push(`Step ${allSteps[index - 1].priority}: ${allSteps[index - 1].description}`);
        }
        return dependencies;
    }

    estimateResourceRequirements(executionPlan) {
        const stepCount = executionPlan.steps.length;
        
        return {
            teamSize: Math.max(2, Math.ceil(stepCount / 3)),
            totalEffort: stepCount * 2, // 2 person-days per step
            primaryTools: ['Project Management Software', 'Communication Tools', 'Documentation Platform'],
            budgetEstimate: `$${stepCount * 1000}-${stepCount * 2000}`,
            riskLevel: stepCount > 8 ? 'Medium' : 'Low'
        };
    }

    // Results display methods
    displayExecutionPlan(executionPlan) {
        const resultsDisplay = document.getElementById('execution-plan-display');
        if (!resultsDisplay) return;

        const stepsHtml = executionPlan.steps.map(step => `
            <div class="border-l-4 border-blue-500 pl-4 mb-4 bg-blue-50 p-4 rounded-r-lg">
                <h4 class="font-semibold text-lg text-gray-900 mb-2">${step.priority}. ${step.description}</h4>
                <div class="flex items-center space-x-4 text-sm text-gray-600">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        Priority ${step.priority}
                    </span>
                </div>
            </div>
        `).join('');

        resultsDisplay.innerHTML = `
            <div class="results-content">
                <div class="mb-6">
                    <h2 class="text-2xl font-bold text-gray-900 mb-4">Task Execution Plan</h2>
                    <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                        <h3 class="text-lg font-semibold text-blue-900 mb-2">Summary</h3>
                        <p class="text-blue-800">${executionPlan.summary}</p>
                    </div>
                </div>

                <div class="mb-6">
                    <h3 class="text-xl font-semibold text-gray-900 mb-4">Execution Steps</h3>
                    <div class="space-y-4">
                        ${stepsHtml}
                    </div>
                </div>

                <div class="bg-green-50 border border-green-200 rounded-lg p-4">
                    <h3 class="text-lg font-semibold text-green-900 mb-2">Estimated Time</h3>
                    <p class="text-lg font-semibold text-green-800">${executionPlan.estimatedTime}</p>
                </div>
            </div>
        `;
    }

    displayComparisonView(scenario, executionPlan) {
        const comparisonView = document.getElementById('comparison-view');
        if (!comparisonView) return;

        comparisonView.innerHTML = `
            <div class="comparison-view">
                <div class="comparison-section input">
                    <h4 class="font-semibold text-gray-900 mb-3 flex items-center">
                        <span class="text-lg mr-2">📥</span>
                        Original Task
                    </h4>
                    <div class="space-y-3">
                        <div>
                            <span class="text-sm font-medium text-gray-700">Description:</span>
                            <p class="text-sm text-gray-600 mt-1">${scenario.title}</p>
                        </div>
                        <div>
                            <span class="text-sm font-medium text-gray-700">Category:</span>
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 ml-2">
                                ${scenario.category}
                            </span>
                        </div>
                        <div>
                            <span class="text-sm font-medium text-gray-700">Complexity:</span>
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${scenario.getComplexityColor()} ml-2">
                                ${scenario.complexity}
                            </span>
                        </div>
                    </div>
                </div>
                
                <div class="comparison-section output">
                    <h4 class="font-semibold text-gray-900 mb-3 flex items-center">
                        <span class="text-lg mr-2">📤</span>
                        Generated Plan
                    </h4>
                    <div class="space-y-3">
                        <div>
                            <span class="text-sm font-medium text-gray-700">Steps Generated:</span>
                            <p class="text-sm text-gray-600 mt-1">${executionPlan.steps.length} actionable steps</p>
                        </div>
                        <div>
                            <span class="text-sm font-medium text-gray-700">Estimated Duration:</span>
                            <p class="text-sm text-gray-600 mt-1">${executionPlan.estimatedTime}</p>
                        </div>
                        <div>
                            <span class="text-sm font-medium text-gray-700">Plan Summary:</span>
                            <p class="text-sm text-gray-600 mt-1">${executionPlan.summary}</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
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