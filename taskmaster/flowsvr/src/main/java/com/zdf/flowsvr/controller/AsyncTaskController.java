package com.zdf.flowsvr.controller;

import com.zdf.flowsvr.constant.ErrorStatusReturn;
import com.zdf.flowsvr.data.AsyncTaskRequest;
import com.zdf.flowsvr.data.AsyncTaskSetRequest;
import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.flowsvr.service.AsyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.zdf.flowsvr.util.Utils.isStrNull;

@RestController
@RequestMapping("/task")
public class AsyncTaskController {
    @Autowired
    private AsyncTaskService asyncTaskService;

    Logger logger = LoggerFactory.getLogger(AsyncTaskController.class);

    @PostMapping("/create_task")
    public ReturnStatus createTask(@RequestBody AsyncTaskRequest asyncTaskGroup) {
        if (isStrNull(asyncTaskGroup.getTaskData().getTask_type())){
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.createTask(asyncTaskGroup);
    }

    @GetMapping("/get_task")
    public ReturnStatus getTask(@RequestParam("task_id") String task_id) {
        if (isStrNull(task_id)){
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.getTask(task_id);
    }

    @GetMapping("/task_list")
    public ReturnStatus getTaskList(@RequestParam("task_type") String taskType, @RequestParam("status") int status, @RequestParam("limit") int limit) {
        if (isStrNull(taskType) || !ErrorStatusReturn.IsValidStatus(status)) {
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.getTaskList(taskType, status, limit);
    }

    @GetMapping("/hold_task")
    public ReturnStatus holdTask(@RequestParam("task_type") String taskType, @RequestParam("status") int status, @RequestParam("limit") int limit) {
        if (isStrNull(taskType) || !ErrorStatusReturn.IsValidStatus(status)) {
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.holdTask(taskType, status, limit);
    }



    @PostMapping("/set_task")
    public ReturnStatus addTask(@RequestBody AsyncTaskSetRequest asyncTaskSetRequest) {
        if (isStrNull(asyncTaskSetRequest.getTask_id())) {
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.setTask(asyncTaskSetRequest);
    }

    @GetMapping("/user_task_list")
    public ReturnStatus getUserTaskList(@RequestParam("user_id") String user_id, @RequestParam("status_list") int statusList) {
        if (isStrNull(user_id)) {
            logger.error("input invalid");
            return ErrorStatusReturn.ERR_INPUT_INVALID;
        }
        return asyncTaskService.getTaskByUserIdAndStatus(user_id, statusList);
    }



}
