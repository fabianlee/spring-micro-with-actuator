/**
 * Custom metrics for REST service
 * 
 * Metrics can be manually checked for syntax using promtool
 * kubectl exec -it -n prom statefulset/prometheus-prom-stack-kube-prometheus-prometheus -c prometheus -- sh
  * echo -e "mymetric 1.0\nanother 2.0" | promtool check metrics
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

    @ReadOperation
    public Map<String,String> doRead() {
        return keys;
    }

    @ReadOperation
    public String doReadAtPath(@Selector String pathName) {
        return "hello at " + pathName;
    }



}