package com.codelens.backend.controller;

import java.util.stream.Collectors;
import java.util.List;
import com.codelens.backend.dto.FileMetadataResponse;
import com.codelens.backend.dto.ProjectRequest;
import com.codelens.backend.dto.ProjectResponse;
import com.codelens.backend.model.Project;
import com.codelens.backend.service.ProjectService;
import com.codelens.backend.utils.GitHubUrlParser; // Singular .util
import com.codelens.backend.service.AiAnalysisService;
import com.codelens.backend.service.GitHubApiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AiAnalysisService aiAnalysisService;

    @Autowired
    private GitHubApiService gitHubApiService;

    @GetMapping("/{id}/files")
    public List<FileMetadataResponse> getProjectFiles(@PathVariable Long id) {
        return projectService.getFilesByProjectId(id).stream()
                .map(file -> FileMetadataResponse.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .filePath(file.getFilePath())
                        .hash(file.getHash())
                        .summary(file.getSummary())
                        .lastAnalyzed(file.getLastAnalyzed())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getAllProjects().stream()
                .map(project -> ProjectResponse.builder()
                        .id(project.getId())
                        .name(project.getName())
                        .url(project.getGithubUrl())
                        .description(project.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public ProjectResponse addProject(@Valid @RequestBody ProjectRequest projectRequest) {
        Project savedProject = projectService.saveProject(
                projectRequest.getName(),
                projectRequest.getUrl(),
                projectRequest.getDescription());

        try {
            GitHubUrlParser.RepoDetails repoDetails = GitHubUrlParser.parse(savedProject.getGithubUrl());

            List<String> filters = projectRequest.getAllowedExtensions();
            if (filters == null || filters.isEmpty()) {
                filters = List.of("java", "py", "js", "jsx", "ts", "tsx");
            }

            System.out.println(">>> Querying remote file mapping layout for workspace...");
            List<GitHubApiService.RemoteFileItem> remoteFiles = gitHubApiService.fetchRepositoryTree(
                    repoDetails.getOwner(),
                    repoDetails.getRepo(),
                    projectRequest.getGithubToken(),
                    filters);

            System.out.println(">>> Filtered scan complete. Target matches: " + remoteFiles.size());

            for (GitHubApiService.RemoteFileItem remoteFile : remoteFiles) {
                String fileHash = remoteFile.sha;

                if (projectService.isAlreadyAnalyzed(fileHash)) {
                    System.out.println(">>> [CACHE HIT] Reusing summary for: " + remoteFile.path);
                    String cachedSummary = projectService.getExistingSummaryByHash(fileHash);

                    // Optimized baseline: estimate tokens saved using a 400-token standard file size baseline
                    // to prevent making an expensive network call to GitHub for a file we are skipping!
                    long estimatedSavedTokens = (cachedSummary != null) ? (cachedSummary.length() / 4) + 250 : 300;

                    // Fixed Method Name to match ProjectService signature
                    projectService.saveFileMetadataWithSummaryAndTokens(
                            savedProject,
                            remoteFile.path,
                            remoteFile.downloadUrl,
                            fileHash,
                            cachedSummary,
                            0L,
                            estimatedSavedTokens
                    );
                } else {
                    System.out.println(">>> [CACHE MISS] Fetching target content: " + remoteFile.path);
                    String codeContent = gitHubApiService.fetchRawFileContent(remoteFile.downloadUrl,
                            projectRequest.getGithubToken());

                    if (codeContent != null) {
                        System.out.println(">>> Sending code blocks to Python processing node...");
                        String summary = aiAnalysisService.analyzeFileContent(codeContent, remoteFile.path);

                        long actualTokens = (codeContent.length() / 4) + (summary.length() / 4) + 50;
                        
                        // Fixed Method Name to match ProjectService signature
                        projectService.saveFileMetadataWithSummaryAndTokens(
                                savedProject,
                                remoteFile.path,
                                remoteFile.downloadUrl,
                                fileHash,
                                summary,
                                actualTokens,
                                0L);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Project ingestion processing crashed on ID: " + savedProject.getId() + " - " + e.getMessage());
            throw new RuntimeException("API parsing Pipeline exception occurring", e);
        }

        return ProjectResponse.builder()
                .id(savedProject.getId())
                .name(savedProject.getName())
                .url(savedProject.getGithubUrl())
                .description(savedProject.getDescription())
                .build();
    }

    @GetMapping("/analytics/tokens")
    public Map<String, Long> getTokenAnalytics() {
        long totalConsumed = projectService.getAllFilesMetadata().stream().mapToLong(f -> f.getTokensConsumed()).sum();
        long totalSaved = projectService.getAllFilesMetadata().stream().mapToLong(f -> f.getTokensSaved()).sum();
        
        return Map.of(
            "tokensConsumed", totalConsumed,
            "tokensSaved", totalSaved
        );
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}