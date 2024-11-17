package com.zdf.worker.kafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.worker.data.AsyncTaskReturn;


import com.zdf.worker.data.TaskList;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

public class KafkaTaskFetcherManual {

    private final String bootstrapServers;
    private final String groupId;

    public KafkaTaskFetcherManual(String bootstrapServers, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
    }



    public List<AsyncTaskReturn> fetchTasks(String topic) {
        // Kafka 消费者配置
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.kafka.consumer.properties.spring.json.value.default.type", "com.zdf.flowsvr.data.ReturnStatus"); // 指定反序列化目标类
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        List<AsyncTaskReturn> taskList = new ArrayList<>();
        try {
            // 拉取消息
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                // 假设消息是 JSON 格式，解析为 AsyncTaskReturn
                taskList = parseJson(record.value());
            }
        } finally {
            consumer.close(); // 关闭消费者
        }
        return taskList;
    }

    public List<AsyncTaskReturn> parseJson(String readValue) {
        try {
            // 使用 Jackson 解析 JSON，返回 List<AsyncTaskReturn>
            ObjectMapper objectMapper = new ObjectMapper();
            // 使用泛型解析 JSON
            // 反序列化 JSON 到 ReturnStatus<TaskList>
            ReturnStatus<TaskList> returnStatus = objectMapper.readValue(readValue, objectMapper.getTypeFactory()
                    .constructParametricType(ReturnStatus.class, TaskList.class));

            // 提取 TaskList
            TaskList taskList = returnStatus.getResult();
            if (taskList != null) {
                List<AsyncTaskReturn> tasks = taskList.getTaskList();
                tasks.forEach(System.out::println); // 打印每个任务
            } else {
                System.out.println("No TaskList found in the input JSON.");
            }
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        }
        return new ArrayList<>(); // 返回空列表以避免空指针问题
    }


}
