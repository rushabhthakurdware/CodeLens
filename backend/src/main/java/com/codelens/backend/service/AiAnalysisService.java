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

    // public String analyzeFile(File file) {
    //     try {
    //         // 1. Read the actual code from the file
    //         String content = Files.readString(file.toPath());

    //         if (content.trim().isEmpty()) {
    //         return "Empty file - no analysis needed.";
    //     }
    //         // 2. Prepare the payload for Python
    //         Map<String, String> request = Map.of(
    //             "file_name", file.getName(),
    //             "code", content
    //         );

    //         // 3. POST to Python and get the summary
    //         Map<String, String> response = restTemplate.postForObject(AI_SERVICE_URL, request, Map.class);
            
    //         return response != null ? response.get("summary") : "No summary generated.";

    //     } catch (Exception e) {
    //         System.err.println("AI Analysis Error for " + file.getName() + ": " + e.getMessage());
    //         return "Analysis failed.";
    //     }
    // }
    public String analyzeFile(File file) {
    try {
        String content = Files.readString(file.toPath());
        if (content.trim().isEmpty()) return "Empty file.";

        Map<String, String> request = Map.of(
            "file_name", file.getName(), 
            "code", content
        );

        // Call Python
        Map<String, String> response = restTemplate.postForObject(AI_SERVICE_URL, request, Map.class);
        
        // --- FIX HERE: Change "summary" to "analysis" ---
        if (response != null && response.containsKey("analysis")) {
            return response.get("analysis");
        } else {
            System.err.println("❌ Python sent back: " + response);
            return "AI Error: Key mismatch";
        }

    } catch (Exception e) {
        System.err.println("❌ AI Analysis Error: " + e.getMessage());
        return "Analysis failed.";
    }
}
}