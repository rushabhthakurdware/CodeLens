package com.codelens.backend.service;

// import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Git;
import org.springframework.stereotype.Service;
import java.io.File;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;

@Service
public class GitClonerService {

    @Value("${codelens.clone.path}")
    private String cloneBasePath;

    public void cloneAndAnalyze(String repoUrl , Long projectId){
        File workDir = new File(cloneBasePath, "project-" + projectId);

        try(Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(workDir)
                .setCloneAllBranches(false) 
                .setDepth(1) // "Big Level" Tip
                .call())
                {
                    System.out.println("Cloned successfully to: " + workDir.getAbsolutePath());    
                }
        catch (Exception e){
            throw new RuntimeException("Global Cloner Error: " + e.getMessage());
        }
        
    }
}
