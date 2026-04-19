package com.codelens.backend.controller;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.io.File;
import com.codelens.backend.service.FileWalkerService;
import com.codelens.backend.service.AiAnalysisService;
import com.codelens.backend.model.FileMetadata;
import com.codelens.backend.dto.FileMetadataResponse; // You'll create this
import com.codelens.backend.dto.ProjectRequest;
import com.codelens.backend.dto.ProjectResponse;
import com.codelens.backend.model.Project;
import com.codelens.backend.service.ProjectService;
import com.codelens.backend.service.GitClonerService;
import com.codelens.utils.HashUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final GitClonerService gitClonerService;
    private final FileWalkerService fileWalkerService;
    private final AiAnalysisService aiAnalysisService;
    // REMOVED: FileMetadataRepository (talk to ProjectService instead)

    /**
     * GET Files for a specific project
     * Uses DTO to prevent Infinite JSON Recursion
     */
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

        java.nio.file.Path rootPath = java.nio.file.Paths.get("temp-repos", "project-" + savedProject.getId())
                .toAbsolutePath().normalize();
        File rootDir = rootPath.toFile();

        try {
            gitClonerService.cloneAndAnalyze(savedProject.getGithubUrl(), savedProject.getId());
            List<File> files = fileWalkerService.getInterestingFiles(rootDir);

            for (File file : files) {
                String hash = HashUtils.calculateHash(file);
                
                // Use the service to check for existing metadata
                if (projectService.isAlreadyAnalyzed(hash)) {
                    System.out.println(">>> skipping AI call for " + file.getName() + " (Already Analyzed)");
                } else {
                    System.out.println(">>> Calling AI for: " + file.getName());
                    String summary = aiAnalysisService.analyzeFile(file);
                    
                    projectService.saveFileMetadataWithSummary(
                            savedProject,
                            file.getName(),
                            file.getAbsolutePath(),
                            hash,
                            summary);

                    // Throttle for Free Tier API
                    try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing project " + savedProject.getId() + ": " + e.getMessage());
            throw new RuntimeException("Ingestion failed", e);
        } finally {
            if (rootDir.exists()) {
                deleteDirectory(rootDir);
                System.out.println(">>> Cleaned up workspace for Project ID: " + savedProject.getId());
            }
        }

        return ProjectResponse.builder()
                .id(savedProject.getId())
                .name(savedProject.getName())
                .url(savedProject.getGithubUrl())
                .description(savedProject.getDescription())
                .build();
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}