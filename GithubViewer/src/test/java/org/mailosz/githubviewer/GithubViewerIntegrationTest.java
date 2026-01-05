package org.mailosz.githubviewer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubViewerIntegrationTest {

    RestTestClient client;
    static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock(){
        if(wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Value("classpath:proxyResponses/happy-ending-response.json")
    private Resource happyEndingJson;

    @Value("classpath:proxyResponses/user-not-found.json")
    private Resource notFoundJson;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.default-url", () -> wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnOnlyNotForkedRepositories() throws IOException {
        String testName = "Mailosz0812";
//        Stubbing for repos (one repo with fork)
        wireMockServer.stubFor(get(urlEqualTo("/users/" + testName + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-repo-response.json")));

//        Stubbing for branches (branches only for non-fork repo)
        wireMockServer.stubFor(get(urlEqualTo("/repos/" + testName + "/DietPlanner/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-branches-response.json")));

        String expectedBody = new String(happyEndingJson.getInputStream().readAllBytes());

//        Final request to API
        client.get()
                .uri("/user/" + testName)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(expectedBody);

    }
    @Test
    void shouldReturnEmptyListWhenUserHasNoRepositories(){
        String emptyUser = "emptyName";
//        Stubbing for empty list
        wireMockServer.stubFor(get(urlEqualTo("/users/" + emptyUser + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("[]")));
        client.get()
                .uri("/user/" + emptyUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("[]");
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExists() throws IOException {
//        Stubbing for non-existent user
        String nonExistUser = "nonExistUser";
        wireMockServer.stubFor(get(urlEqualTo("/users/" + nonExistUser + "/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-user-not-found.json")));

        String expectedBody = new String(notFoundJson.getInputStream().readAllBytes());

//        Final request to API
        client.get()
                .uri("/user/"+nonExistUser)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json(expectedBody);
    }
}
