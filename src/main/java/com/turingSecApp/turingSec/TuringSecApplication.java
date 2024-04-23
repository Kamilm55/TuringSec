package com.turingSecApp.turingSec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
public class TuringSecApplication {
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

}
