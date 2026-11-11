// Sample App Ideas for Spring Coding Agent

const scenarios = [
    {
        id: 'blog-platform',
        title: 'Create a REST API for a blog platform with CRUD operations',
        description: 'Spring Boot REST API with posts, comments, and user management',
        category: 'Web Application',
        complexity: 'Moderate',
        estimatedSteps: 6,
        expectedDuration: '2-3 hours',
        highlights: [
            'REST API with CRUD operations',
            'JPA entities and relationships',
            'User authentication',
            'JSON API responses'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies entities (Post, Comment, User), API endpoints, and authentication needs',
            'defineApplicationArchitecture': 'Designs layered architecture with controllers, services, and repositories',
            'breakDownIntoImplementationSteps': 'Creates step-by-step implementation plan with dependencies',
            'generateProjectStructure': 'Sets up Maven project with Spring Boot dependencies',
            'generateSourceCode': 'Generates complete Spring Boot application with all source files'
        }
    },
    {
        id: 'todo-app',
        title: 'Build a todo list web app with Spring Boot backend',
        description: 'Full-stack todo application with REST API and HTML frontend',
        category: 'Web Application',
        complexity: 'Simple',
        estimatedSteps: 5,
        expectedDuration: '1-2 hours',
        highlights: [
            'REST API with CRUD operations',
            'Database persistence',
            'Modern web UI',
            'Frontend integration'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies Todo entity, CRUD operations, and frontend requirements',
            'defineApplicationArchitecture': 'Designs simple layered architecture with REST API and static frontend',
            'breakDownIntoImplementationSteps': 'Plans implementation order: entities → services → controllers → frontend',
            'generateProjectStructure': 'Creates Maven project with Spring Boot web and JPA dependencies',
            'generateSourceCode': 'Generates complete application with Java backend and HTML/CSS/JS frontend'
        }
    },
    {
        id: 'product-catalog',
        title: 'Create a product catalog API with search functionality',
        description: 'E-commerce product catalog with filtering, pagination, and search',
        category: 'Web Application',
        complexity: 'Moderate',
        estimatedSteps: 7,
        expectedDuration: '2-3 hours',
        highlights: [
            'Advanced search features',
            'Filtering & pagination',
            'Database relationships',
            'REST API best practices'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies Product, Category entities, search requirements, and API endpoints',
            'defineApplicationArchitecture': 'Designs architecture with search service, pagination, and filtering',
            'breakDownIntoImplementationSteps': 'Plans complex implementation with search algorithms and database optimization',
            'generateProjectStructure': 'Sets up project with additional dependencies for search and pagination',
            'generateSourceCode': 'Generates sophisticated application with advanced search capabilities'
        }
    },
    {
        id: 'user-management',
        title: 'Build a user management system with authentication',
        description: 'Spring Boot application with user registration, login, and profile management',
        category: 'Web Application',
        complexity: 'Moderate',
        estimatedSteps: 6,
        expectedDuration: '2-3 hours',
        highlights: [
            'User authentication',
            'Security implementation',
            'Profile management',
            'Session handling'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies User entity, authentication requirements, and security needs',
            'defineApplicationArchitecture': 'Designs secure architecture with authentication and authorization',
            'breakDownIntoImplementationSteps': 'Plans security-first implementation with proper authentication flow',
            'generateProjectStructure': 'Sets up project with Spring Security dependencies and configuration',
            'generateSourceCode': 'Generates secure application with proper authentication and user management'
        }
    },
    {
        id: 'bookstore-api',
        title: 'Create a bookstore REST API with books, authors, and customers',
        description: 'Complete bookstore management system with inventory and customer management',
        category: 'Web Application',
        complexity: 'Moderate',
        estimatedSteps: 7,
        expectedDuration: '2-3 hours',
        highlights: [
            'Complex entity relationships',
            'Inventory management',
            'Customer operations',
            'Business logic implementation'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies Book, Author, Customer entities with complex relationships',
            'defineApplicationArchitecture': 'Designs architecture with proper entity relationships and business logic',
            'breakDownIntoImplementationSteps': 'Plans implementation considering entity relationships and business rules',
            'generateProjectStructure': 'Sets up project with JPA and relationship mapping dependencies',
            'generateSourceCode': 'Generates application with complex entity relationships and business logic'
        }
    },
    {
        id: 'event-management',
        title: 'Build an event management system with registration and ticketing',
        description: 'Event management platform with event creation, registration, and ticket management',
        category: 'Web Application',
        complexity: 'Complex',
        estimatedSteps: 8,
        expectedDuration: '3-4 hours',
        highlights: [
            'Event management',
            'Registration system',
            'Ticket generation',
            'Payment integration ready'
        ],
        expectedActions: ['analyzeApplicationRequirements', 'defineApplicationArchitecture', 'breakDownIntoImplementationSteps', 'generateProjectStructure', 'generateSourceCode'],
        educationalNotes: {
            'analyzeApplicationRequirements': 'Identifies Event, Registration, Ticket entities with complex business rules',
            'defineApplicationArchitecture': 'Designs architecture with event management and registration workflows',
            'breakDownIntoImplementationSteps': 'Plans complex implementation with business logic and state management',
            'generateProjectStructure': 'Sets up project with comprehensive dependencies for event management',
            'generateSourceCode': 'Generates sophisticated application with complex business logic and workflows'
        }
    }
];

class ScenarioManager {
    constructor() {
        this.scenarios = scenarios;
    }

    getAllScenarios() {
        return this.scenarios;
    }

    getScenarioById(id) {
        return this.scenarios.find(scenario => scenario.id === id);
    }

    getScenariosByCategory(category) {
        return this.scenarios.filter(scenario => scenario.category === category);
    }

    getScenariosByComplexity(complexity) {
        return this.scenarios.filter(scenario => scenario.complexity === complexity);
    }

    renderScenarioCards(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        container.innerHTML = this.scenarios.map(scenario => `
            <div class="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow cursor-pointer scenario-card" 
                 data-scenario-id="${scenario.id}">
                <div class="flex items-start justify-between mb-4">
                    <div class="flex-1">
                        <h4 class="text-lg font-semibold text-gray-900 mb-2">${scenario.title}</h4>
                        <p class="text-gray-600 text-sm mb-3">${scenario.description}</p>
                    </div>
                    <div class="ml-4 flex flex-col items-end">
                        <span class="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full mb-2">${scenario.category}</span>
                        <span class="bg-gray-100 text-gray-600 text-xs px-2 py-1 rounded-full">${scenario.complexity}</span>
                    </div>
                </div>
                
                <div class="space-y-2 text-xs text-gray-500 mb-4">
                    <div class="flex items-center">
                        <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                        <span>${scenario.estimatedSteps} implementation steps</span>
                    </div>
                    <div class="flex items-center">
                        <span class="w-2 h-2 bg-blue-500 rounded-full mr-2"></span>
                        <span>Estimated time: ${scenario.expectedDuration}</span>
                    </div>
                </div>
                
                <div class="flex flex-wrap gap-1">
                    ${scenario.highlights.map(highlight => 
                        `<span class="bg-gray-100 text-gray-600 text-xs px-2 py-1 rounded">${highlight}</span>`
                    ).join('')}
                </div>
            </div>
        `).join('');

        // Add click handlers
        container.querySelectorAll('.scenario-card').forEach(card => {
            card.addEventListener('click', () => {
                const scenarioId = card.dataset.scenarioId;
                const scenario = this.getScenarioById(scenarioId);
                if (scenario && window.embabelDemo) {
                    window.embabelDemo.executeScenario(scenario);
                }
            });
        });
    }
}

// Make ScenarioManager globally available
window.ScenarioManager = ScenarioManager;