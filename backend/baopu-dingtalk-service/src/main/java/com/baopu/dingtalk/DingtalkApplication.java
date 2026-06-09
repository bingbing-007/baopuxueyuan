package com.baopu.dingtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DingtalkApplication {
  public static void main(String[] args) {
    SpringApplication.run(DingtalkApplication.class, args);
  }
}
