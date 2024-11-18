package com.zdf.flowsvr.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.flowsvr.data.TaskList;
import com.zdf.flowsvr.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskRunner {

    private static final String taskType = "Lark";
    private static final int N = 5; // 每分钟拉取的次数
    private static final long INTERVAL_BETWEEN_CALLS = 10000; // 每次调用间隔（毫秒），这里是 10 秒

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AsyncTaskService asyncTaskService; // 注入服务

    @Autowired
    public ScheduledTaskRunner(AsyncTaskService asyncTaskService, KafkaTemplate<String, String> kafkaTemplate) {
        this.asyncTaskService = asyncTaskService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000) // 每隔 1 分钟执行一次
    public void executeTask() {
        System.out.println("Starting scheduled task...");

        new Thread(() -> {
            for (int i = 0; i < N; i++) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    // 调用 holdTask 方法，直接获取返回值
                    ReturnStatus<TaskList> response = asyncTaskService.holdTask(taskType, 1, 10);
                    // Serialize the object into JSON string
                    String message = objectMapper.writeValueAsString(response);
                    // 将结果发送到 Kafka
                    if (!response.getResult().getTaskList().isEmpty()) {
                        kafkaTemplate.send(taskType, message);
                        System.out.println("Task result sent to Kafka: " + response);
                    } else {
                        System.out.println("No response from holdTask method.");
                    }

                    // 每次调用间隔
                    Thread.sleep(INTERVAL_BETWEEN_CALLS); // 等待指定间隔时间
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}