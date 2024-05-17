-- This table is in charge of storing the information of the request to create application api.
CREATE TABLE IF NOT EXISTS public.loan_application (
    id SERIAL PRIMARY KEY,
    process_id VARCHAR(100) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    request JSONB NOT NULL,
    response JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_process_id ON loan_application (process_id);