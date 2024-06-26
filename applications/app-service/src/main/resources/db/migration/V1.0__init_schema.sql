-- This table is in charge of storing the information of the request to create application api.
CREATE TABLE IF NOT EXISTS public.loan_application (
    id SERIAL PRIMARY KEY,
    process_id VARCHAR(100) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    request JSONB NOT NULL,
    response JSONB NOT NULL,
    application_status VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
    offer_status VARCHAR(50) DEFAULT 'UNFINISHED' NOT NULL,
    email_status VARCHAR(50) DEFAULT 'UNSENT' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_loan_application_process_id ON loan_application (process_id);

-- This table is in charge of storing the information of ocr document.
CREATE TABLE IF NOT EXISTS public.ocr_document (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data JSONB NULL,
    failure_code VARCHAR(50) NULL,
    failure_reason VARCHAR(250) NULL,
    analysis_id VARCHAR(100) NOT NULL UNIQUE,
    storage_id VARCHAR(200) NOT NULL UNIQUE,
    storage_route VARCHAR(200) NOT NULL,
    process_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_ocr_document_process_id ON ocr_document (process_id);