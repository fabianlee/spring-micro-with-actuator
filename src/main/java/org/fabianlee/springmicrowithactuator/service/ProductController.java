package org.fabianlee.springmicrowithactuator.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.fabianlee.springmicrowithactuator.domain.Product;
import org.fabianlee.springmicrowithactuator.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/product")
public class ProductController {

    Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductRepository productRepository;

    // live runtime metrics for number and amount of sales
    private final AtomicLong saleCounter = new AtomicLong();
    private final AtomicLong revenueCounter = new AtomicLong();
    public long getSaleCounter() {
        return saleCounter.get();
    }
    public long getRevenueCounter() {
        return revenueCounter.get();
    }

    @GetMapping
    public Iterable<Product> findAllProducts() {
        return productRepository.findAll();
    }

    // for new or updates to existing, is idempotent
    @PutMapping(value="/",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@Validated @RequestBody Product product) {
        Optional<Product> productInDB = productRepository.findById(product.getId());

        if ( productInDB.isEmpty()) {
            logger.error("Need to create new Product: " + product.toString());
            productRepository.save(product);
            return ResponseEntity.ok().body(product);
        }else {
            logger.error("Need to update product: " + productInDB.get().toString());
            // do copy of params into DB object
            productInDB.get().setName(product.getName());
            productInDB.get().setCount(product.getCount());
            productInDB.get().setPrice(product.getPrice());
            productRepository.save(productInDB.get());
            return ResponseEntity.ok().body(product);
        }
    }

    // for new objects, NOT idempotent
    @PostMapping("/")
    public ResponseEntity<Product> saveProduct(@Validated @RequestBody Product product) {
        productRepository.save(product);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable(value="id") long id) {
        Optional<Product> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            return ResponseEntity.ok().body(product.get());
        }else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // create new sale record, NOT idempotent
    @PostMapping("/{id}/sale")
    public ResponseEntity<Product> saleExecuted(@PathVariable(value="id") long id) {
        Optional<Product> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            int current_count = product.get().getCount();

            // if we still have more, then decrement inventory count and save to DB
            // else use a 412 HTTP status to indicate precondition failed
            if (current_count>0) {
                product.get().setCount(current_count-1);
                productRepository.save(product.get());
                logger.warn("New count for " + product.get().getName() + ": " + current_count);

                // increment the number of sales
                saleCounter.getAndIncrement();
                // add price of item sold
                revenueCounter.getAndAdd((long)(product.get().getPrice()*100));

                return ResponseEntity.ok().body(product.get());
            }else {
                logger.error("Item count already 0, cannot decrement any more");
                return ResponseEntity.status(412).build();
            }
        }else {
            return ResponseEntity.notFound().build(); // 404
        }
    }



    
}
