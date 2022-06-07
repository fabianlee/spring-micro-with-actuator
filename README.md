#  Spring Boot REST microservice with OpenAPI docs and exported Prometheus metrics

This is a small REST API using the Spring Boot framework, with REST documentation and interactive test page provided using the OpenAPI standard ([Swagger](https://swagger.io/tools/swagger-ui/)).  Metrics are exposed in prometheus format.

## Domain Model

The domain model is a simple product inventory.  You have a list of [Products](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/domain/Product.java), where each has:

* id (long, database unique identifier)
* name (string, 120 chars in length describing item)
* count (int, how many items are still available)
* price (double, price of each item in dollars and cents)

These objects are stored in an H2 in-memory database, using JPA for a simple [CrudRepository of Product](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/persistence/ProductRepository.java).  This database can be browsed with the h2-console web UI at:
* http://localhost:8080/h2-console (jdbc url=jdbc:h2:mem:testdb; username=sa, password=&lt;empty&gt;)


## REST Service

The [REST service](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/service/ProductController.java) exposes the following resource endpoints:

* GET /api/product - list all products
* GET /api/product/{id} - fetch a product by id
* POST /api/product - create a new product
* PUT /api/product - create or update a product
* POST /api/product/{id}/sale - create a sale record for specific product

These services can be invoked from a simple REST client or curl/wget, but they are also self-documented and exposed from the integrated OpenAPI documentation (Swagger):

* http://localhost:8080/swagger-ui/index.html

## Prometheus Metrics

In addition to the main RestController being offered on port 8080, the Actuator metrics are exposed on port 8081

* generic JVM metrics - http://localhost:8081/actuator/prometheus
* [REST service specific metrics](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/actuator/CustomPrometheusEndpoint.java) - http://localhost:8081//actuator/prometheus-custom

The service specific metrics at '/actuator/prometheus-custom' provide business level metrics that can be alerted on:

* number_of_sales - how many items have been sold since the service started
* total_revenue - the total dollar amount that has been sold using this service (any and all items)
* low_inventory_count{id=%d,name="%s"} - [tagged metric](https://sysdig.com/blog/prometheus-metrics/) that shows objects whose count is less than 3

These values can be scraped using Prometheus, and configured to alert.  For example, alerts could be emailed to staff when a product is reaching low levels of inventory so that it could reordered from Suppliers.


## Project initially created using Spring Intializer

[Spring Initializer Web UI](https://start.spring.io/)

```
id=spring-micro-with-actuator
artifact_id="${id//-}"
SpringAppClassName=SpringMicroMain
version="0.0.2-SNAPSHOT"
curl https://start.spring.io/starter.zip \
    -d dependencies=web,prometheus,devtools,actuator,thymeleaf,h2,data-jpa \
    -d javaVersion=11 \
    -d bootVersion=2.7.0 \
    -d groupId=org.fabianlee \
    -d artifactId=$artifact_id \
    -d name=$SpringAppClassName \
    -d baseDir=$id \
    -d version=$version \
    -o $id.zip

unzip $id.zip
cd $id
chmod +x ./mvnw

./mvnw compile package

./mvnw spring-boot:run
#java -jar target/$id-$version.jar
```


## References

#### data and JPA
* https://stackoverflow.com/questions/38040572/spring-boot-loading-initial-data
* https://docs.spring.io/spring-boot/docs/2.1.x/reference/html/howto-database-initialization.html
* https://www.baeldung.com/spring-data-jpa-generate-db-schema
* https://www.baeldung.com/spring-boot-data-sql-and-schema-sql
* http://www.h2database.com/html/features.html#connection_modes

#### h2
* http://localhost:8080/h2-console (jdbc url=jdbc:h2:mem:testdb; username=sa, password=<empty>)

#### metrics collection
* https://www.baeldung.com/spring-rest-api-metrics
* https://stackoverflow.com/questions/56987541/metrics-collection-for-spring-boot-rest-apis

### prometheus
* https://docs.spring.io/spring-metrics/docs/current/public/prometheus
