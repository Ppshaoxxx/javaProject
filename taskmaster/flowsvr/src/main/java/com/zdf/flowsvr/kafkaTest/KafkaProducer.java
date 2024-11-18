package com.zdf.flowsvr.kafkaTest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

//@SpringBootApplication
public class KafkaProducer {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducer.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
        return args -> {
            String topic = "test-topic";
            for (int i = 1; i <= 1; i++) {
                String message = "ReturnStatus(msg=ok, code=0, result=TaskList(taskList=[AsyncTaskReturn(user_id=yy2, task_id=248155516985933824_lark_task_1, task_type=lark, task_stage=sendmsg, status=1, crt_retry_num=0, max_retry_num=3, max_retry_interval=10, schedule_log=, task_context={\"ReqBody\":{\"Msg\":\"nice to meet u\",\"FromAddr\":\"fish\",\"ToAddr\":\"cat\"},\"UserId\":\"\"}, create_time=1731667285756, modify_time=1731871001134), AsyncTaskReturn(user_id=yy2, task_id=248160725061599232_lark_task_1, task_type=lark, task_stage=sendmsg, st\"[truncated 2936 chars]; line: 1, column: 13]";
//                String message = "Message " + i;
                kafkaTemplate.send(topic, message);
                System.out.println("Sent: " + message);
                Thread.sleep(500); // 模拟消息发送间隔
            }
        };
    }
}
