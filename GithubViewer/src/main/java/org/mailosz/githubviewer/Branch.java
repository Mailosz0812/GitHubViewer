package org.mailosz.githubviewer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record Branch(
        String name,
        @JsonUnwrapped
        Commit commit
) {
    private record Commit(
            @JsonProperty("sha")
            String lastCommitSha
    ){}
}