package dev.vk.iam;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@SpringBootTest @AutoConfigureMockMvc @Testcontainers(disabledWithoutDocker=true)
class PolicyApiIntegrationTest {
 @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16-alpine");
 @DynamicPropertySource static void db(DynamicPropertyRegistry r){r.add("spring.datasource.url",postgres::getJdbcUrl);r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);}
 @Autowired MockMvc mvc;
 @Test void adminCanCreatePolicyAndAuthenticatedCallerCanDecide() throws Exception {
   String policy="""{"name":"platform-read","effect":"ALLOW","priority":100,"roles":["engineer"],"actions":["read"],"resourcePattern":"document/*","conditions":{"department":"platform"},"enabled":true}""";
   mvc.perform(post("/api/v1/policies").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_policy-admin"))).contentType(MediaType.APPLICATION_JSON).content(policy)).andExpect(status().isCreated());
   String request="""{"subject":"alice","roles":["engineer"],"action":"read","resource":"document/42","attributes":{"department":"platform"}}""";
   mvc.perform(post("/api/v1/decisions").with(jwt()).contentType(MediaType.APPLICATION_JSON).content(request)).andExpect(status().isOk()).andExpect(jsonPath("$.allowed").value(true));
 }
 @Test void callerWithoutAdminRoleCannotManagePolicies() throws Exception {mvc.perform(get("/api/v1/policies").with(jwt())).andExpect(status().isForbidden());}
 @Test void anonymousCallerCannotRequestDecision() throws Exception {mvc.perform(post("/api/v1/decisions").contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isUnauthorized());}
}
