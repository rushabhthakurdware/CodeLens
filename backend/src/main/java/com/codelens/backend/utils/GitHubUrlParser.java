package com.codelens.backend.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUrlParser {
    private static final Pattern PATTERN = Pattern.compile("github\\.com/([^/]+)/([^/]+)");

    public static RepoDetails parse(String url) {
        Matcher matcher = PATTERN.matcher(url);
        if (matcher.find()) {
            String owner = matcher.group(1);
            // Clean up trailing .git if present
            String repo = matcher.group(2).replace(".git", "");
            return new RepoDetails(owner, repo);
        }
        throw new IllegalArgumentException("Could not parse GitHub owner and repository from URL: " + url);
    }

    public static class RepoDetails {
        private final String owner;
        private final String repo;

        public RepoDetails(String owner, String repo) {
            this.owner = owner;
            this.repo = repo;
        }

        public String getOwner() { return owner; }
        public String getRepo() { return repo; }
    }
}