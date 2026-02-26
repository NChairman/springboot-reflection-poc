package com.poc.reflection.springboot.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo controller to illustrate Spring Framework 3+ (6.x) reflection behavior:
 * without -parameters compiler flag or explicit param/path names, endpoints
 * that rely on parameter name discovery can fail at runtime.
 */
@RestController
@RequestMapping("/demo")
public class ReflectionDemoController {

    /**
     * FAIL case: relies on reflection to discover parameter names.
     * Fails at runtime if the class was not compiled with -parameters
     * (Spring 6.1+ no longer uses LocalVariableTableParameterNameDiscoverer by default).
     */
    @GetMapping("/fail")
    public String fail(
            @RequestParam String query,
            @RequestParam(required = false) String filter) {
        return "query=" + query + ", filter=" + filter;
    }

    /**
     * SUCCESS case: explicit names avoid the need for parameter name discovery.
     * Works regardless of -parameters compiler flag.
     */
    @GetMapping("/success")
    public String success(
            @RequestParam(name = "query") String q,
            @RequestParam(name = "filter", required = false) String f) {
        return "query=" + q + ", filter=" + f;
    }

    /**
     * FAIL case for path variables: no explicit name.
     */
    @GetMapping("/fail/{id}")
    public String failPath(@PathVariable String id) {
        return "id=" + id;
    }

    /**
     * SUCCESS case for path variables: explicit name.
     */
    @GetMapping("/success/{id}")
    public String successPath(@PathVariable(name = "id") String resourceId) {
        return "id=" + resourceId;
    }
}
