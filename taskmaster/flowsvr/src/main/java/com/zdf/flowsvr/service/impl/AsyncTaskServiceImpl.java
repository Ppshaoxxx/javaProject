package com.zdf.flowsvr.service.impl;

import com.zdf.flowsvr.constant.ErrorStatusReturn;
import com.zdf.flowsvr.constant.Task;
import com.zdf.flowsvr.dao.AsyncFlowTaskDao;
import com.zdf.flowsvr.dao.ScheduleConfigDao;
import com.zdf.flowsvr.dao.TSchedulePosDao;
import com.zdf.flowsvr.data.*;
import com.zdf.flowsvr.enums.ErrorStatus;
import com.zdf.flowsvr.enums.TaskStatus;
import com.zdf.flowsvr.service.AsyncTaskService;
import com.zdf.flowsvr.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author zhangdafeng
 */
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {
    Logger logger = LoggerFactory.getLogger(AsyncTaskServiceImpl.class);

    @Autowired
    private AsyncFlowTaskDao asyncFlowTaskDao;

    @Autowired
    private ScheduleConfigDao scheduleConfigDao;

    @Autowired
    private TSchedulePosDao tSchedulePosDao;


    private AsyncFlowClientData getAsyncFlowClientData(AsyncTaskRequest asyncTaskGroup) {
        AsyncFlowClientData asyncFlowClientData = asyncTaskGroup.getTaskData();
        return asyncFlowClientData;
    }

    @Override
    public <T> ReturnStatus<T> createTask(AsyncTaskRequest asyncTaskRequest) {
        AsyncFlowClientData asyncFlowClientData = getAsyncFlowClientData(asyncTaskRequest);
        TSchedulePos taskPos = null;
        try {
            taskPos = tSchedulePosDao.getTaskPos(asyncFlowClientData.getTask_type());
        } catch (Exception e) {
            return ErrorStatusReturn.ERR_GET_TASK_POS;
        }
        if (taskPos == null) {
            logger.error("db.TaskPosNsp.GetTaskPos failed.");
        }
        String tableName = getTableName(taskPos.getScheduleEndPos(), asyncFlowClientData.getTask_type());

        ScheduleConfig taskTypeCfg;
        try {
            taskTypeCfg = scheduleConfigDao.getTaskTypeCfg(asyncFlowClientData.getTask_type());
        } catch (Exception e) {
            logger.error("Visit t_task_type_cfg error");
            return ErrorStatusReturn.ERR_GET_TASK_SET_POS_FROM_DB;
        }

        AsyncFlowTask asyncFlowTask = new AsyncFlowTask();
        String taskId = getTaskId(asyncFlowClientData.getTask_type(), taskPos.getScheduleEndPos(), tableName);
        try {
            fillTaskModel(asyncFlowClientData, asyncFlowTask, taskId, taskTypeCfg);
            asyncFlowTaskDao.create(tableName, asyncFlowTask);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("create task error");
            return ErrorStatusReturn.ERR_CREATE_TASK;

        }
        TaskResult taskResult = new TaskResult(taskId);
        return new ReturnStatus(taskResult);
    }

    private String getTaskId(String taskType, int taskPos, String tableName) {
        return Utils.getTaskId() + "_" + taskType + "_" + tableName() + "_" + taskPos;
    }

    public void fillTaskModel (AsyncFlowClientData asyncFlowClientData, AsyncFlowTask asyncFlowTask, String taskId, ScheduleConfig taskTypeCfg) {
        asyncFlowTask.setTask_id(taskId);
        asyncFlowTask.setUser_id(asyncFlowClientData.getUser_id());
        asyncFlowTask.setTask_type(asyncFlowClientData.getTask_type());
        asyncFlowTask.setTask_stage(asyncFlowClientData.getTask_stage());
        Long currentTime = System.currentTimeMillis();
        asyncFlowTask.setModify_time(currentTime);
        asyncFlowTask.setMax_retry_interval(taskTypeCfg.getRetry_interval());
        asyncFlowTask.setMax_retry_num(taskTypeCfg.getMax_retry_num());
        asyncFlowTask.setCrt_retry_num(0);
        asyncFlowTask.setOrder_time(currentTime);
        asyncFlowTask.setCreate_time(currentTime);
        asyncFlowTask.setStatus(TaskStatus.PENDING.getStatus());
        asyncFlowTask.setSchedule_log(asyncFlowClientData.getSchedule_log());
        asyncFlowTask.setTask_context(asyncFlowClientData.getTask_context());
    }

    @Override
    public <T> ReturnStatus<T> holdTask(String taskType, int status, int limit) {
        if (limit > Task.MAX_TASK_LIST_LIMIT) {
            limit = Task.MAX_TASK_LIST_LIMIT;
        }
        if (limit == 0) {
            limit = Task.DEFAULT_TASK_LIST_LIMIT;
        }
        TSchedulePos taskPos;
        try {
            taskPos = tSchedulePosDao.getTaskPos(taskType);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorStatusReturn.ERR_GET_TASK_SET_POS_FROM_DB;
        }
        String tableName = getTableName(taskPos.getScheduleBeginPos(), taskType);
        List<AsyncFlowTask> taskList;
        try {
            taskList = asyncFlowTaskDao.getTaskList(taskType, status, limit, tableName);

        } catch (Exception e) {
            logger.error(ErrorStatus.ERR_GET_TASK_LIST_FROM_DB.getMsg());
            return ErrorStatusReturn.ERR_GET_TASK_LIST_FROM_DB;
        }
        List<AsyncFlowTask> filterList = taskList
                .stream()
                .parallel()
                .filter(asyncFlowTask -> asyncFlowTask.getCrt_retry_num() == 0 || asyncFlowTask.getMax_retry_interval() != 0
                        && asyncFlowTask.getOrder_time() <= System.currentTimeMillis()).collect(Collectors.toList());
        List<String> ids = conventTaskIdList(filterList);
        if (!ids.isEmpty()) {
            asyncFlowTaskDao.updateStatusBatch(ids, TaskStatus.EXECUTING.getStatus(), System.currentTimeMillis(), tableName);
        }
        List<AsyncTaskReturn> taskReturns = getTaskReturnList(filterList);
        TaskList list = new TaskList(taskReturns);
        return new ReturnStatus(list);
    }

    @Override
    public <T> ReturnStatus<T> getTaskList(String taskType, int status, int limit) {
        if (limit > Task.MAX_TASK_LIST_LIMIT) {
            limit = Task.MAX_TASK_LIST_LIMIT;
        }
        if (limit == 0) {
            limit = Task.DEFAULT_TASK_LIST_LIMIT;
        }
        TSchedulePos taskPos;
        try {
            taskPos = tSchedulePosDao.getTaskPos(taskType);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorStatusReturn.ERR_GET_TASK_SET_POS_FROM_DB;
        }
        String tableName = getTableName(taskPos.getScheduleBeginPos(), taskType);
        List<AsyncFlowTask> taskList;
        try {
             taskList = asyncFlowTaskDao.getTaskList(taskType, status, limit, tableName);

        } catch (Exception e) {
            logger.error(ErrorStatus.ERR_GET_TASK_LIST_FROM_DB.getMsg());
            return ErrorStatusReturn.ERR_GET_TASK_LIST_FROM_DB;
        }
        List<AsyncTaskReturn> taskReturns = getTaskReturns(taskList);
        TaskList list = new TaskList(taskReturns);
        return new ReturnStatus(list);
    }

    private List<AsyncTaskReturn> getTaskReturns(List<AsyncFlowTask> taskList) {
        List<AsyncTaskReturn> taskReturns = new ArrayList<>();
        for (AsyncFlowTask asyncFlowTask : taskList) {
            taskReturns.add(getTaskReturn(asyncFlowTask));
        }
        return taskReturns;
    }

    private AsyncTaskReturn getTaskReturn(AsyncFlowTask asyncFlowTask) {
        AsyncTaskReturn tr = new AsyncTaskReturn(
                asyncFlowTask.getUser_id(),
                asyncFlowTask.getTask_id(),
                asyncFlowTask.getTask_type(),
                asyncFlowTask.getTask_stage(),
                asyncFlowTask.getStatus(),
                asyncFlowTask.getCrt_retry_num(),
                asyncFlowTask.getMax_retry_num(),
                asyncFlowTask.getMax_retry_interval(),
                asyncFlowTask.getSchedule_log(),
                asyncFlowTask.getTask_context(),
                asyncFlowTask.getCreate_time(),
                asyncFlowTask.getModify_time()
        );
        return tr;


    }

    @Override
    public <T> ReturnStatus<T> setTask(AsyncTaskSetRequest asyncTaskSetRequest) {
        AsyncFlowTask asyncFlowTask;
        String tableName = getTableNameById(asyncTaskSetRequest.getTask_id());
        try {
            asyncFlowTask = asyncFlowTaskDao.find(asyncTaskSetRequest.getTask_id(), tableName);
        } catch (Exception e) {
            logger.error(ErrorStatus.ERR_GET_TASK_INFO.getMsg());
            return ErrorStatusReturn.ERR_GET_TASK_INFO;
        }

        if (asyncFlowTask == null) {
            logger.error("db.TaskPosNsp.Find Task failed. TaskId:%s", asyncTaskSetRequest.getTask_id());
            return ErrorStatusReturn.ERR_GET_TASK_INFO;
        }
        if (!isUnUpdate(asyncTaskSetRequest.getStatus())) {
            asyncFlowTask.setStatus(asyncTaskSetRequest.getStatus());
        }
        if (!isNullString(asyncTaskSetRequest.getTask_stage())) {
            asyncFlowTask.setTask_stage(asyncTaskSetRequest.getTask_stage());
        }
        if (!isNullString(asyncTaskSetRequest.getTask_context())) {
            asyncFlowTask.setTask_context(asyncTaskSetRequest.getTask_context());
        }
        if (!isNullString(asyncTaskSetRequest.getSchedule_log())) {
            asyncFlowTask.setSchedule_log(asyncTaskSetRequest.getSchedule_log());
        }
        if (!isUnUpdate(asyncTaskSetRequest.getCrt_retry_num())) {
            asyncFlowTask.setCrt_retry_num(asyncTaskSetRequest.getCrt_retry_num());
        }
        if (!isUnUpdate(asyncTaskSetRequest.getMax_retry_interval())) {
            asyncFlowTask.setMax_retry_interval(asyncTaskSetRequest.getMax_retry_interval());
        }
        if (!isUnUpdate(asyncTaskSetRequest.getMax_retry_num())) {
            asyncFlowTask.setMax_retry_num(asyncTaskSetRequest.getMax_retry_num());
        }
        if (asyncTaskSetRequest.getOrder_time() != 0) {
            asyncFlowTask.setOrder_time(asyncTaskSetRequest.getOrder_time());
        }
        if (!isUnUpdate(asyncTaskSetRequest.getPriority())) {
            asyncFlowTask.setPriority(asyncTaskSetRequest.getPriority());
        }

        asyncFlowTask.setModify_time(System.currentTimeMillis());
        try {
            List<Integer> list = new ArrayList<Integer>() {{
                add(TaskStatus.SUCCESS.getStatus());
                add(TaskStatus.FAIL.getStatus());
            }};
            asyncFlowTaskDao.updateTask(asyncFlowTask, list, tableName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ErrorStatus.ERR_SET_TASK.getMsg());
            return ErrorStatusReturn.ERR_SET_TASK;
        }
        return ErrorStatusReturn.SUCCESS;
    }

    private String getTableNameById(String taskId) {
        String[] strs = taskId.split("_");
        String tableName = getTableName(Integer.parseInt(strs[3]), strs[1]);
        return tableName;
    }

    private boolean isUnUpdate(int x) {
        return x == Task.DEFAULT_SET_TASK_STATUS;
    }

    private boolean isNullString(String s) {
        return s.equals(Task.DEFAULT_SET_TASK_STAGE_SCHEDULELOG_CONTEXT);
    }

    @Override
    public <T> ReturnStatus<T> getTask(String task_id) {
        AsyncFlowTask asyncFlowTask;
        String tableName = getTableNameById(task_id);
        try {
            asyncFlowTask = asyncFlowTaskDao.find(task_id, tableName);
        } catch (Exception e) {
            logger.error("get task info error");
            return ErrorStatusReturn.ERR_GET_TASK_INFO;
        }
        TaskByTaskIdReturn<AsyncTaskReturn> taskByTaskIdReturn = new TaskByTaskIdReturn(getTaskReturn(asyncFlowTask));
        return new ReturnStatus(taskByTaskIdReturn);
    }

    @Override
    public <T> ReturnStatus<T> getTaskByUserIdAndStatus(String user_id, int statusList) {

        List<AsyncFlowTask> asyncFlowTaskList;
        String tableName = getTableName(1, "LarkTask");
        try {
            asyncFlowTaskList = asyncFlowTaskDao.getTaskByUser_idAndStatus(user_id, getStatusList(statusList), tableName);
        } catch (Exception e) {
            logger.error("get task info error");
            return ErrorStatusReturn.ERR_GET_TASK_INFO;
        }
        List<AsyncTaskReturn> taskReturns = getTaskReturns(asyncFlowTaskList);
        TaskList list = new TaskList(taskReturns);
        return new ReturnStatus(list);
    }



    private List<Integer> getStatusList(int status) {
        List<Integer> statusList = new ArrayList<>();
        while (status != 0) {
            int cur = status & -status;
            statusList.add(cur);
            status ^= cur;
        }
        return statusList;
    }


    private List<AsyncTaskReturn> getAsyncTaskReturns(List<AsyncFlowTask> taskList) {
        return getTaskReturnList(taskList);
    }

    private List<AsyncTaskReturn> getTaskReturnList(List<AsyncFlowTask> taskList) {
        List<AsyncTaskReturn> tasks = new ArrayList<>();
        for (AsyncFlowTask asyncFlowTask : taskList) {
            AsyncTaskReturn asyncTaskReturn = new AsyncTaskReturn(
                    asyncFlowTask.getUser_id(),
                    asyncFlowTask.getTask_id(),
                    asyncFlowTask.getTask_type(),
                    asyncFlowTask.getTask_stage(),
                    asyncFlowTask.getStatus(),
                    asyncFlowTask.getCrt_retry_num(),
                    asyncFlowTask.getMax_retry_num(),
                    asyncFlowTask.getMax_retry_interval(),
                    asyncFlowTask.getSchedule_log(),
                    asyncFlowTask.getTask_context(),
                    asyncFlowTask.getCreate_time(),
                    asyncFlowTask.getModify_time()
            );
            tasks.add(asyncTaskReturn);
        }
        return tasks;
    }

    public List<String> conventTaskIdList(List<AsyncFlowTask> list) {
        return list.stream().map(AsyncFlowTask::getId).collect(Collectors.toList());
    }

    public int getTaskCountByStatus(TaskStatus taskStatus) {
        String tableName = getTableName(1, "LarkTask");
        return asyncFlowTaskDao.getTaskCountByStatus(taskStatus.getStatus(), tableName);
    }

    public int getAliveTaskCount() {
        String tableName = getTableName(1, "LarkTask");
        return asyncFlowTaskDao.getTaskCount(this.getAliveStatus(), tableName);
    }

    public int getAllTaskCount() {
        String tableName = getTableName(1, "LarkTask");
        return asyncFlowTaskDao.getTaskCount(this.getAllStatus(), tableName);
    }

    public List<AsyncFlowTask> getAliveTaskList() {
        String tableName = getTableName(1, "LarkTask");
        return asyncFlowTaskDao.getAliveTaskList(this.getAliveStatus(), tableName);
    }

    public List<Integer> getAliveStatus() {
        return new LinkedList<Integer>() {{
            add(TaskStatus.PENDING.getStatus());
            add(TaskStatus.EXECUTING.getStatus());
        }};
    }
    public List<Integer> getAllStatus() {
        return new LinkedList<Integer>() {{
            add(TaskStatus.PENDING.getStatus());
            add(TaskStatus.EXECUTING.getStatus());
            add(TaskStatus.SUCCESS.getStatus());
            add(TaskStatus.FAIL.getStatus());
        }};
    }

    public String getTableName(int pos, String taskType) {
        return "t_" + taskType.toLowerCase() + "_" + this.tableName() + "_" + pos;
    }

    public String tableName() {
        return "task";
    }
}
