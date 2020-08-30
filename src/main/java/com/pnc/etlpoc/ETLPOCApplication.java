package com.pnc.etlpoc;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Bootstrap application.
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableSwagger2
public class ETLPOCApplication {

    public static void main(String[] args) {
        SpringApplication.run(ETLPOCApplication.class, args);
    }

}
