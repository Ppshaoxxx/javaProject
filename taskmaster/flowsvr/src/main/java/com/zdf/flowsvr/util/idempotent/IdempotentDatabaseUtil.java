package com.zdf.flowsvr.util.idempotent;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdempotentDatabaseUtil {

    private final JdbcTemplate jdbcTemplate;

    public IdempotentDatabaseUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 尝试保存幂等键
     *
     * @param key 幂等键
     * @return 如果键不存在并保存成功，则返回true；否则返回false
     */
    public boolean saveKeyIfAbsent(String key) {
        String sql = "INSERT INTO idempotent_keys (idempotent_key) VALUES (?)";
        try {
            jdbcTemplate.update(sql, key);
            return true; // 插入成功
        } catch (Exception e) {
            return false; // 键已存在
        }
    }

    /**
     * 删除幂等键
     *
     * @param key 幂等键
     */
    public void deleteKey(String key) {
        String sql = "DELETE FROM idempotent_keys WHERE idempotent_key = ?";
        jdbcTemplate.update(sql, key);
    }

    /**
     * 检查是否存在幂等键
     *
     * @param key 幂等键
     * @return 存在则返回true，否则返回false
     */
    public boolean exists(String key) {
        String sql = "SELECT COUNT(1) FROM idempotent_keys WHERE idempotent_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{key}, Integer.class);
        return count != null && count > 0;
    }

    /**
     * 清理过期幂等键（可选，用于定期清理）
     *
     * @param durationInMinutes 清理指定分钟数之前的键
     */
    public void cleanOldKeys(int durationInMinutes) {
        String sql = "DELETE FROM idempotent_keys WHERE created_at < NOW() - INTERVAL ? MINUTE";
        jdbcTemplate.update(sql, durationInMinutes);
    }
}
