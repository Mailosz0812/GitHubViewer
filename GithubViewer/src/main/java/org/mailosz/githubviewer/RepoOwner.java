package org.mailosz.githubviewer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepoOwner(
        @JsonProperty("login")
        String ownerLogin
) {
}
