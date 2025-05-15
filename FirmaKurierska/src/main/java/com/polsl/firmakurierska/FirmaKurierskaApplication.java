package com.polsl.firmakurierska;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import com.polsl.firmakurierska.view.hello_world.Hello_world;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.polsl.firmakurierska")
@EnableJpaRepositories("com.polsl.firmakurierska.repository")
@EntityScan("com.polsl.firmakurierska.model")
@ComponentScan({"com.polsl.firmakurierska.repository","com.polsl.firmakurierska.controller"})
public class FirmaKurierskaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		// SpringApplication.run(FirmaKurierskaApplication.class, args);
		new SpringApplicationBuilder(FirmaKurierskaApplication.class).headless(false).run(args);
	}

	// Method that runs a test window
	@Override
    public void run(String... args) {

		Hello_world mainMenu = new Hello_world();
		mainMenu.main(args);
    }
}
