/**
 * https://www.javagists.com/spring-boot-actuator-example
 * https://ofstack.com/java/43087/spring-boot-actuator-custom-health-check-tutorial.html
 * https://www.baeldung.com/spring-boot-health-indicators
 */
package org.fabianlee.springmicrowithactuator.actuator;


import java.util.HashMap;
import java.util.Map;

import org.fabianlee.springmicrowithactuator.product.Product;
import org.fabianlee.springmicrowithactuator.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class ProductHealthIndicator implements HealthIndicator {
    
    @Autowired
    protected ProductRepository productRepository;

    @Override
    public Health health() {
        int lowestCount = Integer.MAX_VALUE;

        Map<String,Integer> productCounts = new HashMap<String,Integer>();
        for (Product p:productRepository.findAll()) {
            productCounts.put(p.getName(),Integer.valueOf(p.getCount()));

            // find lowest inventory count
            if(p.getCount()<lowestCount)
                lowestCount = p.getCount(); 
        }

        // if any of the products are out of stock, report health down
        if (lowestCount>0) 
            return new Health.Builder().up().withDetail("products", productCounts).build();
        else
            return new Health.Builder().down().withDetail("products", productCounts).build();
    }

}