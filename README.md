#  Spring Boot REST microservice with OpenAPI docs and exported Prometheus metrics

This is a small REST API built with the Spring Boot framework, to illustrate the use of:

* OpenAPI standard ([Swagger](https://swagger.io/tools/swagger-ui/)) for documentation and interactive testing
* Exposing metrics via [Actuator](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/actuator.html) and [Micrometer](https://micrometer.io/docs/concepts) for Prometheus consumption
* Testing custom ServiceMonitor and Prometheus alerts coming from service

## Domain Model

The domain model is a simple product inventory.  You have a list of [Products](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/domain/Product.java), where each has:

* id (long, database unique identifier)
* name (string, 120 chars in length describing item)
* count (int, how many items are still available)
* price (double, price of each item in dollars and cents)

These objects are stored in an H2 in-memory database, using JPA for a simple [CrudRepository of Product](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/persistence/ProductRepository.java).  This database can be browsed with the h2-console web UI at:
* http://localhost:8080/h2-console (jdbc url=jdbc:h2:mem:testdb; username=sa, password=&lt;empty&gt;)

The [data.sql](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/resources/data.sql) file populates the database at container startup.

```
insert into products (id,name,count,price) VALUES (1,'Wrist Watch7',113,24.95);
insert into products (id,name,count,price) VALUES (2,'Coffee Cup',3,5.95);
insert into products (id,name,count,price) VALUES (3,'T-shirt',40,29.99);
insert into products (id,name,count,price) VALUES (4,'LCD Monitor',5,199.00);
```

The system considers a product in low inventory if there are less than 3 in stock.  For example, you can see we start with 3 Coffe Cups, so if even one is purchased, 
the system will consider this product in low inventory and send alerts.

If there are no items left in stock, the custom [ProductHealthIndicator](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/actuator/ProductHealthIndicator.java) will start reporting "DOWN", at http://localhost:8081/actuator/health.  When deployed in Kubernetes, this will cause the container to be marked unhealthy and restarted since this is its [healthcheck](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/resources/kubernetes/deployment-spring-micro-with-actuator.yaml#L62https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/resources/kubernetes/deployment-spring-micro-with-actuator.yaml#L62).


## REST Service

The [REST service](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/service/ProductController.java) exposes the following resource endpoints:

* GET /api/product - list all products
* GET /api/product/{id} - fetch a product by id
* POST /api/product - create a new product
* PUT /api/product - create or update a product
* POST /api/product/{id}/sale - create a sale record for specific product

These services can be invoked from a simple REST client or curl/wget, but they are also self-documented and exposed from the OpenAPI documentation (Swagger) coming from the 'springdoc-openapi-ui' project dependency.

* http://localhost:8080/swagger-ui/index.html

## Prometheus Metrics

This service exposes metrics from 3 different endpoints to illustrate multiple ways
to achieve Prometheus monitoring integration.

* basic build metrics on main service port - http://localhost:8080/metrics
* JVM and custom metrics using Actuator on mgmt port - http://localhost:8081/actuator/prometheus
* Service level metrics using custom Actuator endpoint on mgmt port - http://localhost:8081/actuator/prometheus-custom

### basic build metrics exposed at :8080/metrics:

* spring_micro_with_actuator - set to 0.0, simply there to test for existence
* management_server_port - pulled from [application.properties](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/resources/application.properties), port where /actuator is exposed
* spring_micro_with_actuator_info - multidimensional metric that pulls info from [build.gradle](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/build.gradle) for name, group, version

### JVM and custom metrics exposed at :8081/prometheus:

The 'micrometer-registry-prometheus' package by default exposes many generic JVM level metrics such as memory and disk utilization at :8081/prometheus.  We can add our own custom
metrics to this endpoint by creating a Class that uses [constructor injection of the MeterRegistry](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/actuator/MyMetricsCustomBean.java).


* number_of_sales - how many items have been sold since the service started
* total_revenue - the total dollar amount that has been sold using this service (any and all items)
* low_inventory_count{pid=%d,pname="%s"} - [tagged metric](https://sysdig.com/blog/prometheus-metrics/) that shows products whose count is less than 3
* sys_env{key="%s",value="%s"} - any environment values that start with 'K8S_', useful to capture environment vars such as 'K8S_node_name' that are passed via [K8S Downward API](https://fabianlee.org/2021/05/01/kubernetes-using-the-downward-api-to-access-pod-container-metadata/)


### JVM and custom metrics exposed at :8081/prometheus-custom:

We can expose our own Actuator custom endpoint at ':8081/actuator/prometheus-custom' by using the [ControllerEndpoint annotation](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/java/org/fabianlee/springmicrowithactuator/actuator/CustomPrometheusEndpoint.java):

* custom_number_of_sales - how many items have been sold since the service started
* custom_total_revenue - the total dollar amount that has been sold using this service (any and all items)
* custom_low_inventory_count{pid=%d,pname="%s"} - [tagged metric](https://sysdig.com/blog/prometheus-metrics/) that shows products whose count is less than 3
* custom_sys_env{key="%s",value="%s"} - any environment values that start with 'K8S_', useful to capture environment vars such as 'K8S_node_name' that are passed via [K8S Downward API](https://fabianlee.org/2021/05/01/kubernetes-using-the-downward-api-to-access-pod-container-metadata/)


### Prometheus and AlertManager rules

Once these values are scraped using Prometheus, they can be configured to alert.  

For example, Prometheus and AlertManager alert via a [PrometheusRule](https://github.com/fabianlee/spring-micro-with-actuator/blob/main/src/main/resources/kubernetes/prometheusrule-spring-micro-with-actuator.yaml) when a product is reaching low levels of inventory (<3 left) so that it can be reordered from Suppliers.

```
expr: low_inventory_count{}<3
```


## Running as SpringBoot Jar for local development

```
#
# FOR GRADLE
#
./gradlew tasks
./gradlew --refresh-dependencies
./gradlew build -x test
# run application in one console
./gradlew bootRun
# run in another console to auto-update when files changed
./gradlew build --continuous


#
# FOR MAVEN
#
./mvnw compile package
./mvnw spring-boot:run
```

## Building Docker image and running locally

```
# build docker image locally
./gradlew bootJar docker
# push to Docker Hub
./gradlew bootJar dockerPush


export VERSION=0.0.2-SNAPSHOT

# run in foreground
./gradlew dockerRun
OR
docker run -it -p 8080:8080 -p 8081:8081 --rm fabianlee/spring-micro-with-actuator:$VERSION /bin/bash

# run in background
docker run -d -p 8080:8080 -p 8081:8081 --rm --name spring-micro-with-actuator fabianlee/spring-micro-with-actuator:$VERSION

# create new running container, but go to shell instead of server being run
docker run -it --rm --entrypoint /bin/bash fabianlee/spring-micro-with-actuator:$VERSION

# examine inside of daemonized container where server is being run
docker exec -it spring-micro-with-actuator /bin/bash

# stop container
./gradlew dockerStop
OR
docker stop spring-micro-with-actuator
```

## Deploy on Kubernetes

```
cd src/main/resources/kubernetes
export VERSION=0.0.2-SNAPSHOT
echo "Using KUBECONFIG $KUBECONFIG"

# create deployment and service
envsubst < deployment-spring-micro-with-actuator.yaml | kubectl apply -f -
kubectl get deployment spring-micro-with-actuator

# main ingress for end users of API
kubectl apply -f ingress-spring-micro-rest.yaml
# ingress to validate actuator metrics on mgmt port (this would not be exposed in prod)
kubectl apply -f ingress-spring-micro-actuator.yaml
kubectl get ingress
```

## Monitor with Prometheus Operator

```
cd src/main/resources/kubernetes

# ServiceMonitor that picks up the three custom metric endpoints for this service
# on main port: /metrics
# on mgmt port: /prometheus, /prometheus-custom
kubectl apply -f servicemonitor-spring-micro-with-actuator.yaml

# rules for product low inventory counts
kubectl apply -f prometheusrule-spring-micro-with-actuator.yaml

# restart AlertManager so changes take affect
kubectl rollout restart statefulsets alertmanager-prom-stack-kube-prometheus-alertmanager -n prom
kubectl rollout status statefulset alertmanager-prom-stack-kube-prometheus-alertmanager -n prom
```


## Project initially created using Spring Intializer

[Spring Initializer Web UI](https://start.spring.io/)

```
id=spring-micro-with-actuator
artifact_id="${id//-}"
SpringAppClassName=SpringMicroMain
version="0.0.2-SNAPSHOT"
curl https://start.spring.io/starter.zip \
    -d type=maven-project|gradle-project
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
chmod +x ./mvnw ./gradlew
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
