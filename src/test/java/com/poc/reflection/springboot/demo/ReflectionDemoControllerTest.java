package com.poc.reflection.springboot.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReflectionDemoController.class)
class ReflectionDemoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void successEndpoint_withExplicitParamNames_returnsOk() throws Exception {
        mockMvc.perform(get("/demo/success").param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string("query=test, filter=null"));
    }

    @Test
    void successPathEndpoint_withExplicitPathVariableName_returnsOk() throws Exception {
        mockMvc.perform(get("/demo/success/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("id=42"));
    }

    /**
     * When the class is compiled without -parameters, Spring cannot discover
     * parameter names for @RequestParam/@PathVariable without explicit names,
     * so invoking the fail endpoint throws (e.g. ServletException wrapping IllegalArgumentException).
     * This test verifies that behavior; if you enable -parameters in build.gradle,
     * this endpoint may return 200 and this test would need to be updated.
     */
    @Test
    void failEndpoint_withoutParameterNames_throws() {
        assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/demo/fail").param("query", "test")).andReturn());
    }

    @Test
    void failPathEndpoint_withoutParameterNames_throws() {
        assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/demo/fail/42")).andReturn());
    }
}
