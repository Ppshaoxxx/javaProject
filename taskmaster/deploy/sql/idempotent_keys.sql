-- 创建用于存储幂等键的表
CREATE TABLE idempotent_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键，自增，唯一标识每条记录
    idempotent_key VARCHAR(255) NOT NULL UNIQUE, -- 幂等键，唯一约束，用于防止重复操作
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 键的创建时间，默认为当前时间
) COMMENT='存储幂等键的表，用于实现幂等性操作';
