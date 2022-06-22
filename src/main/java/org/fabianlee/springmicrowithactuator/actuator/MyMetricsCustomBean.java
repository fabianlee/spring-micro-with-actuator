package org.fabianlee.springmicrowithactuator.actuator;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fabianlee.springmicrowithactuator.product.Product;
import org.fabianlee.springmicrowithactuator.product.ProductController;
import org.fabianlee.springmicrowithactuator.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class MyMetricsCustomBean {

    Logger logger = LoggerFactory.getLogger(MyMetricsCustomBean.class);

    public void logit(String s) {
        try {
            FileWriter fw = new FileWriter("/tmp/fw.log", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s);
            bw.newLine();
            bw.close();
        }catch(Exception exc) {}
    }

    // REST controller for metrics
    @Lazy
    @Autowired
    protected ProductController productController;

    // Database for metrics
    @Lazy
    @Autowired
    protected ProductRepository productRepository;
    

    // multigauge counts for low inventory
    MultiGauge lowInventoryCounts = null;
    MultiGauge sysEnvKeys = null;

    public void setSysEnvKeys() {
        boolean overWrite = true;
        Map<String, String> all_env = System.getenv();
        sysEnvKeys.register(
            all_env.keySet().stream().
            filter( res -> res.startsWith("JAVA") || res.startsWith("K8S_") ).
            map(
                (String res) -> MultiGauge.Row.of( Tags.of("key",res,"value",all_env.get(res)), 0 )
            ).collect(Collectors.toList()
            )
        ,overWrite);
    }

    public void updateLowInventoryGauges() {
        boolean overWrite = true;
        if(productRepository==null) {
            logit("not updating low inventory gauges yet because productRepository is null");
            return;
        }

        // create MultiGauge.Row for each product with low inventory count
        lowInventoryCounts.register(
            productRepository.findProductWithLowInventoryCount().stream().
            map(
                (Product p) -> MultiGauge.Row.of(Tags.of("pid",""+p.getId(),"pname",p.getName()),p.getCount())
            ).
            collect(Collectors.toList()
            )
        ,overWrite);

    }

    public Supplier<Number> fetchSalesCounter() {
        return ()->productController.getSaleCounter();
    }
    public Supplier<Number> fetchRevenueCounter() {
        return ()->(float)productController.getRevenueCounter()/100;
    }
    public Supplier<Number> fetchProductCount(Product p) {
        return ()->p.getCount();
    }

    public MyMetricsCustomBean(MeterRegistry registry) {

        // hardcoded metric with no tags
        registry.gauge("foo1.test", 0);
        // hardcoded metric with tags
        registry.gauge("foo2.test", Tags.of("foo","bar","another","tag"), 2);
        // builder to create metric with tags and random value
        Supplier<Number> randomValue = () -> Math.random();
        Gauge.builder("foo3.test",randomValue).
            tag("name","foo").
            tag("name3","foo3").
            description("foo descrip").
            register(registry);


        // dynamic metrics from controller
        Gauge.builder("number.of.sales",fetchSalesCounter()).register(registry);
        Gauge.builder("total.revenue",fetchRevenueCounter()).register(registry);

        // dynamically sized dimensions from database
        // rely on updateLowInventoryGauges() to populate because data is not available here
        lowInventoryCounts = MultiGauge.builder("low.inventory.count").tag("pid","pname").register(registry);

        // dynamically sized from environment keys
        sysEnvKeys = MultiGauge.builder("sys.env").tag("key","value").register(registry);
        // can immediately populate system env keys because they will not change
        setSysEnvKeys();

    } // constructor

} // class
