package com.zdf.flowsvr.kafkaTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.flowsvr.data.TaskList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class SingleConsumer {
    @KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "singleFactory", autoStartup = "true")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) throws JsonProcessingException {
        System.out.println("SingleConsumer - Received: " + record.value());
        ObjectMapper objectMapper = new ObjectMapper();
        // 使用泛型解析 JSON
        // 反序列化 JSON 到 ReturnStatus<TaskList>
        ReturnStatus<TaskList> returnStatus = objectMapper.readValue(record.value(), objectMapper.getTypeFactory()
                .constructParametricType(ReturnStatus.class, TaskList.class));
        System.out.println(returnStatus);
        // 手动提交offset
        acknowledgment.acknowledge();
    }
}
