package com.fastcampus.hellospringbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;


@EnableBatchProcessing
@SpringBootApplication
public class HelloSpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringBatchApplication.class, args);
	}

}
