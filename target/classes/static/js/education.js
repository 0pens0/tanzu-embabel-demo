// Educational content JavaScript for Embabel Demo

class EmbabelEducation {
    constructor() {
        this.init();
    }

    init() {
        this.setupSmoothScrolling();
        this.setupCodeHighlighting();
        this.setupInteractiveElements();
    }

    setupSmoothScrolling() {
        // Add smooth scrolling to navigation links
        document.querySelectorAll('nav a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    }

    setupCodeHighlighting() {
        // Ensure all code blocks are highlighted
        document.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightElement(block);
        });
    }

    setupInteractiveElements() {
        // Add click handlers for building blocks
        this.setupBuildingBlockInteractions();
        
        // Add hover effects for benefits
        this.setupBenefitsInteractions();
    }

    setupBuildingBlockInteractions() {
        const buildingBlocks = document.querySelectorAll('#building-blocks .bg-white');
        
        buildingBlocks.forEach(block => {
            block.addEventListener('mouseenter', () => {
                this.highlightRelatedCode(block);
            });
            
            block.addEventListener('mouseleave', () => {
                this.removeCodeHighlight();
            });
        });
    }

    setupBenefitsInteractions() {
        const benefits = document.querySelectorAll('#benefits .flex.items-start');
        
        benefits.forEach(benefit => {
            benefit.addEventListener('mouseenter', () => {
                benefit.style.transform = 'translateX(4px)';
                benefit.style.transition = 'transform 0.2s ease';
            });
            
            benefit.addEventListener('mouseleave', () => {
                benefit.style.transform = 'translateX(0)';
            });
        });
    }

    highlightRelatedCode(block) {
        // Find the code block within the building block
        const codeBlock = block.querySelector('pre code');
        if (codeBlock) {
            codeBlock.style.backgroundColor = '#fef3c7';
            codeBlock.style.transition = 'background-color 0.3s ease';
        }
    }

    removeCodeHighlight() {
        document.querySelectorAll('pre code').forEach(code => {
            code.style.backgroundColor = '';
        });
    }

    // Method to show framework information
    async loadFrameworkInfo() {
        try {
            const response = await fetch('/api/framework-info');
            const frameworkInfo = await response.json();
            this.displayFrameworkInfo(frameworkInfo);
        } catch (error) {
            console.error('Failed to load framework info:', error);
        }
    }

    displayFrameworkInfo(info) {
        // This could be used to dynamically populate framework information
        // For now, the information is static in the HTML
        console.log('Framework Info:', info);
    }

    // Method to create interactive code examples
    createInteractiveCodeExample() {
        const codeExamples = document.querySelectorAll('.bg-gray-900 pre code');
        
        codeExamples.forEach(code => {
            const pre = code.parentElement;
            const copyButton = document.createElement('button');
            copyButton.className = 'absolute top-2 right-2 bg-gray-700 text-white px-2 py-1 rounded text-xs hover:bg-gray-600 transition-colors';
            copyButton.textContent = 'Copy';
            
            // Make the pre element relative for absolute positioning
            pre.style.position = 'relative';
            pre.appendChild(copyButton);
            
            copyButton.addEventListener('click', () => {
                navigator.clipboard.writeText(code.textContent).then(() => {
                    copyButton.textContent = 'Copied!';
                    setTimeout(() => {
                        copyButton.textContent = 'Copy';
                    }, 2000);
                });
            });
        });
    }

    // Method to add tooltips to technical terms
    addTooltips() {
        const tooltipTerms = {
            'OODA loop': 'Observe, Orient, Decide, Act - a decision-making framework',
            'FSM': 'Finite State Machine - a computational model',
            'AOP': 'Aspect-Oriented Programming - programming paradigm',
            'IoC': 'Inversion of Control - design principle',
            'QoS': 'Quality of Service - performance characteristics'
        };

        Object.entries(tooltipTerms).forEach(([term, description]) => {
            const regex = new RegExp(`\\b${term}\\b`, 'gi');
            document.body.innerHTML = document.body.innerHTML.replace(regex, 
                `<span class="tooltip-term" title="${description}">${term}</span>`
            );
        });
    }

    // Method to create animated counters for statistics
    animateCounters() {
        const counters = document.querySelectorAll('.counter');
        
        counters.forEach(counter => {
            const target = parseInt(counter.getAttribute('data-target'));
            const increment = target / 100;
            let current = 0;
            
            const updateCounter = () => {
                if (current < target) {
                    current += increment;
                    counter.textContent = Math.ceil(current);
                    requestAnimationFrame(updateCounter);
                } else {
                    counter.textContent = target;
                }
            };
            
            // Start animation when element is in viewport
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        updateCounter();
                        observer.unobserve(entry.target);
                    }
                });
            });
            
            observer.observe(counter);
        });
    }

    // Method to create collapsible sections
    createCollapsibleSections() {
        const collapsibleHeaders = document.querySelectorAll('.collapsible-header');
        
        collapsibleHeaders.forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', () => {
                const content = header.nextElementSibling;
                const isHidden = content.style.display === 'none';
                
                content.style.display = isHidden ? 'block' : 'none';
                header.querySelector('.toggle-icon').textContent = isHidden ? '−' : '+';
            });
        });
    }
}

// Initialize educational features when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new EmbabelEducation();
});