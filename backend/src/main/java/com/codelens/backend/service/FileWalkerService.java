package com.codelens.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.springframework.stereotype.Service;

@Service
public class FileWalkerService {

    public List<File> getInterestingFiles(File rootDir, List<String> userSelectedExts) {
        List<File> result = new ArrayList<>();
        
        // Use user selections, or default to a safe list if null/empty
        List<String> filters = (userSelectedExts == null || userSelectedExts.isEmpty()) 
                               ? List.of("java", "py", "js", "jsx", "ts", "tsx") 
                               : userSelectedExts;

        if (rootDir.exists() && rootDir.isDirectory()) {
            scan(rootDir, result, filters);
        }
        return result;
    }

    private void scan(File file, List<File> fileList, List<String> filters) {
        File[] children = file.listFiles();
        if (children == null) return;

        for (File child : children) {
            if (child.isDirectory()) {
                // Skip heavy/system folders
                if (!isSystemDirectory(child.getName())) {
                    scan(child, fileList, filters);
                }
            } else {
                if (isUserRequestedFile(child.getName(), filters)) {
                    fileList.add(child);
                }
            }
        }
    }

    private boolean isSystemDirectory(String name) {
        return name.equals(".git") || 
               name.equals("node_modules") || 
               name.equals("target") || 
               name.equals(".idea") ||
               name.equals("build") ||
               name.equals("dist") ||
               name.equals(".venv");
    }

    private boolean isUserRequestedFile(String fileName, List<String> filters) {
        String name = fileName.toLowerCase();
        // Check if the file ends with any of the selected extensions (e.g., ".java")
        return filters.stream().anyMatch(ext -> name.endsWith("." + ext.toLowerCase()));
    }
}