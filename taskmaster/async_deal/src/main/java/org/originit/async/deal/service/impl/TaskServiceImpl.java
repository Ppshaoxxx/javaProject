package org.originit.async.deal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.originit.async.deal.enums.TaskStatus;
import org.originit.async.deal.mapper.TaskMapper;
import org.originit.async.deal.pojo.Task;
import org.originit.async.deal.service.ScheduleConfigService;
import org.originit.async.deal.service.SchedulePosService;
import org.originit.async.deal.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pshao
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    @Lazy
    ScheduleConfigService scheduleConfigService;

    @Autowired
    @Lazy
    SchedulePosService schedulePosService;

    @Autowired
    TaskMapper taskMapper;

    @Value("${deal.timeout-tasks.limit-count:-1}")
    Integer timeoutLimitCount;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void dealTimeoutTasksByType(String taskType) {
        if (taskType == null) {
            throw new IllegalArgumentException("任务类型不能为空");
        }
        // 1. 获取任务的最大执行时间
        final Integer limitTime = scheduleConfigService.getTaskTypeMaxLimit(taskType);
        if (limitTime == null) {
            throw new IllegalArgumentException("任务类型\"" + taskType+ "\"不存在");
        }
        // 2. 获取当前开始表位置
        final Integer tableIndex = schedulePosService.getBeginTablePos(taskType);
        // 3. 拼接表名
        final String tableName = Task.acquireTableName(taskType, tableIndex);
        // 4. 查询超时任务
        List<Task> timeoutTasks = taskMapper.selectTimeoutTasks(TaskStatus.EXECUTING.getStatus(), tableName, System.currentTimeMillis() - limitTime.longValue() * 1000, timeoutLimitCount);
        if (timeoutTasks.isEmpty()) {
            return;
        }
        // 5. 更新任务的状态和重试次数以及重试时间
        timeoutTasks = timeoutTasks.stream().map(this::timeoutTask2UpdateTask).collect(Collectors.toList());
        // 6. 批量将任务的状态进行更新，包括失败的任务和重试的任务
        taskMapper.batchUpdateRetryTask(tableName,TaskStatus.FAIL.getStatus(),TaskStatus.EXECUTING.getStatus(), timeoutTasks);
        log.info("处理{}条超时任务",timeoutTasks.size());
    }

    @Override
    public Integer getAllCount(String tableName) {
        return taskMapper.selectCount(tableName);
    }

    @Override
    public boolean createNewTaskTable(String newTableName) {
        return taskMapper.createNewTable(newTableName) == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
    public int getCountByStatus(String taskType, int status) {
        final Integer beginTablePos = schedulePosService.getBeginTablePos(taskType);
        String tableName = Task.acquireTableName(taskType, beginTablePos);
        return taskMapper.selectCountByStatus(tableName, status);
    }

    private Task timeoutTask2UpdateTask(Task task) {
        Long now = System.currentTimeMillis();
        if (task.getCrtRetryNum().equals(task.getMaxRetryNum())) {
            task.setStatus(TaskStatus.FAIL.getStatus());
            task.setModifyTime(now);
        } else {
            task.setStatus(TaskStatus.PENDING.getStatus());
            task.setCrtRetryNum(task.getCrtRetryNum() + 1);
            task.setOrderTime(Task.calcOrderTime(now, task.getMaxRetryInterval(), task.getCrtRetryNum()));
            task.setModifyTime(now);
        }
        return task;
    }

}
