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
    @ApiResponse(responseCode = "200")
    @GetMapping(produces = "application/vnd.user.v1+json")
    public Iterable<User> findAllUsers() {
        Iterable<User> list = Arrays.asList(new User("moe"),new User("larry"));
        return list;
    }

    @Operation(summary = "get list of users")
    @ApiResponse(responseCode = "200")
    @GetMapping(produces = "application/vnd.user.v2+json")
    public Iterable<Userv2> findAllUsersv2() {
        Iterable<Userv2> list = Arrays.asList(new Userv2("moe","fine"),new Userv2("larry","fine"));
        return list;
    }



}
