-- Seed data for Backend Java role
-- Based on PRD section 7.4 - Initial roles

-- Insert Backend Java role
INSERT INTO role (id, name, description, created_at) VALUES 
('backend-java', 'Backend Java Developer', 'Desarrollador Backend especializado en Java y Spring Framework', CURRENT_TIMESTAMP);

-- Insert initial version for Backend Java role
INSERT INTO role_version (role_id, version, is_published, created_at) VALUES 
('backend-java', '1.0', true, CURRENT_TIMESTAMP);

-- Insert questions for Backend Java role
INSERT INTO question (id, text_es, text_en, type, pillar, help) VALUES 
-- TECH pillar questions (35% weight)
('q-spring-boot', '¿Qué tan cómodo te sientes desarrollando aplicaciones con Spring Boot?', 'How comfortable are you developing applications with Spring Boot?', 'LIKERT', 'TECH', 'Considera creación de proyectos, configuración, servicios REST'),
('q-spring-security', '¿Dominas autenticación y autorización en Spring Security?', 'Do you master authentication and authorization in Spring Security?', 'LIKERT', 'TECH', 'Piensa en JWT, filtros, method security'),
('q-jpa-hibernate', '¿Qué nivel tienes en JPA/Hibernate para persistencia de datos?', 'What level do you have in JPA/Hibernate for data persistence?', 'LIKERT', 'TECH', 'Considera entidades, consultas, optimización'),
('q-microservices', '¿Has trabajado con arquitecturas de microservicios?', 'Have you worked with microservices architectures?', 'LIKERT', 'TECH', 'Considera comunicación entre servicios, service discovery'),
('q-testing-java', '¿Qué tan sólido eres en testing (JUnit, Mockito, TestContainers)?', 'How solid are you in testing (JUnit, Mockito, TestContainers)?', 'LIKERT', 'TECH', 'Incluye unit tests, integration tests, test coverage'),
('q-database-design', '¿Tienes experiencia diseñando bases de datos relacionales?', 'Do you have experience designing relational databases?', 'LIKERT', 'TECH', 'Normalización, índices, optimización de consultas'),

-- AI pillar questions (35% weight)
('q-ai-code-assist', '¿Usas herramientas de IA para escribir código (GitHub Copilot, ChatGPT)?', 'Do you use AI tools to write code (GitHub Copilot, ChatGPT)?', 'LIKERT', 'AI', 'Frecuencia y efectividad en el uso diario'),
('q-ai-debugging', '¿Utilizas IA para debugging y resolución de problemas?', 'Do you use AI for debugging and problem solving?', 'LIKERT', 'AI', 'Análisis de errores, optimización de código'),
('q-ai-architecture', '¿Sabes integrar servicios de IA (APIs, modelos) en aplicaciones Java?', 'Do you know how to integrate AI services (APIs, models) in Java applications?', 'LIKERT', 'AI', 'OpenAI API, servicios cloud de IA, embeddings'),
('q-prompt-engineering', '¿Tienes habilidades en prompt engineering para obtener mejores resultados?', 'Do you have prompt engineering skills to get better results?', 'LIKERT', 'AI', 'Técnicas para prompts efectivos, iteración'),
('q-ai-security', '¿Conoces sobre seguridad y buenas prácticas al usar IA en desarrollo?', 'Do you know about security and best practices when using AI in development?', 'LIKERT', 'AI', 'Privacidad de datos, validación de código generado'),

-- COMMUNICATION pillar questions (15% weight)
('q-technical-docs', '¿Qué tan bien documentas tu código y arquitecturas técnicas?', 'How well do you document your code and technical architectures?', 'LIKERT', 'COMMUNICATION', 'README, API docs, diagramas técnicos'),
('q-code-review', '¿Participas activamente en code reviews y feedback técnico?', 'Do you actively participate in code reviews and technical feedback?', 'LIKERT', 'COMMUNICATION', 'Dar y recibir feedback constructivo'),
('q-technical-presentation', '¿Te sientes cómodo presentando soluciones técnicas a equipos?', 'Do you feel comfortable presenting technical solutions to teams?', 'LIKERT', 'COMMUNICATION', 'Explicar arquitecturas, decisiones técnicas'),

-- PORTFOLIO pillar questions (15% weight)
('q-github-portfolio', '¿Tienes un portafolio actualizado en GitHub con proyectos representativos?', 'Do you have an updated portfolio on GitHub with representative projects?', 'LIKERT', 'PORTFOLIO', 'Proyectos públicos, README detallados, código limpio'),
('q-ci-cd', '¿Implementas CI/CD en tus proyectos personales/profesionales?', 'Do you implement CI/CD in your personal/professional projects?', 'LIKERT', 'PORTFOLIO', 'GitHub Actions, Jenkins, pipelines automatizados'),
('q-deployment', '¿Sabes deployar aplicaciones en la nube (AWS, Azure, Docker)?', 'Do you know how to deploy applications in the cloud (AWS, Azure, Docker)?', 'LIKERT', 'PORTFOLIO', 'Containerización, servicios cloud, infraestructura'),
('q-monitoring', '¿Configuras monitoreo y observabilidad en tus aplicaciones?', 'Do you configure monitoring and observability in your applications?', 'LIKERT', 'PORTFOLIO', 'Logs, métricas, health checks, alertas');

-- Link questions to Backend Java role version 1.0
INSERT INTO role_question (role_version_id, question_id, weight, question_order) 
SELECT rv.id, q.question_id, q.weight, q.order_num
FROM role_version rv,
(VALUES 
    -- TECH questions (weight 1.0-1.2 for important ones)
    ('q-spring-boot', 1.2, 1),
    ('q-spring-security', 1.0, 2),
    ('q-jpa-hibernate', 1.1, 3),
    ('q-microservices', 0.9, 4),
    ('q-testing-java', 1.1, 5),
    ('q-database-design', 0.8, 6),
    
    -- AI questions (weight 1.0-1.3 for critical AI skills)
    ('q-ai-code-assist', 1.3, 7),
    ('q-ai-debugging', 1.1, 8),
    ('q-ai-architecture', 1.0, 9),
    ('q-prompt-engineering', 1.2, 10),
    ('q-ai-security', 1.0, 11),
    
    -- COMMUNICATION questions
    ('q-technical-docs', 1.0, 12),
    ('q-code-review', 1.1, 13),
    ('q-technical-presentation', 0.9, 14),
    
    -- PORTFOLIO questions
    ('q-github-portfolio', 1.2, 15),
    ('q-ci-cd', 1.0, 16),
    ('q-deployment', 1.1, 17),
    ('q-monitoring', 0.8, 18)
) AS q(question_id, weight, order_num)
WHERE rv.role_id = 'backend-java' AND rv.version = '1.0';
