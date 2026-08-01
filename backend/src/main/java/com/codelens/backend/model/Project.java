package com.codelens.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor   // Required by JPA
@AllArgsConstructor  // Required by @Builder when @NoArgsConstructor is present
@Builder            // Uncomment this!
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "github_url")
    private String githubUrl; 

    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
private List<FileMetadata> fileMetadata;
}