package com.codelens.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
// import lombok.Builder;
import lombok.Data;
@Data
// @Builder

public class ProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;
    @NotBlank(message = "Github URL is required")
    @Pattern(regexp = "^(https?://)?(www\\.)?github\\.com/.+$", message = "Invalid GitHub URL")
    private String url;
    private String description;
}
