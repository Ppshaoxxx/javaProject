package org.originit.async.deal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.originit.async.deal.enums.TaskStatus;
import org.originit.async.deal.mapper.SchedulePosMapper;
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

/**
 * @author pshao
 */
@Service
@Slf4j
public class SchedulePosServiceImpl implements SchedulePosService {

    @Autowired
    SchedulePosMapper schedulePosMapper;

    @Autowired
    @Lazy
    SchedulePosService schedulePosService;

    @Autowired
    @Lazy
    ScheduleConfigService scheduleConfigService;

    @Autowired
    @Lazy
    TaskService taskService;

    @Value("${deal.tasks.table-limit:5000000}")
    Integer tableLimit;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Integer getBeginTablePos(String taskType) {
        return schedulePosMapper.selectBeginTableByType(taskType);
    }

//    @Override
//    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
//    public Integer getEndTablePos(String taskType) {
//        return schedulePosMapper.selectEndTablePos(taskType);
//    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handleTaskTableFull(String taskType) {
        final Integer endTablePos = schedulePosMapper.selectEndTablePos(taskType);
        final String tableName = Task.acquireTableName(taskType, endTablePos);
        // 表的最大容量
        final Integer taskTypeMaxLimit = tableLimit;
        final Integer allCount = taskService.getAllCount(tableName);
        if (allCount >= taskTypeMaxLimit) {
            final int newEndPos = endTablePos + 1;
            // 创建新的表
            final String newTableName = Task.acquireTableName(taskType, newEndPos);
            // 这里会提交前面的事务，但是没关系，我们最后创建一下表之后再更新一下结束位置
            taskService.createNewTaskTable(newTableName);
            // 如果走到这肯定创建了表，可以更新任务的结束位置,如果下面失败了，那么下一次也可以维护这个
            schedulePosMapper.updateEndPos(taskType, newEndPos);
            log.info("当前表{}的记录数达到{}条，超过限制{}，创建新表{}",tableName, allCount,taskTypeMaxLimit, newTableName);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void handleTaskTableEmpty(String taskType) {
        final Integer beginTablePos = schedulePosMapper.selectBeginTableByType(taskType);
        final Integer endTablePos = schedulePosMapper.selectEndTablePos(taskType);
        // 如果没法往后滚动就不管了
        if (beginTablePos.equals(endTablePos)) {
            return;
        }
        final String tableName = Task.acquireTableName(taskType, beginTablePos);
        int leastCount = taskService.getCountByStatus(taskType, TaskStatus.EXECUTING.getStatus()) +
                taskService.getCountByStatus(taskType, TaskStatus.PENDING.getStatus());
        // 如果当前没有了等待的任务和执行中的任务，那么就说明已经没有任务了
        if (leastCount == 0) {
            // 向后滚动
            schedulePosMapper.updateBeginPos(taskType, beginTablePos + 1);
            log.info("表{}已经没有可执行的任务，向后滚动,pos={}",tableName, beginTablePos + 1);
        }
    }


}
