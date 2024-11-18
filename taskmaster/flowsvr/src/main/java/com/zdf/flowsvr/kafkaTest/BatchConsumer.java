package com.zdf.flowsvr.kafkaTest;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchConsumer {

    @KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "batchFactory", autoStartup = "false")
    public void batchListen(List<String> messages, Acknowledgment acknowledgment) {
        System.out.println("BatchConsumer - Received batch: " + messages);
        // 手动提交offset
        acknowledgment.acknowledge();
    }
}
