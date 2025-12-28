package org.mailosz.githubviewer;

public record Branch(
        String name,
        String lastCommitSha
) {}
