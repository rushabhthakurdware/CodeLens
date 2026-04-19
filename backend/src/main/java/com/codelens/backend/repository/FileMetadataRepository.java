package com.codelens.backend.repository;

import com.codelens.backend.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findByHash(String hash);
    List<FileMetadata> findByProjectId(Long projectId); // Add this line
}
