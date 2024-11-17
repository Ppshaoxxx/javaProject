package com.zdf.flowsvr.util.redisIdempotent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisIdempotentAspect {

    private final RedisUtils redisUtils;

    public RedisIdempotentAspect(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 定义Pointcut，用于拦截service包中的所有方法
     */
    @Pointcut("execution(* com.zdf.flowsvr.service..*(..)) && @annotation(redisIdempotent)")
    public void redisIdempotentMethods(RedisIdempotent redisIdempotent) {
    }

    /**
     * 定义环绕通知，处理幂等性逻辑
     */
    @Around("redisIdempotentMethods(redisIdempotent)")
    public Object handleRedisIdempotent(ProceedingJoinPoint joinPoint, RedisIdempotent redisIdempotent) throws Throwable {
        // 生成幂等键
        String key = generateKey(joinPoint);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("无法生成幂等键");
        }

        boolean success = redisUtils.setIfAbsent(key, "1", 10, TimeUnit.MINUTES);

        if (!success) {
            throw new IllegalStateException("重复操作");
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 可选：操作完成后清理key，视业务需求决定是否需要
        }
    }

    /**
     * 动态生成幂等键
     */
    private String generateKey(ProceedingJoinPoint joinPoint) {
        // 获取类名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 获取参数
        Object[] args = joinPoint.getArgs();
        String argsString = Arrays.toString(args);

        // 原始键内容
        String rawKey = String.format("%s:%s:%s", className, methodName, argsString);

        // 对键进行MD5编码
        return "IDEMPOTENT:" + md5(rawKey);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
}