/**
 * Should be automatically picked up by Prometheus Operator
 */
package org.fabianlee.springmicrowithactuator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;

@Controller
public class StandardMetrics {

    // pull from build.gradle via 'springBoot' directive
    @Autowired
    private BuildProperties buildProperties;

    // pull from application.properties
    @Value("${management.server.port}")
    protected String mgmtPort;

    @RequestMapping(value="/metrics",method=RequestMethod.GET,produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String metrics() {

        // create map of all keys we want reported back
        Map<String,String> kv = new TreeMap<String,String>();
        kv.put("key1","0.0");
        kv.put("spring_micro_with_actuator","0.0");
        kv.put("management_server_port",mgmtPort);
        kv.put(String.format("spring_micro_with_actuator_info{version=\"%s\"}",buildProperties.getVersion().toString()),"0.0");
        kv.put(String.format("spring_micro_with_actuator_info{name=\"%s\"}",buildProperties.getName().toString()),"0.0");
        kv.put(String.format("spring_micro_with_actuator_info{group=\"%s\"}",buildProperties.getGroup().toString()),"0.0");

        // build sorted, line-by-line for each key
        StringBuffer sbuf = new StringBuffer();
        for (Iterator<String> it=kv.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sbuf.append(key + " " + kv.get(key) + "\n");
        }

        return sbuf.toString();
    }

    
}
