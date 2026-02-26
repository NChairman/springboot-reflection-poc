package com.poc.reflection.springboot.demo;

import org.springframework.boot.SpringApplication;

public class TestSpringbootReflectionPocApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringbootReflectionPocApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
