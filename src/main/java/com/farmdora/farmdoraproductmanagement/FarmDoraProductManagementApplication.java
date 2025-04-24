package com.farmdora.farmdoraproductmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:${user.home}/config/farm-dora.properties")
public class FarmDoraProductManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmDoraProductManagementApplication.class, args);
    }

}
