package org.mailosz.githubviewer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Repository(
        @JsonProperty("name")
        String repoName,

        @JsonProperty("owner")
        RepoOwner owner,

        @JsonProperty(value = "fork", access = JsonProperty.Access.WRITE_ONLY)
        boolean isFork,

        @JsonProperty(value = "branches", access = JsonProperty.Access.READ_ONLY)
        Branch[] branches
) {
}
