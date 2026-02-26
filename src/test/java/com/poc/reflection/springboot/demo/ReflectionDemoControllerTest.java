package com.poc.reflection.springboot.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

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
     * When the class is compiled WITH -parameters, Spring discovers
     * parameter names for @RequestParam/@PathVariable without explicit names,
     * so the fail endpoint now returns 200 OK.
     */
    @Test
    void failEndpoint_withParameterNames_returnsOk() throws Exception {
        mockMvc.perform(get("/demo/fail").param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string("query=test, filter=null"));
    }

    @Test
    void failPathEndpoint_withParameterNames_returnsOk() throws Exception {
        mockMvc.perform(get("/demo/fail/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("id=42"));
    }
}
