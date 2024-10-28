package com.zdf.flowsvr.service;


import com.zdf.flowsvr.data.AsyncTaskRequest;
import com.zdf.flowsvr.data.AsyncTaskSetRequest;
import com.zdf.flowsvr.data.ReturnStatus;

public interface AsyncTaskService {
    /**
     * 创建任务
     * @param asyncTaskRequest
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> createTask(AsyncTaskRequest asyncTaskRequest);

    /**
     * 获取任务列表
     * @param taskType
     * @param status
     * @param limit
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> getTaskList(String taskType, int status, int limit);

    /**
     * 更改任务信息
     * @param asyncTaskSetRequest
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> setTask(AsyncTaskSetRequest asyncTaskSetRequest);

    /**
     * 获取任务
     * @param task_id
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> getTask(String task_id);

    /**
     * 获取指定用户的任务列表
     * @param user_id
     * @param statusList
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> getTaskByUserIdAndStatus(String user_id, int statusList);


    /**
     * 占据任务
     * @param taskType
     * @param status
     * @param limit
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> holdTask(String taskType, int status, int limit);
}
