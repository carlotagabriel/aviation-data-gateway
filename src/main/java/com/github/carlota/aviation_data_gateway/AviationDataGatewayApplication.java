package com.github.carlota.aviation_data_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AviationDataGatewayApplication {

    public static void main(String[] args) {
        log.info("Starting Aviation Data Gateway application");
        SpringApplication.run(AviationDataGatewayApplication.class, args);
        log.info("Aviation Data Gateway application started");
    }

}
