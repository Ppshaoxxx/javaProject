package org.originit.async.deal.service;

/**
 * @author pshao
 */
public interface SchedulePosService {
    /**
     * 获取该任务类型当前开始表的名字，也就是任务读取哪个表
     * @param taskType 任务类型
     * @return 任务分表的索引(eg: 1,2,3)
     */
    Integer getBeginTablePos(String taskType);

//    /**
//     * 获取该任务类型当前结束表的名字，也就是任务写入哪个表
//     * @param taskType 任务类型
//     * @return 任务分表的索引(eg: 1,2,3)
//     */
//    Integer getEndTablePos(String taskType);

    /**
     * 处理当前任务写入表满的情况，创建新表并修改结束位置
     * @param taskType 任务类型
     */
    void handleTaskTableFull(String taskType);

    /**
     * 处理当前任务表没有可以执行的任务时滚动到下一个表
     * @param taskType 任务类型
     */
    void handleTaskTableEmpty(String taskType);
}
