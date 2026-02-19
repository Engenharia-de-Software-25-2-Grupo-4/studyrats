package com.example.studyrats.E2E;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

public class RequisicoesMock {

    private MockMvc driver;
    private ObjectMapper objectMapper;
    private String getUrl, putUrl, deleteUrl, postUrl;

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

    public void performGetOK(String complementoDoPath, String userToken) throws Exception {
        driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk());
    }

    public <T> T performGetOK(Class<T> expectedTypeResponse) throws Exception {
        String response = driver.perform(get(getUrl).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public <T> T performGetOK(TypeReference<T> expectedTypeResponse, String userToken) throws Exception {
        String response = driver.perform(get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public <T> T performGetOK(Class<T> expectedTypeResponse, String complementoDoPath, String userToken) throws Exception {
        String response = driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public <T> T performGetOK(Class<T> expectedTypeResponse, String userToken) throws Exception {
        String response = driver.perform(get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public <T> T performPutOk(Class<T> expectedTypeResponse, Object body, String path, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        String response = driver.perform(put(putUrl + "/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public void performGetNotFound(String complementoDoPath, String userToken) throws Exception {
        driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNotFound());
    }

    public void performGetUnauthorized(String complementoDoPath, String userToken) throws Exception {
        driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performGetUnauthorized(String complementoDoPath) throws Exception {
        driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performGetUnauthorizedToken(String userToken) throws Exception {
        driver.perform(get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performGetUnauthorized() throws Exception {
        driver.perform(get(getUrl).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performGetAcessDenied(String complementoDoPath) throws Exception {
        driver.perform(get(getUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public void performGetAcessDenied() throws Exception {
        driver.perform(get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public <T> T performGetOK(TypeReference<T> expectedTypeResponse, String complementoDoPath, String userToken) throws Exception {
        String response = driver.perform(get(getUrl + "/" + complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public void performPostUnauthorized(Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    public void performPostUnauthorized(String complementoDoPath) throws Exception {
        driver.perform(post(postUrl+"/"+complementoDoPath).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performPostUnauthorized(Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performPostUnauthorized(String complementoDoPath, String userToken) throws Exception {
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performPostUnauthorized(Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performPostAccessDenied() throws Exception {
        driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public void performPostAccessDenied(String complementoDoPath) throws Exception {
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public void performPostAccessDenied(Object body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isForbidden());
    }

    public void performPostNotFound(Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNotFound());
    }

    public void performPostOk(Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk());
    }

    public void performPostOk(String complementoDoPath, String userToken) throws Exception {
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk());
    }

    public <T> T performPostCreated(Class<T> expectedTypeResponse, Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        String response = driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public <T> T performPostCreated(Class<T> expectedTypeResponse, Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        String response = driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public String performPostCreatedStringReturn(String complementoDoPath, String userToken) throws Exception {
        return driver.perform(post(postUrl+"/"+complementoDoPath)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    public void performPostCreated(Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isCreated());
    }


    public void performPostCreated(Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isCreated());
    }

    public void performPostConflict(Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isConflict());
    }

    public void performPostBadRequest(Object body, String complementoDoPath, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(post(postUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    public void performPutAccessDenied(String complementoDoPath) throws Exception {
        driver.perform(put(putUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public void performPutUnauthorized(String complementoDoPath) throws Exception {
        driver.perform(put(putUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performPutUnauthorized(String complementoDoPath, String userToken) throws Exception {
        driver.perform(put(putUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public <T> T performPutOk(Class<T> expectedTypeResponse, Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        String response = driver.perform(put(putUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, expectedTypeResponse);
    }

    public void performPutOk(Object body, String userToken) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(put(putUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isOk());

    }

    public void performPutNotFound(Object body, String userToken, String complementoDoPath) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        driver.perform(put(putUrl + "/" + complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    public void performDeleteAccessDenied() throws Exception {
        driver.perform(put(putUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    public void performDeleteUnauthorized() throws Exception {
        driver.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void performDeleteUnauthorized(String complementoDoPath, String userToken) throws Exception {
        driver.perform(delete(deleteUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performDeleteUnauthorized(String userToken) throws Exception {
        driver.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isUnauthorized());
    }

    public void performDeleteNoContent(String userToken) throws Exception {
        driver.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNoContent());
    }

    public void performDeleteNoContent(String userToken, String complementoDoPath) throws Exception {
        driver.perform(delete(deleteUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNoContent());
    }

    public void performDeleteNotFound(String userToken) throws Exception {
        driver.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNotFound());
    }

    public void performDeleteNotFound(String complementoDoPath, String userToken) throws Exception {
        driver.perform(delete(deleteUrl+"/"+complementoDoPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+userToken))
                .andExpect(status().isNotFound());
    }
}