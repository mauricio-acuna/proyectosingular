-- Initial database schema for AI Readiness Web
-- Based on PRD section 13 - Database model

-- Create roles table
CREATE TABLE role (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create role versions table
CREATE TABLE role_version (
    id BIGSERIAL PRIMARY KEY,
    role_id VARCHAR(100) NOT NULL REFERENCES role(id),
    version VARCHAR(50) NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, version)
);

-- Create questions table
CREATE TABLE question (
    id VARCHAR(100) PRIMARY KEY,
    text_es TEXT NOT NULL,
    text_en TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('LIKERT', 'MULTIPLE', 'TEXT')),
    pillar VARCHAR(20) NOT NULL CHECK (pillar IN ('TECH', 'AI', 'COMMUNICATION', 'PORTFOLIO')),
    help TEXT
);

-- Create role_question relationship table
CREATE TABLE role_question (
    id BIGSERIAL PRIMARY KEY,
    role_version_id BIGINT NOT NULL REFERENCES role_version(id),
    question_id VARCHAR(100) NOT NULL REFERENCES question(id),
    weight DECIMAL(3,2) NOT NULL DEFAULT 1.0,
    question_order INTEGER NOT NULL,
    UNIQUE(role_version_id, question_id)
);

-- Create indexes for performance
CREATE INDEX idx_role_version_role_id ON role_version(role_id);
CREATE INDEX idx_role_version_published ON role_version(is_published);
CREATE INDEX idx_question_pillar ON question(pillar);
CREATE INDEX idx_role_question_version ON role_question(role_version_id);
CREATE INDEX idx_role_question_order ON role_question(role_version_id, question_order);
