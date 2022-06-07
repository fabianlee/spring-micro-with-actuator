package org.fabianlee.springmicrowithactuator.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="products")
public class Product {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length=120)
    private String name;

    private int count;

    private Double price;

    public Product() {

    }

    public Product(String name, int count, Double price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product [count=" + count + ", id=" + id + ", name=" + name + ", price=" + price + "]";
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    
}
