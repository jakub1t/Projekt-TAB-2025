package com.polsl.firmakurierska;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.polsl.firmakurierska")
@EnableJpaRepositories("com.polsl.firmakurierska.repository")
@EntityScan("com.polsl.firmakurierska.model")
@ComponentScan({"com.polsl.firmakurierska.repository","com.polsl.firmakurierska.controller","com.polsl.firmakurierska.exception"})
public class FirmaKurierskaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirmaKurierskaApplication.class, args);
	}

}
