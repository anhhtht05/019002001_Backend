CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(255) NOT NULL DEFAULT 'END_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE licenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(255),
    license_key VARCHAR(255) NOT NULL UNIQUE,
    service_type VARCHAR(255) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    duration_type VARCHAR(255) DEFAULT 'FIXED',
    usage_limit INT,
    usage_count INT,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_license_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE models (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    file_path VARCHAR(255) NOT NULL,
    metadata TEXT,
    encryption_key_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_model_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE license_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    license_id BIGINT,
    name VARCHAR(255) NOT NULL,
    duration_days INT,
    max_usage INT,
    service_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_plan_license FOREIGN KEY (license_id) REFERENCES licenses (id)
);

CREATE TABLE usage_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    client_ip VARCHAR(255),
    device_id VARCHAR(255),
    license_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    model_id BIGINT NOT NULL,
    CONSTRAINT fk_log_license FOREIGN KEY (license_id) REFERENCES licenses (id),
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_log_model FOREIGN KEY (model_id) REFERENCES models (id)
);
