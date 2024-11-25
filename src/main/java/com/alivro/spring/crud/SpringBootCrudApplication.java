package com.alivro.spring.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.alivro.spring.crud"})
public class SpringBootCrudApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootCrudApplication.class, args);
	}
}
