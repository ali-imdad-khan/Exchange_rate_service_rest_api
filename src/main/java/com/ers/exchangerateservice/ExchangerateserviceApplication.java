package com.ers.exchangerateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExchangerateserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangerateserviceApplication.class, args);
	}

}
