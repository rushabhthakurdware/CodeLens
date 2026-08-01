package com.codelens.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.codelens.backend.model.FileMetadata;
import com.codelens.backend.model.Project;
import com.codelens.backend.repository.ProjectRepository;
import com.codelens.backend.repository.FileMetadataRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    public Project saveProject(String name, String url, String desc) {
        return projectRepository.save(
            Project.builder()
                .name(name)
                .githubUrl(url)
                .description(desc)
                .build()
        );
    }

    /**
     * Updated method signature to match the exact naming convention called by ProjectController
     */
    public void saveFileMetadataWithSummaryAndTokens(
            Project project, 
            String fileName, 
            String path, 
            String hash, 
            String summary, 
            long tokenConsumed, 
            long tokenSaved) {
            
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
                record.setTokensConsumed(tokenConsumed);
                record.setTokensSaved(tokenSaved);
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

        // 5. Success case: Build the entry completely BEFORE saving to database (Fixes the double-save efficiency leak)
        FileMetadata metadata = FileMetadata.builder()
                .project(project)
                .fileName(fileName)
                .filePath(path)
                .hash(hash)
                .summary(summary)
                .tokensConsumed(tokenConsumed) // Map directly into your builder pipeline
                .tokensSaved(tokenSaved)       // Map directly into your builder pipeline
                .lastAnalyzed(LocalDateTime.now())
                .build();

        fileMetadataRepository.save(metadata);
        System.out.println(">>> Stored metadata for " + fileName + " [Consumed: " + tokenConsumed + ", Saved: " + tokenSaved + "]");
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<FileMetadata> getFilesByProjectId(Long projectId) {
        return fileMetadataRepository.findByProjectId(projectId);
    }

    /**
     * Essential data tracking aggregation loop requested by ProjectController's analytics API
     */
    public List<FileMetadata> getAllFilesMetadata() {
        return fileMetadataRepository.findAll();
    }

    public boolean isAlreadyAnalyzed(String hash) {
        return fileMetadataRepository.findByHash(hash)
                .map(m -> m.getSummary() != null && !m.getSummary().equals("Analysis failed."))
                .orElse(false);
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
        System.out.println(">>> Successfully deleted Project ID: " + id + " and its metadata.");
    }

    public String getExistingSummaryByHash(String hash) {
        return fileMetadataRepository.findFirstByHash(hash)
                .map(metadata -> metadata.getSummary())
                .orElse("AI Analysis cached, but summary details were unavailable.");
    }
}