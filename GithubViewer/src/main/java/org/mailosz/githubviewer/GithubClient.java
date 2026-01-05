package org.mailosz.githubviewer;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GithubClient {
    private final RestClient restClient;

    public GithubClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Repository[] getRepositories(String username){
        String uri = "/users/" + username + "/repos";
        Repository[] repos = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new ClientException("Not Found",response.getStatusCode().value());
                })
                .body(Repository[].class);
        return repos;
    }

    public Branch[] getRepoBranches(String repoName,String username){
        Branch[] branches = restClient.get()
                .uri("/repos/{owner}/{repo}/branches",username,repoName)
                .retrieve()
                .onStatus(status -> status.value() == 404,((request, response) -> {
                    throw new ClientException("Not Found",response.getStatusCode().value());
                }))
                .body(Branch[].class);
        return branches;

    }
}
