-- =============================================
--(MASTER DATA)
-- =============================================

CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id VARCHAR(100) UNIQUE NOT NULL,
    device_name VARCHAR(200) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    hardware_version VARCHAR(50),
    serial_number VARCHAR(100) UNIQUE,
    mac_address VARCHAR(17) UNIQUE,
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
    state VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE firmwares (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    checksum VARCHAR(64),
    release_notes TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE ota_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    schedule_type VARCHAR(20) DEFAULT 'IMMEDIATE',
    scheduled_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'DRAFT',
    rollout_percentage INTEGER DEFAULT 100,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE device_firmware_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    firmware_id UUID NOT NULL REFERENCES firmwares(id) ON DELETE CASCADE,
    installed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    installation_status VARCHAR(20) DEFAULT 'SUCCESS', -- SUCCESS, FAILED, PENDING
    UNIQUE(device_id, firmware_id)
);

CREATE TABLE campaign_firmware_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL REFERENCES ota_campaigns(id) ON DELETE CASCADE,
    firmware_id UUID NOT NULL REFERENCES firmwares(id) ON DELETE CASCADE,
    UNIQUE(campaign_id, firmware_id)
);

CREATE TABLE campaign_device_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL REFERENCES ota_campaigns(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, SUCCESS, FAILED
    progress INTEGER DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(campaign_id, device_id)
);


CREATE TABLE firmware_hardware_compatibility (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firmware_id UUID NOT NULL REFERENCES firmwares(id) ON DELETE CASCADE,
    hardware_version VARCHAR(50) NOT NULL,
    UNIQUE(firmware_id, hardware_version)
);

-- =============================================
--(STATUS TABLES)
-- =============================================


CREATE TABLE device_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL, -- ONLINE, OFFLINE, UPDATING, ERROR
--    ip_address INET,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE device_connection_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    event_type VARCHAR(20) NOT NULL, -- CONNECTED, DISCONNECTED, HEARTBEAT
--    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE device_operation_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    log_level VARCHAR(10) NOT NULL, -- INFO, WARN, ERROR, DEBUG
    component VARCHAR(100), -- firmware, network, hardware
    message TEXT NOT NULL,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
--(REFERENCE TABLES)
-- =============================================


CREATE TABLE user_refresh_tokens (
    user_id UUID NOT NULL,
    refresh_token_id UUID NOT NULL,
    PRIMARY KEY (user_id, refresh_token_id),
    CONSTRAINT fk_user_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_refresh_token_token
        FOREIGN KEY (refresh_token_id) REFERENCES refresh_tokens(id) ON DELETE CASCADE
);


CREATE TABLE device_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    api_key VARCHAR(100) NOT NULL,
    secret_key VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE firmware_download_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    firmware_id UUID NOT NULL REFERENCES firmwares(id) ON DELETE CASCADE,
    downloaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    ip_address INET,
    user_agent TEXT
);

