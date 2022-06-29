/**
 * NOT USED!!!!!  
 * This TimedAspect must already be configured by dependencies, because 
 * bean by this name is already registered and overriding is disabled (according to build error)
 * 
 * Enables ability to use @Timed 
 */
package org.fabianlee.springmicrowithactuator.actuator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class NOTUSEDTimedConfiguration {
   //@Bean
   //public TimedAspect timedAspect(MeterRegistry registry) {
   //   return new TimedAspect(registry);
   //}
}