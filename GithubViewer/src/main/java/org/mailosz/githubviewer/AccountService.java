package org.mailosz.githubviewer;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AccountService {
    private final GithubClient githubClient;

    public AccountService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<Repository> getRepositories(String username){
        Repository[] repos = githubClient.getRepositories(username);

        return Arrays.stream(repos).filter(repository -> !repository.isFork())
                .map(repository -> {
                    Branch[] branches = githubClient.getRepoBranches(repository.repoName(),username);
                    return new Repository(
                            repository.repoName(),repository.owner(),
                            false, branches
                    );
                }).toList();
    }
}

