package org.springcloud.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class SpringcloudRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudRegisterApplication.class, args);
    }

}
