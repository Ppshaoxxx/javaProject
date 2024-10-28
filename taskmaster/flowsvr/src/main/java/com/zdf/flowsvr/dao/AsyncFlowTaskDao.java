package com.zdf.flowsvr.dao;

import com.zdf.flowsvr.data.AsyncFlowTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AsyncFlowTaskDao {
    /**
     * 创建任务
     *
     * @param asyncFlowTask
     * @param tableName
     */
    void create(@Param("tableName") String tableName, @Param("asyncFlowTask") AsyncFlowTask asyncFlowTask);

    /**
     * 新增或更新任务
     *
     * @param asyncFlowTask
     * @param tableName
     */
    void save(@Param("asyncFlowTask") AsyncFlowTask asyncFlowTask, @Param("tableName") String tableName);

    /**
     * 获得对应状态的对应任务列表
     *
     * @param taskType  任务类型
     * @param status    任务状态
     * @param limit     限制数目
     * @param tableName
     * @return
     */
    List<AsyncFlowTask> getTaskList(@Param("taskType") String taskType, @Param("status") int status,
                                    @Param("limit") int limit, @Param("tableName") String tableName);

    /**
     * 更新任务信息
     *
     * @param asyncFlowTask
     * @param statuss
     * @param tableName
     */
    void updateTask(@Param("asyncFlowTask") AsyncFlowTask asyncFlowTask,
                    @Param("statuss") List<Integer> statuss, @Param("tableName") String tableName);

    /**
     * 获得活跃状态的任务
     *
     * @param statusList 活跃状态列表
     * @param tableName
     * @return
     */
    List<AsyncFlowTask> getAliveTaskList(@Param("statusList") List<Integer> statusList,
                                         @Param("tableName") String tableName);

    /**
     * 获取对应状态的任务数
     *
     * @param status 任务状态
     *               ≈
     * @return
     */
    int getTaskCountByStatus(@Param("status") int status, @Param("tableName") String tableName);

    /**
     * 获取任务状态列表中的任务数
     *
     * @param statusList
     * @param tableName
     * @return
     */
    int getTaskCount(@Param("statusList") List<Integer> statusList, @Param("tableName") String tableName);

    /**
     * 获取处于执行状态的超过最大执行时间的任务列表
     *
     * @param status         任务状态
     * @param limit          限制数目
     * @param maxProcessTime 任务最大执行时间
     * @param currentTime    当前时间
     * @param tableName
     * @return
     */
    List<AsyncFlowTask> getLongTimeProcessing(@Param("status") int status, @Param("limit") int limit,
                                              @Param("maxProcessTime") long maxProcessTime,
                                              @Param("currentTime") long currentTime,
                                              @Param("tableName") String tableName);

    /**
     * 增加重试次数
     *
     * @param taskId
     * @param tableName
     */
    void increaseCrtRetryNum(@Param("taskId") String taskId, @Param("tableName") String tableName);

    /**
     * 根据任务查找任务
     *
     * @param task_id
     * @param tableName
     * @return
     */
    AsyncFlowTask find(@Param("task_id") String task_id, @Param("tableName") String tableName);

    /**
     * 设置任务状态
     *
     * @param task_id
     * @param tableName
     */
    void setStatus(@Param("task_id") String task_id, @Param("tableName") String tableName);

    /**
     * 更改任务上下文
     *
     * @param task_id
     * @param tableName
     */
    void updateTask_contextByTask_id(@Param("task_id") String task_id, @Param("tableName") String tableName);

    /**
     * 更改超时的任务为Pending状态
     *
     * @param currentTime
     * @param maxProcessingTime
     * @param oldStatus
     * @param newStatus
     * @param tableName
     */
    void modifyTimeoutPending(@Param("currentTime") Long currentTime, @Param("maxProcessingTime") Long maxProcessingTime,
                              @Param("oldStatus") int oldStatus,
                              @Param("newStatus") int newStatus, @Param("tableName") String tableName);

    /**
     * 查看指定用户的任务
     *
     * @param user_id
     * @param statusList
     * @param tableName
     * @return
     */
    List<AsyncFlowTask> getTaskByUser_idAndStatus(@Param("user_id") String user_id, @Param("statusList") List<Integer> statusList,
                                                  @Param("tableName") String tableName);

    /**
     * 将列表中的任务修改为指定状态
     *
     * @param ids
     * @param status
     * @param tableName
     * @return
     */
    int updateStatusBatch(@Param("ids") List<String> ids, @Param("status") int status,
                          @Param("modifyTime") long modifyTime, @Param("tableName") String tableName);
}
