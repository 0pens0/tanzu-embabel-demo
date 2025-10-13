// Main application JavaScript for Embabel Demo

class EmbabelDemo {
    constructor() {
        this.executeButton = document.getElementById('executeButton');
        this.taskInput = document.getElementById('taskInput');
        this.executionProgress = document.getElementById('executionProgress');
        this.progressSteps = document.getElementById('progressSteps');
        this.results = document.getElementById('results');
        this.resultsContent = document.getElementById('resultsContent');
        this.sampleTasks = document.getElementById('sampleTasks');
        
        this.init();
    }

    init() {
        this.loadSampleTasks();
        this.setupEventListeners();
    }

    setupEventListeners() {
        this.executeButton.addEventListener('click', () => this.executeTask());
        this.taskInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && e.ctrlKey) {
                this.executeTask();
            }
        });
    }

    async loadSampleTasks() {
        try {
            const response = await fetch('/api/sample-tasks');
            const tasks = await response.json();
            
            tasks.forEach(task => {
                const button = document.createElement('button');
                button.className = 'sample-task bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-sm transition-colors';
                button.textContent = task.title;
                button.addEventListener('click', () => {
                    this.taskInput.value = task.title;
                    this.taskInput.focus();
                });
                this.sampleTasks.appendChild(button);
            });
        } catch (error) {
            console.error('Failed to load sample tasks:', error);
        }
    }

    async executeTask() {
        const taskDescription = this.taskInput.value.trim();
        
        if (!taskDescription) {
            this.showError('Please enter a task description');
            return;
        }

        this.setLoading(true);
        this.hideResults();
        this.showProgress();

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
            this.displayExecutionTrace(executionTrace);
            
        } catch (error) {
            console.error('Error executing task:', error);
            this.showError('Failed to execute task. Please try again.');
        } finally {
            this.setLoading(false);
        }
    }

    showProgress() {
        this.executionProgress.classList.remove('hidden');
        this.progressSteps.innerHTML = '';
        
        // Show initial progress steps
        const steps = [
            { name: 'analyzeTask', description: 'Analyzing task and determining complexity...' },
            { name: 'breakDownIntoSteps', description: 'Breaking down into actionable steps...' },
            { name: 'prioritizeSteps', description: 'Prioritizing steps based on dependencies...' },
            { name: 'createExecutionPlan', description: 'Creating final execution plan...' }
        ];

        steps.forEach((step, index) => {
            const stepElement = this.createProgressStep(step.name, step.description, index + 1);
            this.progressSteps.appendChild(stepElement);
            
            // Simulate execution delay
            setTimeout(() => {
                this.updateProgressStep(stepElement, 'executing');
            }, index * 1000);
        });
    }

    createProgressStep(name, description, stepNumber) {
        const stepDiv = document.createElement('div');
        stepDiv.className = 'progress-step flex items-start p-4 border border-gray-200 rounded-lg mb-4';
        stepDiv.innerHTML = `
            <div class="step-number">${stepNumber}</div>
            <div class="flex-1">
                <h4 class="font-semibold text-gray-900">${this.formatActionName(name)}</h4>
                <p class="text-gray-600 text-sm">${description}</p>
                <div class="mt-2 text-xs text-gray-500">
                    <span class="font-mono">@Action</span>
                </div>
            </div>
        `;
        return stepDiv;
    }

    updateProgressStep(stepElement, status) {
        const stepNumber = stepElement.querySelector('.step-number');
        const description = stepElement.querySelector('p');
        
        switch (status) {
            case 'executing':
                stepElement.classList.add('executing');
                description.textContent += ' (Executing...)';
                break;
            case 'completed':
                stepElement.classList.remove('executing');
                stepElement.classList.add('completed');
                stepNumber.classList.add('completed');
                stepNumber.textContent = '✓';
                description.textContent = description.textContent.replace(' (Executing...)', ' (Completed)');
                break;
            case 'error':
                stepElement.classList.remove('executing');
                stepElement.classList.add('error');
                stepNumber.classList.add('error');
                stepNumber.textContent = '✗';
                description.textContent = description.textContent.replace(' (Executing...)', ' (Error)');
                break;
        }
    }

    displayExecutionTrace(executionTrace) {
        // Update all progress steps to completed
        const stepElements = this.progressSteps.querySelectorAll('.progress-step');
        stepElements.forEach(step => {
            this.updateProgressStep(step, 'completed');
        });

        // Show results
        this.showResults(executionTrace);
    }

    showResults(executionTrace) {
        if (executionTrace.finalResult) {
            this.resultsContent.innerHTML = this.formatExecutionPlan(executionTrace.finalResult);
        } else {
            this.resultsContent.innerHTML = '<div class="error">Failed to create execution plan</div>';
        }
        
        this.results.classList.remove('hidden');
        
        // Scroll to results
        this.results.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    formatExecutionPlan(executionPlan) {
        const stepsHtml = executionPlan.steps.map(step => `
            <div class="border-l-4 border-blue-500 pl-4 mb-4">
                <h4 class="font-semibold text-lg">${step.priority}. ${step.title}</h4>
                <p class="text-gray-600 mb-2">${step.description}</p>
                <div class="flex items-center space-x-4 text-sm text-gray-500">
                    <span><strong>Priority:</strong> ${step.priority}</span>
                    <span><strong>Time:</strong> ${step.estimatedTime}</span>
                    <span><strong>Category:</strong> ${step.category}</span>
                </div>
            </div>
        `).join('');

        return `
            <div class="results-content">
                <h1>Task Execution Plan</h1>
                
                <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                    <h2>Original Task</h2>
                    <p><strong>Description:</strong> ${executionPlan.originalTask.description}</p>
                    <p><strong>Category:</strong> ${executionPlan.originalTask.category}</p>
                    <p><strong>Complexity:</strong> ${executionPlan.originalTask.complexity}</p>
                </div>

                <h2>Summary</h2>
                <p>${executionPlan.summary}</p>

                <h2>Execution Steps</h2>
                ${stepsHtml}

                <div class="bg-green-50 border border-green-200 rounded-lg p-4 mt-6">
                    <h3>Total Estimated Time</h3>
                    <p class="text-lg font-semibold text-green-800">${executionPlan.totalEstimatedTime}</p>
                </div>
            </div>
        `;
    }

    formatActionName(actionName) {
        return actionName
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    }

    setLoading(loading) {
        this.executeButton.disabled = loading;
        if (loading) {
            this.executeButton.innerHTML = '<div class="spinner"></div>Processing...';
        } else {
            this.executeButton.textContent = 'Break Down Task';
        }
    }

    showError(message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error';
        errorDiv.textContent = message;
        
        // Remove existing error messages
        const existingErrors = document.querySelectorAll('.error');
        existingErrors.forEach(error => error.remove());
        
        // Insert error message
        this.taskInput.parentNode.insertBefore(errorDiv, this.taskInput.nextSibling);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }

    hideResults() {
        this.results.classList.add('hidden');
    }
}

// Initialize the demo when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new EmbabelDemo();
});