package com.codelens.backend.service;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class GitHubApiService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // DTO to hold metadata from GitHub without touching the local disk
    public static class RemoteFileItem {
        public String path;
        public String sha; // GitHub's precomputed SHA hash of the file content
        public String downloadUrl;
    }

    public List<RemoteFileItem> fetchRepositoryTree(String owner, String repo, String token, List<String> allowedExtensions) {
        List<RemoteFileItem> filteredFiles = new ArrayList<>();
        // GitHub API endpoint for recursive tree lookup (defaulting to main branch)
        String url = String.format("https://api.github.com/repos/%s/%s/git/trees/main?recursive=1", owner, repo);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");
            if (token != null && !token.trim().isEmpty()) {
                headers.set("Authorization", "token " + token);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode treeNode = root.get("tree");

            if (treeNode != null && treeNode.isArray()) {
                for (JsonNode item : treeNode) {
                    String type = item.get("type").asText();
                    String path = item.get("path").asText();
                    
                    // Only process files that match the requested extensions
                    if ("blob".equals(type) && matchesExtensions(path, allowedExtensions)) {
                        RemoteFileItem remoteFile = new RemoteFileItem();
                        remoteFile.path = path;
                        remoteFile.sha = item.get("sha").asText();
                        remoteFile.downloadUrl = String.format("https://raw.githubusercontent.com/%s/%s/main/%s", owner, repo, path);
                        filteredFiles.add(remoteFile);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to query GitHub Tree API: " + e.getMessage());
            // Fallback strategy: try with 'master' branch if 'main' fails
            if (url.contains("main")) {
                return fetchRepositoryTree(owner, repo, token, allowedExtensions.toString().contains("master") ? allowedExtensions : allowedExtensions);
            }
        }
        return filteredFiles;
    }

    public String fetchRawFileContent(String downloadUrl, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null && !token.trim().isEmpty()) {
                headers.set("Authorization", "token " + token);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("❌ Error fetching raw content: " + e.getMessage());
            return null;
        }
    }

    private boolean matchesExtensions(String path, List<String> filters) {
        if (filters == null || filters.isEmpty()) return true; 
        String lowercasePath = path.toLowerCase();
        return filters.stream().anyMatch(ext -> lowercasePath.endsWith("." + ext.toLowerCase()));
    }
}