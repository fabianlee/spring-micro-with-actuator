package org.fabianlee.springmicrowithactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@EntityScan("org.fabianlee.springmicrowithactuator")
public class SpringMicroMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMicroMainApplication.class, args);
	}

	// https://www.tutorialworks.com/spring-boot-prometheus-micrometer/
	// https://github.com/monodot/spring-boot-with-metrics/tree/main/src/main/java/com/tutorialworks/demos/springbootwithmetrics
	// for @Timed methods
	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect();
	}

}
