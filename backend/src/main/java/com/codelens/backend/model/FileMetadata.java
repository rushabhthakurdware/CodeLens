package com.codelens.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    private String hash;

    @Column(columnDefinition = "TEXT")
    private String summary;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDateTime lastAnalyzed;
    @Column(name = "tokens_consumed")
    private Long tokensConsumed = 0L;

    @Column(name = "tokens_saved")
    private Long tokensSaved = 0L;

    // If you aren't using Lombok's @Data, add the standard getters/setters for
    // these two fields here!

}
