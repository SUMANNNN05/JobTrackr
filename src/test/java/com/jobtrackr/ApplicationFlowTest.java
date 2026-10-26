package com.jobtrackr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** End-to-end test against a real PostgreSQL (Testcontainers) with Flyway migrations applied. */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ApplicationFlowTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String registerAndGetToken(String email) throws Exception {
        String body = """
                {"email":"%s","password":"password123","fullName":"Test User"}""".formatted(email);
        String res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return om.readTree(res).get("token").asText();
    }

    @Test
    void fullApplicationLifecycle() throws Exception {
        String token = registerAndGetToken("flow@test.com");
        String auth = "Bearer " + token;

        // create
        String created = mvc.perform(post("/api/applications")
                        .header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"company":"Acme","role":"Backend Engineer","status":"APPLIED","appliedOn":"2026-01-12"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andReturn().getResponse().getContentAsString();
        long id = om.readTree(created).get("id").asLong();

        // list
        mvc.perform(get("/api/applications").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        // move through the pipeline
        mvc.perform(patch("/api/applications/" + id + "/status")
                        .header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INTERVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));

        // history: null->APPLIED, APPLIED->INTERVIEW
        mvc.perform(get("/api/applications/" + id + "/history").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // analytics
        mvc.perform(get("/api/analytics/summary").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.byStatus.INTERVIEW").value(1))
                .andExpect(jsonPath("$.interviewRate").value(100.0));

        mvc.perform(get("/api/analytics/weekly").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cumulative").value(1));
    }

    @Test
    void usersCannotSeeEachOthersData() throws Exception {
        String a = "Bearer " + registerAndGetToken("a@test.com");
        String b = "Bearer " + registerAndGetToken("b@test.com");

        String created = mvc.perform(post("/api/applications")
                        .header("Authorization", a).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"company\":\"Secret Co\",\"role\":\"SDE\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = om.readTree(created).get("id").asLong();

        mvc.perform(get("/api/applications/" + id).header("Authorization", b))
                .andExpect(status().isNotFound());
    }

    @Test
    void protectedEndpointsRequireAuth() throws Exception {
        mvc.perform(get("/api/applications")).andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateRegistrationIsRejected() throws Exception {
        registerAndGetToken("dup@test.com");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"dup@test.com\",\"password\":\"password123\",\"fullName\":\"X\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidLoginReturns401() throws Exception {
        registerAndGetToken("login@test.com");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@test.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }
}
