package org.fabianlee.springmicrowithactuator.persistence;

import java.util.Collection;

import org.fabianlee.springmicrowithactuator.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // JPQL: notice that 'Product' matches the POJO name, not the plural DB table name
    @Query("SELECT p FROM Product p WHERE p.count <= 3")
    // if we were using native sql, 'Products' would be plural because that is the DB table name
    //@Query(value="SELECT * FROM Products p WHERE p.count<=3",nativeQuery = true)
    Collection<Product> findProductWithLowInventoryCount();

}