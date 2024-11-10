package org.originit.async.deal.job;

import lombok.extern.slf4j.Slf4j;
import org.originit.async.deal.service.ScheduleConfigService;
import org.originit.async.deal.service.SchedulePosService;
import org.originit.async.deal.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

@Component
@Slf4j
public class GlobalJob {

    private final List<String> taskTypes = new ArrayList<>();

    /**
     * 对应任务类型的任务是在在执行，如果为true则是执行中
     */
    private final Map<String,boolean[]> executing = new HashMap<>();

    private static final int IDX_TIMEOUT = 0;

    private static final int IDX_FULL = 1;

    private static final int IDX_EMPTY = 2;

    private static final int ARRAY_SIZE = 3;

    @Autowired
    SchedulePosService schedulePosService;

    @Autowired
    TaskService taskService;

    @Autowired
    ScheduleConfigService scheduleConfigService;

    @Autowired
    @Qualifier(value = "taskExecutor")
    ThreadPoolTaskExecutor executor;

    @Scheduled(fixedDelay = 10000, initialDelay = 0)
    public void acquireAllTaskTypes() {
        // 1. 查询出所有的任务类型
        List<String> curTaskTypes = scheduleConfigService.getTaskTypes();
        synchronized (taskTypes) {
            if (!curTaskTypes.equals(taskTypes)) {
                this.taskTypes.clear();
                this.taskTypes.addAll(curTaskTypes);
            }
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void handleTaskTableFull() {
        handleWithTaskTypes(IDX_FULL, taskType -> {
            try {
                schedulePosService.handleTaskTableFull(taskType);
            } catch (Exception e) {
                log.error("处理写表溢出时发生异常", e);
            }
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void handleTaskTableEmpty() {
        handleWithTaskTypes(IDX_EMPTY, taskType -> {
            try {
                schedulePosService.handleTaskTableEmpty(taskType);
            } catch (Exception e) {
                log.error("处理空表滚动时发生异常", e);
            }
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void handleTaskTimeout() {
        handleWithTaskTypes(IDX_TIMEOUT, taskType -> {
            try {
                taskService.dealTimeoutTasksByType(taskType);
            } catch (Exception e) {
                log.error("处理超时任务时发生异常", e);
            }
        });
    }

    private void handleWithTaskTypes(int idx, Consumer<String> handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler不能为空");
        }
        if (idx < 0 || idx >= ARRAY_SIZE) {
            throw new IllegalArgumentException("索引异常，请检查idx参数,当前idx为:" + idx + "最大idx为" + ARRAY_SIZE);
        }
        // 拷贝一份
        List<String> types;
        synchronized (taskTypes) {
            types = new ArrayList<>(taskTypes);
        }
        for (String taskType: types) {
            synchronized (executing) {
                if (!executing.containsKey(taskType)) {
                    executing.put(taskType, new boolean[ARRAY_SIZE]);
                    return;
                }
                if (executing.get(taskType)[idx]) {
                    continue;
                }
                // 当前正在执行
                executing.get(taskType)[idx] = true;
                executor.execute(() -> {
                    try {
                        handler.accept(taskType);
                    } finally {
                        synchronized (executing) {
                            // 当前任务不在执行
                            executing.get(taskType)[idx] = false;
                        }
                    }
                });
            }
        }

    }


}
