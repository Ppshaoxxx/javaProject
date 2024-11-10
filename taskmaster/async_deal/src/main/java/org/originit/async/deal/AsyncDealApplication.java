package org.originit.async.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author pshao
 */
@SpringBootApplication
public class AsyncDealApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncDealApplication.class, args);
    }



}
