-- V4__Update_schema_for_admin_panel.sql
-- Migration to update schema for admin panel functionality

-- Update Role table to support admin panel
ALTER TABLE role DROP CONSTRAINT IF EXISTS role_pkey;
ALTER TABLE role ALTER COLUMN id TYPE BIGSERIAL;
ALTER TABLE role ADD PRIMARY KEY (id);

-- Add new columns to role table
ALTER TABLE role ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE role ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE role ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Update role_version table
ALTER TABLE role_version ADD COLUMN IF NOT EXISTS version_number INTEGER NOT NULL DEFAULT 1;
ALTER TABLE role_version ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE role_version DROP COLUMN IF EXISTS version;
ALTER TABLE role_version DROP COLUMN IF EXISTS is_published;

-- Update question table
ALTER TABLE question DROP CONSTRAINT IF EXISTS question_pkey;
ALTER TABLE question ALTER COLUMN id TYPE BIGSERIAL;
ALTER TABLE question ADD PRIMARY KEY (id);

-- Restructure question table
ALTER TABLE question ADD COLUMN IF NOT EXISTS text VARCHAR(1000) NOT NULL DEFAULT '';
ALTER TABLE question ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE question ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE question ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE question ADD COLUMN IF NOT EXISTS context VARCHAR(500);

-- Remove old columns (migrate data first if needed)
-- UPDATE question SET text = COALESCE(text_es, text_en, '') WHERE text = '';
-- ALTER TABLE question DROP COLUMN IF EXISTS text_es;
-- ALTER TABLE question DROP COLUMN IF EXISTS text_en;
-- ALTER TABLE question DROP COLUMN IF EXISTS help;

-- Create question_options table for multiple choice options
CREATE TABLE IF NOT EXISTS question_options (
    question_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);

-- Update foreign key constraints
-- Note: This might require data migration for existing data
-- ALTER TABLE role_version DROP CONSTRAINT IF EXISTS fk_role_version_role;
-- ALTER TABLE role_version ADD CONSTRAINT fk_role_version_role 
--     FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_role_active ON role(active);
CREATE INDEX IF NOT EXISTS idx_role_category ON role(category);
CREATE INDEX IF NOT EXISTS idx_question_active ON question(active);
CREATE INDEX IF NOT EXISTS idx_question_pillar ON question(pillar);
CREATE INDEX IF NOT EXISTS idx_role_version_active ON role_version(active);
CREATE INDEX IF NOT EXISTS idx_role_version_role_active ON role_version(role_id, active);

-- Insert some sample data for testing admin panel
INSERT INTO role (name, description, category, active, created_at) 
VALUES 
    ('Frontend Developer', 'React/Vue.js frontend development specialist', 'Development', true, CURRENT_TIMESTAMP),
    ('Data Scientist', 'AI/ML data science and analytics expert', 'Data Science', true, CURRENT_TIMESTAMP),
    ('Product Manager', 'Product strategy and roadmap management', 'Product', true, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Create initial versions for new roles
INSERT INTO role_version (role_id, version_number, active, created_at)
SELECT r.id, 1, true, CURRENT_TIMESTAMP 
FROM role r 
WHERE r.category IN ('Development', 'Data Science', 'Product')
ON CONFLICT DO NOTHING;
