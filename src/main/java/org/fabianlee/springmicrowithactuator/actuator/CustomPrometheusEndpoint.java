package org.fabianlee.springmicrowithactuator.actuator;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.fabianlee.springmicrowithactuator.domain.Product;
import org.fabianlee.springmicrowithactuator.persistence.ProductRepository;
import org.fabianlee.springmicrowithactuator.service.ProductController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Component
@ControllerEndpoint(id = "prometheus-custom")
public class CustomPrometheusEndpoint {


    static HashMap<String,String> map = new HashMap<String,String>();
    static {
        map.put("my_metric_1","10.2");
        map.put("another_value","testvalue");
        map.put("last_one","foobar");
    }

    /* Instance variable(s): */
    @Autowired
    protected SpringTemplateEngine mMessageTemplateEngine;

    @Autowired
    ProductController productController;

    @Autowired
    private ProductRepository productRepository;

    DecimalFormat decimalFormat = new DecimalFormat("#####.00");

    @GetMapping(value="/", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String customEndPoint(){
        final Context theContext = new Context();

        // static key
        theContext.setVariable("foo", "bar");

        // map for final set of keys
        SortedMap<String,String> map = new TreeMap<String,String>();
        theContext.setVariable("metrics", map);

        // metrics from controller
        map.put("number_of_sales","" + productController.getSaleCounter());
        map.put("total_revenue","" + decimalFormat.format((float)productController.getRevenueCounter()/100));

        // metrics from database
        Collection<Product> productLowCount = productRepository.findProductWithLowInventoryCount();
        for ( Product p:productLowCount) {
            map.put(
                String.format("low_inventory_count{id=%d,name=\"%s\"}",p.getId(),p.getName()),
                "" + p.getCount()
                );
        }

        // metrics from environment
        Map<String, String> all_env = System.getenv();
        for (Iterator<String> it=all_env.keySet().iterator(); it.hasNext();) {
            String envKey = it.next();

            // only add env keys that we are interested in
            if ( envKey.startsWith("JAVA_") || envKey.startsWith("K8S_"))
                map.put(envKey,all_env.get(envKey).toString());
        }

        // render final set of prometheus formatted key/values using Thymeleaf text template
        String theTextMessage =  mMessageTemplateEngine.process("text/prometheus-custom.txt", theContext);
        return theTextMessage;
    }

}