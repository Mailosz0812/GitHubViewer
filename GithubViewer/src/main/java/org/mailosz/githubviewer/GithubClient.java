package org.mailosz.githubviewer;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class GithubClient {
    private final RestClient restClient;
    private final ObjectMapper mapper;

    public GithubClient(RestClient restClient, ObjectMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    public Repository[] getRepositories(String username){
        String uri = "/users/" + username + "/repos";
        Repository[] repos = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String message = this.getMessage(response);
                    throw new ClientException(message,response.getStatusCode().value());
                })
                .body(Repository[].class);
        return repos;
    }

    public Branch[] getRepoBranches(String repoName,String username){
        List<Map<String,Object>> branchesInfo = restClient.get()
                .uri("/repos/{owner}/{repo}/branches",username,repoName)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    String message = this.getMessage(response);
                    throw new ClientException(message,response.getStatusCode().value());
                }))
                .body(new ParameterizedTypeReference<>() {
                });
        Branch[] branches = branchesInfo.stream().map(b -> {
            Map<String,String> lastCommitInfo = (Map<String, String>) b.get("commit");
            return new Branch((String) b.get("name"),lastCommitInfo.get("sha"));
        }).toArray(Branch[]::new);

        return branches;
    }

    private String getMessage(ClientHttpResponse response) throws IOException {
        Map<String,Object> errMap = mapper.readValue(response.getBody(),Map.class);
        String message = (String) errMap.getOrDefault("message", response.getStatusText());
        return message;
    }

}
