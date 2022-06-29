/**
 * https://www.javagists.com/spring-boot-actuator-example
 */
package org.fabianlee.springmicrowithactuator.actuator;


import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class SimpleHealthIndicator implements HealthIndicator {

    // always reports UP
    @Override
    public Health health() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("foo","bar");
        map.put("my","value");
        return new Health.Builder().up().withDetail("simple", map).build();
        //return new Health.Builder().down().withDetail("simple", map).build();
    }

}