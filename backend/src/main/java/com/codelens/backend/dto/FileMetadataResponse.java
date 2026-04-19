package com.codelens.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FileMetadataResponse {
    private Long id;
    private String fileName;
    private String filePath;
    private String hash;
    private String summary;
    private LocalDateTime lastAnalyzed;
}