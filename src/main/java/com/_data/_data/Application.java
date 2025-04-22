package com._data._data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com._data._data")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
