CREATE TABLE firmware_model_compatibility (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firmware_id UUID NOT NULL REFERENCES firmwares(id) ON DELETE CASCADE,
    model VARCHAR(50) NOT NULL,
    UNIQUE (firmware_id, model)
);