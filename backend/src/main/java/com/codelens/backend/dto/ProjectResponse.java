package com.codelens.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class ProjectResponse {
    private Long id;
    private String name;
    private String url;
    private String description;
}
