package com.codelens.backend.repository;

import com.codelens.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface  ProjectRepository extends JpaRepository<Project,Long> {
    
}
