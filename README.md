#  Spring Boot REST microservice with OpenAPI docs and exported Prometheus metrics

This is a small REST API written with Spring Boot, with documentation and interactive tested provided using OpenAPI (Swagger).  

http://localhost:8080/swagger-ui/index.html

In addition to the main service being offered on port 8080, the Actuator metrics are exposed on port 8081 and provide:

* generic JVM metrics at http://localhost:8081/actuator/prometheus
* REST service specific metrics at http://localhost:8081//actuator/prometheus-custom


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
