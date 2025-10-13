// Sample Scenarios for Embabel Demo

const scenarios = [
    {
        id: 'podcast',
        title: 'Create a podcast series about sustainable technology with 10 episodes',
        description: 'Plan and produce a podcast series covering sustainable technology innovations',
        category: 'Content Creation',
        complexity: 'Complex',
        estimatedSteps: 8,
        expectedDuration: '3-4 weeks',
        highlights: [
            'Uses ANALYST persona for research',
            'Demonstrates multi-step breakdown',
            'Shows dependency prioritization'
        ],
        expectedActions: ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'],
        educationalNotes: {
            'analyzeTask': 'Uses ANALYST persona with temperature 0.3 for accurate complexity assessment',
            'breakDownIntoSteps': 'Demonstrates how the agent breaks complex tasks into manageable components',
            'prioritizeSteps': 'Shows dependency-based prioritization using PRIORITIZER persona',
            'createExecutionPlan': 'Final assembly of all components into a cohesive execution plan'
        }
    },
    {
        id: 'mobile-app',
        title: 'Develop a mobile app for tracking personal fitness goals with social features',
        description: 'Build a fitness tracking app with social sharing and community features',
        category: 'Mobile Development',
        complexity: 'Very Complex',
        estimatedSteps: 12,
        expectedDuration: '4-6 weeks',
        highlights: [
            'Shows technical architecture planning',
            'Demonstrates user experience considerations',
            'Includes integration complexity'
        ],
        expectedActions: ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'],
        educationalNotes: {
            'analyzeTask': 'Uses ANALYST persona to assess technical complexity and requirements',
            'breakDownIntoSteps': 'Breaks down into frontend, backend, database, and integration components',
            'prioritizeSteps': 'Prioritizes based on technical dependencies and user value',
            'createExecutionPlan': 'Creates detailed development timeline with milestones'
        }
    },
    {
        id: 'marketing-campaign',
        title: 'Plan a marketing campaign for launching a new eco-friendly product line',
        description: 'Create a comprehensive marketing strategy for sustainable product launch',
        category: 'Business Strategy',
        complexity: 'Complex',
        estimatedSteps: 10,
        expectedDuration: '2-3 weeks',
        highlights: [
            'Shows strategic thinking process',
            'Demonstrates market analysis',
            'Includes multi-channel planning'
        ],
        expectedActions: ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'],
        educationalNotes: {
            'analyzeTask': 'Uses ANALYST persona to understand market dynamics and competition',
            'breakDownIntoSteps': 'Breaks down into research, strategy, creative, and execution phases',
            'prioritizeSteps': 'Prioritizes based on market timing and resource availability',
            'createExecutionPlan': 'Creates comprehensive campaign timeline with key milestones'
        }
    },
    {
        id: 'company-retreat',
        title: 'Organize a company retreat for 50 employees with team building activities',
        description: 'Plan logistics, activities, and budget for a comprehensive company retreat',
        category: 'Event Planning',
        complexity: 'Moderate',
        estimatedSteps: 6,
        expectedDuration: '2-3 weeks',
        highlights: [
            'Shows logistical planning',
            'Demonstrates budget considerations',
            'Includes activity coordination'
        ],
        expectedActions: ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'],
        educationalNotes: {
            'analyzeTask': 'Uses ANALYST persona to assess requirements and constraints',
            'breakDownIntoSteps': 'Breaks down into venue, activities, logistics, and budget components',
            'prioritizeSteps': 'Prioritizes based on booking deadlines and critical dependencies',
            'createExecutionPlan': 'Creates detailed timeline with vendor coordination and checkpoints'
        }
    }
];

class Scenario {
    constructor(data) {
        Object.assign(this, data);
    }

    renderCard() {
        const complexityColor = this.getComplexityColor();
        const categoryIcon = this.getCategoryIcon();
        
        return `
            <div class="scenario-card bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer border border-gray-200" 
                 data-scenario-id="${this.id}">
                <div class="p-6">
                    <div class="flex items-start justify-between mb-4">
                        <div class="flex items-center">
                            <span class="text-2xl mr-3">${categoryIcon}</span>
                            <div>
                                <h3 class="text-lg font-semibold text-gray-900">${this.title}</h3>
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${complexityColor}">
                                    ${this.complexity}
                                </span>
                            </div>
                        </div>
                        <div class="text-right text-sm text-gray-500">
                            <div>${this.estimatedSteps} steps</div>
                            <div>${this.expectedDuration}</div>
                        </div>
                    </div>
                    
                    <p class="text-gray-600 text-sm mb-4">${this.description}</p>
                    
                    <div class="mb-4">
                        <div class="text-xs font-medium text-gray-700 mb-2">Key Highlights:</div>
                        <ul class="text-xs text-gray-600 space-y-1">
                            ${this.highlights.map(highlight => `<li>• ${highlight}</li>`).join('')}
                        </ul>
                    </div>
                    
                    <div class="flex items-center justify-between">
                        <span class="text-xs text-gray-500">${this.category}</span>
                        <button class="scenario-select-btn bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors">
                            Select This Task
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    getComplexityColor() {
        switch (this.complexity) {
            case 'Simple': return 'bg-green-100 text-green-800';
            case 'Moderate': return 'bg-yellow-100 text-yellow-800';
            case 'Complex': return 'bg-orange-100 text-orange-800';
            case 'Very Complex': return 'bg-red-100 text-red-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    }

    getCategoryIcon() {
        switch (this.category) {
            case 'Content Creation': return '🎙️';
            case 'Mobile Development': return '📱';
            case 'Business Strategy': return '📈';
            case 'Event Planning': return '🎉';
            default: return '📋';
        }
    }

    getExpectedActions() {
        return this.expectedActions || ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'];
    }

    getEducationalNote(actionName) {
        return this.educationalNotes?.[actionName] || 'No specific notes for this action';
    }

    // Static methods for scenario management
    static getAll() {
        return scenarios.map(data => new Scenario(data));
    }

    static getById(id) {
        const data = scenarios.find(s => s.id === id);
        return data ? new Scenario(data) : null;
    }

    static getByCategory(category) {
        return scenarios
            .filter(s => s.category === category)
            .map(data => new Scenario(data));
    }

    static getCategories() {
        return [...new Set(scenarios.map(s => s.category))];
    }
}

// Scenario Manager for handling scenario operations
class ScenarioManager {
    constructor() {
        this.scenarios = Scenario.getAll();
        this.selectedScenario = null;
    }

    renderScenarioGrid(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const categories = Scenario.getCategories();
        let html = '';

        categories.forEach(category => {
            const categoryScenarios = this.scenarios.filter(s => s.category === category);
            html += `
                <div class="category-section mb-8">
                    <h3 class="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                        <span class="mr-2">${categoryScenarios[0].getCategoryIcon()}</span>
                        ${category}
                    </h3>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        ${categoryScenarios.map(scenario => scenario.renderCard()).join('')}
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Scenario selection
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('scenario-select-btn')) {
                const card = e.target.closest('.scenario-card');
                const scenarioId = card.dataset.scenarioId;
                this.selectScenario(scenarioId);
            }
        });
    }

    selectScenario(scenarioId) {
        this.selectedScenario = Scenario.getById(scenarioId);
        if (this.selectedScenario && window.embabelDemo) {
            window.embabelDemo.selectScenario(this.selectedScenario);
        }
    }

    getSelectedScenario() {
        return this.selectedScenario;
    }

    // Custom task creation
    createCustomTask(title, description, category = 'Custom') {
        const customScenario = new Scenario({
            id: `custom-${Date.now()}`,
            title: title,
            description: description,
            category: category,
            complexity: 'Moderate',
            estimatedSteps: 6,
            expectedDuration: '1-2 weeks',
            highlights: ['Custom task created by user'],
            expectedActions: ['analyzeTask', 'breakDownIntoSteps', 'prioritizeSteps', 'createExecutionPlan'],
            educationalNotes: {
                'analyzeTask': 'Analyzing custom task requirements',
                'breakDownIntoSteps': 'Breaking down custom task into actionable steps',
                'prioritizeSteps': 'Prioritizing steps based on dependencies',
                'createExecutionPlan': 'Creating execution plan for custom task'
            }
        });

        return customScenario;
    }
}

// Export for use in other modules
window.Scenario = Scenario;
window.ScenarioManager = ScenarioManager;
