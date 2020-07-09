package com.demo.services.eureka.naming.eurekanaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaNamingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaNamingApplication.class, args);
	}

}
