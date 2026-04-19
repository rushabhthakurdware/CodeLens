package com.codelens.backend.service;

import org.springframework.stereotype.Service;
import com.codelens.backend.model.FileMetadata;
import com.codelens.backend.model.Project;
import com.codelens.backend.repository.ProjectRepository;
import com.codelens.backend.repository.FileMetadataRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final FileMetadataRepository fileMetadataRepository;

    public Project saveProject(String name, String url, String desc) {
        return projectRepository.save(
            Project.builder()
                .name(name)
                .githubUrl(url)
                .description(desc)
                .build()
        );
    }

   public void saveFileMetadataWithSummary(Project project, String fileName, String path, String hash, String summary) {
    // 1. Identify "Bad" summaries (null, empty, or the error string from your service)
    boolean isInvalid = (summary == null || summary.isBlank() || summary.equalsIgnoreCase("Analysis failed."));

    Optional<FileMetadata> existing = fileMetadataRepository.findByHash(hash);

    if (existing.isPresent()) {
        FileMetadata record = existing.get();

        // 2. If a valid summary already exists in DB, don't touch it.
        if (record.getSummary() != null && !record.getSummary().isBlank() && !record.getSummary().equals("Analysis failed.")) {
            System.out.println(">>> Skipping " + fileName + " (Hash + Valid Summary already exists)");
            return;
        }

        // 3. If hash exists but summary was null/failed, and we have a GOOD one now: Update it.
        if (!isInvalid) {
            record.setSummary(summary);
            record.setLastAnalyzed(LocalDateTime.now());
            fileMetadataRepository.save(record);
            System.out.println(">>> Updated previously missing summary for: " + fileName);
        } else {
            System.out.println(">>> AI failed again for: " + fileName + ", not overwriting with null.");
        }
        return;
    }

    // 4. If it's a new file but AI failed, skip saving to DB so we can try again next time.
    if (isInvalid) {
        System.out.println(">>> AI failed for NEW file: " + fileName + ", skipping save to allow retry later.");
        return;
    }

    // 5. Success case: New file + Valid summary
    FileMetadata metadata = FileMetadata.builder()
            .project(project)
            .fileName(fileName)
            .filePath(path)
            .hash(hash)
            .summary(summary)
            .lastAnalyzed(LocalDateTime.now())
            .build();

    fileMetadataRepository.save(metadata);
    System.out.println(">>> Saved fresh record with AI Summary for: " + fileName);
}

    // // This is your original method (Keep it for general use)
    // public void saveFileMetadata(Project project, String fileName, String path, String hash) {
    //     if (fileMetadataRepository.findByHash(hash).isPresent()) {
    //         System.out.println(">>> Skipping " + fileName + " (Hash match found)");
    //         return; 
    //     }

    //     FileMetadata metadata = FileMetadata.builder()
    //         .project(project)
    //         .fileName(fileName)
    //         .filePath(path)
    //         .hash(hash)
    //         .lastAnalyzed(LocalDateTime.now())
    //         .build();

    //     fileMetadataRepository.save(metadata);
    //     System.out.println(">>> Saved metadata for: " + fileName);
    // }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // // Add this to your ProjectService.java
    // public List<FileMetadata> getFilesByProjectId(Long projectId) {
    //     return fileMetadataRepository.findByProjectId(projectId);
    // }

    public List<FileMetadata> getFilesByProjectId(Long projectId) {
    return fileMetadataRepository.findByProjectId(projectId);
}

public boolean isAlreadyAnalyzed(String hash) {
    return fileMetadataRepository.findByHash(hash)
            .map(m -> m.getSummary() != null && !m.getSummary().equals("Analysis failed."))
            .orElse(false);
}
}