package com.zdf.flowsvr.util.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性注解
 * 支持Redis持久和数据库持久模式
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    Mode mode() default Mode.REDIS; // 持久模式：默认Redis

    enum Mode {
        REDIS, DATABASE
    }
}

