
package com.market.alphavantage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class AlphavantageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlphavantageApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner logMappings(ApplicationContext ctx) {
        return args -> Arrays.stream(ctx.getBeanDefinitionNames())
                .sorted()
                .forEach(System.out::println);
    }*/
}

