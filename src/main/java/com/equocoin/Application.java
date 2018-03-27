package com.equocoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("deprecation")
@SpringBootApplication
@Configuration
@EnableAutoConfiguration // Sprint Boot Auto Configuration
@ComponentScan(basePackages = "com.equocoin")
@PropertySource({ "classpath:application.properties", "classpath:message.properties" })
public class Application extends SpringBootServletInitializer {
	private static final Class<Application> applicationClass = Application.class;
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(applicationClass);
	public static void main(String[] args) {
		
		SpringApplication.run(applicationClass, args);
	}
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
}
