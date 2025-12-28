package org.mailosz.githubviewer;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 8081)
public class GithubViewerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Value("classpath:proxyResponses/happy-ending-response.json")
    private Resource happyEndingJson;

    @Value("classpath:proxyResponses/user-not-found.json")
    private Resource notFoundJson;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("github.api.default-url", () -> "http://localhost:"+ 8081);
    }

    @Test
    void shouldReturnOnlyNotForkedRepositories() throws IOException {
//        Stubbing for repos (one repo with fork)
        stubFor(get(urlEqualTo("/users/Mailosz0812/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-repo-response.json")));

//        Stubbing for branches (branches only for non-fork repo)
        stubFor(get(urlEqualTo("/repos/Mailosz0812/DietPlanner/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-branches-response.json")));

        String expectedBody = new String(happyEndingJson.getInputStream().readAllBytes());

//        Final request to API
        webTestClient.get()
                .uri("/user/Mailosz0812")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(expectedBody);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExists() throws IOException{
//        Stubbing for non-existent user
        stubFor(get(urlEqualTo("/users/nonExistUser/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github-user-not-found.json")));

        String expectedBody = new String(notFoundJson.getInputStream().readAllBytes());

//        Final request to API
        webTestClient.get()
                .uri("/user/nonExistUser")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .json(expectedBody);
    }
}
