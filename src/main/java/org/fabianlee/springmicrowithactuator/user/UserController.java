package org.fabianlee.springmicrowithactuator.user;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "get list of users")
    @ApiResponse(responseCode = "200", description = "list returned")
    @GetMapping
    public Iterable<User> findAllUsers() {
        Iterable<User> list = Arrays.asList(new User("moe"),new User("larry"));
        return list;
    }


}
