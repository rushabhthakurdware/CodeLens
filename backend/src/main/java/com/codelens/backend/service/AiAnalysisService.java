package com.codelens.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@Service
public class AiAnalysisService {
    private final RestTemplate restTemplate;
    private final String AI_SERVICE_URL = "http://localhost:8000/analyze";

    public AiAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Overloaded method to process raw content fetched straight from GitHub API.
     * This avoids reading/writing anything to your local hard drive.
     */
    public String analyzeFileContent(String content, String filename) {
        try {
            if (content == null || content.trim().isEmpty()) {
                return "Empty file content - no analysis needed.";
            }

            // 1. Package the network payload for your Python node
            Map<String, String> request = Map.of(
                "file_name", filename, 
                "code", content
            );

            // 2. Dispatch a POST request to your Python backend
            Map<String, String> response = restTemplate.postForObject(AI_SERVICE_URL, request, Map.class);
            
            // 3. Extract matching map responses safely
            if (response != null && response.containsKey("analysis")) {
                return response.get("analysis");
            } else if (response != null && response.containsKey("summary")) {
                // Defensive backup check in case your Python key returns "summary" instead
                return response.get("summary");
            } else {
                System.err.println("❌ Python node returned mismatched structure: " + response);
                return "AI Error: Unexpected structural response mapping.";
            }

        } catch (Exception e) {
            System.err.println("❌ Remote AI Pipeline Ingestion Failure for " + filename + ": " + e.getMessage());
            return "Analysis processing node failed.";
        }
    }

    /**
     * Original method tracking physical system workspace environments.
     */
    public String analyzeFile(File file) {
        try {
            String content = Files.readString(file.toPath());
            return analyzeFileContent(content, file.getName()); // Forward execution payload cleanly
        } catch (Exception e) {
            System.err.println("❌ Local Disk Reading Failure: " + e.getMessage());
            return "Analysis failed.";
        }
    }
}