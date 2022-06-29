/**
 * Simple custom actuator endpoint
 */
package org.fabianlee.springmicrowithactuator.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "simple")
public class SimpleCustomEndpoint {

    protected static final Map<String,String> keys = new HashMap<String,String>();
    static {
        keys.put("foo","bar");
        keys.put("mykey","myval");
    }

    // web request to /actuator/simple
    @ReadOperation
    public Map<String,String> doRead() {
        return keys;
    }

    // web request to /actuator/simple/<pathName>
    @ReadOperation
    public String doReadAtPath(@Selector String pathName) {
        return "hello at " + pathName;
    }



}