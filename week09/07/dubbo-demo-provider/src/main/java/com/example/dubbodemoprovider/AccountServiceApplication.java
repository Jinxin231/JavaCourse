package com.example.dubbodemoprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/5-19:32
 */
@SpringBootApplication
@ImportResource({"classpath:spring-dubbo.xml"})
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AccountServiceApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
