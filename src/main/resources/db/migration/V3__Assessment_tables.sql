-- Assessment and Answer tables
-- Based on PRD section 13 - Database model

-- Create assessment table
CREATE TABLE assessment (
    id VARCHAR(100) PRIMARY KEY,
    role_id VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    tenant_id VARCHAR(100),
    user_id VARCHAR(100),
    locale VARCHAR(10) NOT NULL,
    hours_per_week INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    email_hash VARCHAR(256),
    consent BOOLEAN NOT NULL DEFAULT FALSE,
    prev_assessment_id VARCHAR(100)
);

-- Create answer table
CREATE TABLE answer (
    id BIGSERIAL PRIMARY KEY,
    assessment_id VARCHAR(100) NOT NULL REFERENCES assessment(id),
    question_id VARCHAR(100) NOT NULL,
    value_numeric INTEGER,
    value_text TEXT,
    UNIQUE(assessment_id, question_id)
);

-- Create plan table for AI-generated plans
CREATE TABLE plan (
    id BIGSERIAL PRIMARY KEY,
    assessment_id VARCHAR(100) NOT NULL REFERENCES assessment(id) UNIQUE,
    plan_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create telemetry table for basic metrics
CREATE TABLE telemetry (
    id BIGSERIAL PRIMARY KEY,
    assessment_id VARCHAR(100),
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_assessment_role_id ON assessment(role_id);
CREATE INDEX idx_assessment_created_at ON assessment(created_at);
CREATE INDEX idx_assessment_tenant ON assessment(tenant_id);
CREATE INDEX idx_answer_assessment_id ON answer(assessment_id);
CREATE INDEX idx_plan_assessment_id ON plan(assessment_id);
CREATE INDEX idx_telemetry_event_type ON telemetry(event_type);
CREATE INDEX idx_telemetry_created_at ON telemetry(created_at);
