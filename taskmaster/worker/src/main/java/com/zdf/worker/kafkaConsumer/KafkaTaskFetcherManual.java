package com.zdf.worker.kafkaConsumer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdf.worker.data.ReturnStatus;
import com.zdf.worker.data.TaskList;
import com.zdf.worker.data.AsyncTaskReturn;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

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
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");// 指定反序列化目标类
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 创建消费者
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        List<AsyncTaskReturn> taskList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 拉取消息
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                String message = record.value();
                ReturnStatus<TaskList> returnStatus = objectMapper.readValue(
                        message,
                        new TypeReference<ReturnStatus<TaskList>>() {}
                );


                taskList = returnStatus.getResult().getTaskList();
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close(); // 关闭消费者
        }
        return taskList;
    }



}
