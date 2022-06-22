/**
 * Persistence level CRUD operations against Product POJO
 */
package org.fabianlee.springmicrowithactuator.product;

import java.util.Collection;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // JPQL: notice that singular 'Product' matches the POJO name, not the plural DB table name
    @Query("SELECT p FROM Product p WHERE p.count <= 3")
    // if we were using native sql, plural 'Products' because that is the DB table name
    //@Query(value="SELECT * FROM Products p WHERE p.count<=3",nativeQuery = true)
    Collection<Product> findProductWithLowInventoryCount();

}