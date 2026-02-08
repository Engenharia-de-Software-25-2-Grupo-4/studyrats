package com.example.studyrats.E2E;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

public class RequisicoesMock {

    @Autowired
    private MockMvc driver;
    private ObjectMapper objectMapper;
    private String getUrl, putUrl, deleteUrl, postUrl;

    private String tokenFake = "tokenValido";

    public RequisicoesMock(MockMvc driver) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.driver = driver;
        this.getUrl = "";
        this.putUrl = "";
        this.deleteUrl = "";
        this.postUrl = "";
    }

    public RequisicoesMock(MockMvc driver, String baseURL) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.driver = driver;
        this.getUrl = baseURL;
        this.putUrl = baseURL;
        this.deleteUrl = baseURL;
        this.postUrl = baseURL;
    }

    public RequisicoesMock(MockMvc driver, String getURL, String putURL, String deleteURL, String postURL) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.driver = driver;
        this.getUrl = getURL;
        this.putUrl = putURL;
        this.deleteUrl = deleteURL;
        this.postUrl = postURL;
    }

    public <T> T performGetOK(Class<T> expectedTypeResponse) throws Exception {
        String response = driver.perform(get(getUrl).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public void performGetUnauthorized() throws Exception {
        driver.perform(get(getUrl).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performPostUnauthorized(Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(getUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    public <T> T performPostCreated(Class<T> expectedTypeResponse, Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        String response = driver.perform(post(getUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public void performPostCreated(Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(getUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isCreated());
    }

    public void performPostConflict(Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(getUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isConflict());
    }

}