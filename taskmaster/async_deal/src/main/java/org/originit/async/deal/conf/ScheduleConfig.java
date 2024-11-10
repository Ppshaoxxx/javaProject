package org.originit.async.deal.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 防止定时任务串行执行
        taskRegistrar.setScheduler(taskExecutor());
    }

    public static final String EXECUTOR_SERVICE = "scheduledExecutor";

    @Bean(EXECUTOR_SERVICE)
    public ScheduledThreadPoolExecutor taskExecutor() {
        final AtomicInteger threadId = new AtomicInteger();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4, r -> {
            final Thread thread = new Thread(r);
            thread.setName("schedule-" + threadId.getAndIncrement());
            return thread;
        });
        return executor;
    }

    @Bean(name = "taskExecutor")
    ThreadPoolTaskExecutor getDataHandleExecutor(TaskExecutorBuilder builder) {
        ThreadPoolTaskExecutor taskExecutor = builder.build();
        taskExecutor.setThreadNamePrefix("task-thread-");
        return taskExecutor;
    }

}