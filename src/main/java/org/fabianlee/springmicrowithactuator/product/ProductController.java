/**
 * Controller level exposes REST API
 * Should minimize business logic by calling out to service
 */
package org.fabianlee.springmicrowithactuator.product;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fabianlee.springmicrowithactuator.actuator.MyMetricsCustomBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/product")
public class ProductController {

    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    // used to update metrics
    @Autowired
    MyMetricsCustomBean myMetricsCustomBean;

    // live runtime metrics for number and amount of sales
    private final AtomicLong saleCounter = new AtomicLong();
    private final AtomicLong revenueCounter = new AtomicLong();

    public long getSaleCounter() {
        logger.debug("getSalesCounter");
        return saleCounter.get();
    }

    public long getRevenueCounter() {
        return revenueCounter.get();
    }

    @Operation(summary = "get list of products")
    @ApiResponse(responseCode = "200", description = "list returned")
    @GetMapping
    public Iterable<Product> findAllProducts() {
        logger.debug("findAllProducts");
        return productService.findAll();
    }

    @Timed(value="update.time",description="time to update",percentiles={0.5,0.9})
    @Operation(summary = "create or update product")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@Validated @RequestBody Product product) {
        productService.saveOrUpdate(product);

        // update metrics reported via Actuator
        myMetricsCustomBean.updateLowInventoryGauges();

        return ResponseEntity.ok().body(product);
    }

    @Operation(summary = "create product")
    @PostMapping
    public ResponseEntity<Product> saveProduct(@Validated @RequestBody Product product) {
        productService.save(product);
        return ResponseEntity.ok().body(product);
    }

    @Operation(summary = "get product by id")
    @ApiResponse(responseCode = "200", description = "Product returned")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable(value = "id") long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok().body(product.get());
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // create new sale record, NOT idempotent
    @Operation(summary = "create new record of sale, not idempotent")
    @PostMapping("/{id}/sale")
    public ResponseEntity<Product> saleExecuted(@PathVariable(value = "id") long id) {
        logger.error("going to do sale");
        boolean isSold = productService.sold(id);
        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        if (isSold) {
            // increment the number of sales
            saleCounter.getAndIncrement();
            // add price of item sold
            revenueCounter.getAndAdd((long) (product.get().getPrice() * 100));

            // update metrics reported via Actuator
            myMetricsCustomBean.updateLowInventoryGauges();

            return ResponseEntity.ok().body(product.get());
        } else {
            // use a 412 HTTP status to indicate precondition failed
            logger.error("Item count already 0, cannot decrement any more");
            return ResponseEntity.status(412).build();
        }

    }

}
