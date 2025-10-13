// Step Manager for Embabel Demo Wizard Flow

class StepManager {
    constructor() {
        this.steps = [
            { id: 1, name: 'select', title: 'Select Task', icon: '🎯' },
            { id: 2, name: 'execute', title: 'Agent Execution', icon: '🤖' },
            { id: 3, name: 'results', title: 'Review Results', icon: '📊' },
            { id: 4, name: 'learn', title: 'Learn More', icon: '📚' }
        ];
        this.currentStep = 1;
        this.stepData = {};
        this.init();
    }

    init() {
        this.createProgressIndicator();
        this.setupEventListeners();
        this.goToStep(1);
    }

    createProgressIndicator() {
        const container = document.getElementById('demo-container');
        if (!container) return;

        const progressHtml = `
            <div id="step-progress" class="sticky top-0 bg-white shadow-sm z-10 border-b">
                <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div class="step-indicator flex items-center justify-between py-4">
                        ${this.steps.map(step => `
                            <div class="step flex items-center ${step.id === this.currentStep ? 'active' : ''}" 
                                 data-step="${step.id}">
                                <div class="step-circle flex items-center justify-center w-8 h-8 rounded-full border-2 mr-2">
                                    <span class="step-icon">${step.icon}</span>
                                </div>
                                <span class="step-title font-medium">${step.title}</span>
                            </div>
                            ${step.id < this.steps.length ? '<div class="step-connector flex-1 h-0.5 bg-gray-200 mx-4"></div>' : ''}
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
        
        container.insertAdjacentHTML('afterbegin', progressHtml);
    }

    setupEventListeners() {
        document.addEventListener('click', (e) => {
            if (e.target.closest('.step')) {
                const stepId = parseInt(e.target.closest('.step').dataset.step);
                if (this.canGoToStep(stepId)) {
                    this.goToStep(stepId);
                }
            }
        });
    }

    goToStep(stepId) {
        if (!this.canGoToStep(stepId)) {
            console.warn(`Cannot go to step ${stepId} from step ${this.currentStep}`);
            return;
        }

        this.hideAllSteps();
        this.showStep(stepId);
        this.updateProgressIndicator();
        this.currentStep = stepId;
        
        // Trigger step-specific initialization
        this.initializeStep(stepId);
    }

    canGoToStep(stepId) {
        // Allow going back to any previous step
        if (stepId <= this.currentStep) return true;
        
        // For forward navigation, check if current step is complete
        switch (this.currentStep) {
            case 1: // Select Task
                return this.stepData.taskSelected || stepId === 1;
            case 2: // Agent Execution
                return this.stepData.executionComplete || stepId <= 2;
            case 3: // Review Results
                return this.stepData.resultsViewed || stepId <= 3;
            case 4: // Learn More
                return true;
            default:
                return false;
        }
    }

    hideAllSteps() {
        const stepContents = document.querySelectorAll('.step-content');
        stepContents.forEach(step => {
            step.classList.add('hidden');
            step.classList.remove('active');
        });
    }

    showStep(stepId) {
        const stepElement = document.getElementById(`step-${this.steps[stepId - 1].name}`);
        if (stepElement) {
            stepElement.classList.remove('hidden');
            stepElement.classList.add('active');
        }
    }

    updateProgressIndicator() {
        const stepElements = document.querySelectorAll('.step');
        stepElements.forEach((step, index) => {
            const stepId = index + 1;
            const stepCircle = step.querySelector('.step-circle');
            const stepConnector = step.querySelector('.step-connector');
            
            if (stepId < this.currentStep) {
                // Completed step
                step.classList.add('completed');
                step.classList.remove('active');
                stepCircle.classList.add('bg-green-500', 'border-green-500', 'text-white');
                stepCircle.classList.remove('border-gray-300', 'text-gray-500');
                if (stepConnector) {
                    stepConnector.classList.add('bg-green-500');
                    stepConnector.classList.remove('bg-gray-200');
                }
            } else if (stepId === this.currentStep) {
                // Current step
                step.classList.add('active');
                step.classList.remove('completed');
                stepCircle.classList.add('bg-blue-500', 'border-blue-500', 'text-white');
                stepCircle.classList.remove('border-gray-300', 'text-gray-500', 'bg-green-500', 'border-green-500');
            } else {
                // Future step
                step.classList.remove('active', 'completed');
                stepCircle.classList.add('border-gray-300', 'text-gray-500');
                stepCircle.classList.remove('bg-blue-500', 'border-blue-500', 'text-white', 'bg-green-500', 'border-green-500');
                if (stepConnector) {
                    stepConnector.classList.add('bg-gray-200');
                    stepConnector.classList.remove('bg-green-500');
                }
            }
        });
    }

    initializeStep(stepId) {
        switch (stepId) {
            case 1:
                this.initializeTaskSelection();
                break;
            case 2:
                this.initializeAgentExecution();
                break;
            case 3:
                this.initializeResultsReview();
                break;
            case 4:
                this.initializeEducationalInsights();
                break;
        }
    }

    initializeTaskSelection() {
        // Load sample scenarios if not already loaded
        if (window.embabelDemo && !this.stepData.scenariosLoaded) {
            window.embabelDemo.loadScenarios();
            this.stepData.scenariosLoaded = true;
        }
    }

    initializeAgentExecution() {
        // Prepare execution timeline
        const timeline = document.getElementById('action-timeline');
        if (timeline && !timeline.hasChildNodes()) {
            timeline.innerHTML = '<div class="text-center text-gray-500 py-8">Select a task to see agent execution...</div>';
        }
    }

    initializeResultsReview() {
        // Display execution plan results if available
        if (window.embabelDemo && window.embabelDemo.executionData && window.embabelDemo.executionData.finalResult) {
            window.embabelDemo.displayExecutionPlan(window.embabelDemo.executionData.finalResult);
            window.embabelDemo.displayComparisonView(window.embabelDemo.currentScenario, window.embabelDemo.executionData.finalResult);
        } else {
            const resultsDisplay = document.getElementById('execution-plan-display');
            if (resultsDisplay) {
                resultsDisplay.innerHTML = '<div class="text-center text-gray-500 py-8">Complete agent execution to see results...</div>';
            }
        }
    }

    initializeEducationalInsights() {
        // Load educational content
        if (window.embabelDemo) {
            window.embabelDemo.loadEducationalContent();
        }
    }

    // Step completion methods
    completeTaskSelection(taskData) {
        this.stepData.taskSelected = true;
        this.stepData.selectedTask = taskData;
        this.updateProgressIndicator();
    }

    completeExecution(executionData) {
        this.stepData.executionComplete = true;
        this.stepData.executionData = executionData;
        this.updateProgressIndicator();
    }

    completeResultsReview() {
        this.stepData.resultsViewed = true;
        this.updateProgressIndicator();
    }

    // Navigation helpers
    nextStep() {
        if (this.currentStep < this.steps.length) {
            this.goToStep(this.currentStep + 1);
        }
    }

    previousStep() {
        if (this.currentStep > 1) {
            this.goToStep(this.currentStep - 1);
        }
    }

    // Data access
    getStepData(stepId) {
        return this.stepData[`step${stepId}Data`] || {};
    }

    setStepData(stepId, data) {
        this.stepData[`step${stepId}Data`] = data;
    }

    // Reset functionality
    reset() {
        this.currentStep = 1;
        this.stepData = {};
        this.goToStep(1);
    }
}

// Export for use in other modules
window.StepManager = StepManager;
