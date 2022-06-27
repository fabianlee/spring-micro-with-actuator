/**
 * Additional REST service at /user to test OpenDoc ability to support multiple endpoints
 * 
 * Using versioning through content negotiation (header)
 * https://www.xmatters.com/blog/blog-four-rest-api-versioning-strategies/
 * 
 * TODO: Look at GroupedOpenApi configuration object to have OpenDoc show versioning
 * https://springdoc.org/#migrating-from-springfox
 */
package org.fabianlee.springmicrowithactuator.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/user")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private List<User> userListV1 = new ArrayList<User>(
        Arrays.asList(new User("moe"),new User("larry"),new User("curly"))
    );
    public Supplier<Number> fetchUserCount() {
        return ()->userListV1.size();
    }

    // constructor injector for exposing metrics at Actuator /prometheus
    public UserController(MeterRegistry registry) {

        Gauge.builder("usercontroller.usercount",fetchUserCount()).
            tag("version","v1").
            description("usercontroller descrip").
            register(registry);

    }

    @Timed(value="user.get.time",description="time to retrieve users",percentiles={0.5,0.9})
    @Operation(summary = "get list of users")
    @ApiResponse(responseCode = "200")
    @GetMapping(produces = "application/vnd.user.v1+json")
    public Iterable<User> findAllUsers() {
        logger.trace("doing findAllUsers");
        logger.debug("doing findAllUsers");
        logger.info("doing findAllUsers");
        logger.error("doing findAllUsers");
        return userListV1;
    }

    @Operation(summary = "delete last user")
    @ApiResponse(responseCode = "200")
    @DeleteMapping(produces = "application/vnd.user.v1+json")
    public void deleteUser() {
        logger.debug("called deleteUser");
        if(userListV1.size()>0) {
            userListV1.remove(userListV1.size()-1);
        }
    }

/*
    @Operation(summary = "get list of users")
    @ApiResponse(responseCode = "200")
    @GetMapping(produces = "application/vnd.user.v2+json")
    public Iterable<Userv2> findAllUsersv2() {
        Iterable<Userv2> list = Arrays.asList(new Userv2("moe","fine"),new Userv2("larry","fine"));
        return list;
    }
*/


}
