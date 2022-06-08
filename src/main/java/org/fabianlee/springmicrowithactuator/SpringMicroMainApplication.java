package org.fabianlee.springmicrowithactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.fabianlee.springmicrowithactuator")
public class SpringMicroMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMicroMainApplication.class, args);
	}

}
