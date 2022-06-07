package org.fabianlee.springmicrowithactuator.persistence;

import java.util.Optional;

import org.fabianlee.springmicrowithactuator.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }

    public void saveOrUpdate(Product product) {
        productRepository.save(product);
    }

    public void delete(long id) {
        productRepository.deleteById(id);
    }
    
}
