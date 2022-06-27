/**
 * Service level captures the business logic
 * makes calls out to persistence and other systems necessary for integration
*/
package org.fabianlee.springmicrowithactuator.product;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductRepository productRepository;

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }

    public void saveOrUpdate(Product product) {
        Optional<Product> productInDB = productRepository.findById(product.getId());

        if ( productInDB.isEmpty()) {
            logger.error("Need to create new Product: " + product.toString());
            productRepository.save(product);
        }else {
            logger.error("Need to update product: " + productInDB.get().toString());
            // do copy of params into DB object
            productInDB.get().setName(product.getName());
            productInDB.get().setCount(product.getCount());
            productInDB.get().setPrice(product.getPrice());
            productRepository.save(productInDB.get());
         }

    }

    public void delete(long id) {
        productRepository.deleteById(id);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public boolean sold(long id) {
        Optional<Product> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            int current_count = product.get().getCount();

            // if we still have more, then decrement inventory count and save to DB
            if (current_count>0) {
                product.get().setCount(current_count-1);
                productRepository.save(product.get());
                logger.warn("New count for " + product.get().getName() + ": " + current_count);
                return true;
            }
        }

        return false;
    }
    
}
