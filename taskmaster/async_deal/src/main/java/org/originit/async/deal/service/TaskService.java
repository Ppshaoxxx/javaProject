package org.originit.async.deal.service;


/**
 * 任务服务
 * @author pshao
 */
public interface TaskService {

    /**
     * 治理特定类型的超时任务
     * @param taskType 当前任务类型
     */
    void dealTimeoutTasksByType(String taskType);

    /**
     * 获取表当前的总数量
     * @param tableName 表名
     * @return 记录总数量
     */
    Integer getAllCount(String tableName);

    /**
     * 创建新表
     * @param newTableName 表名
     * @return 是否创建成功
     */
    boolean createNewTaskTable(String newTableName);

    /**
     * 获取指定状态任务的记录
     * @param taskType 任务类型
     * @param status 状态
     * @return
     */
    int getCountByStatus(String taskType, int status);
}
