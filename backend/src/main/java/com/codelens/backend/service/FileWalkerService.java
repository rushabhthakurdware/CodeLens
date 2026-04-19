package com.codelens.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;
import org.springframework.stereotype.Service;

@Service
public class FileWalkerService {
    
    public List<File> getInterestingFiles(File rootDirectory) {
        List<File> result = new ArrayList<>();
        scan(rootDirectory, result);
        return result;
    }
    private void scan(File file , List<File> fileList){
        File[]children = file.listFiles();

        if(children == null ) return ;

        for(File child :children){
            if(child.isDirectory()){
               if (!child.getName().equals(".git") && 
                !child.getName().equals("node_modules") && 
                !child.getName().equals("target") && 
                !child.getName().equals(".idea") &&
                !child.getName().equals("build")) {
                
                scan(child, fileList);
            }
            }else{
                if(isIntrestingFile(child.getName())){
                    fileList.add(child);
                }
            }
        }

    }
    private boolean isIntrestingFile(String fileName){
        String name = fileName.toLowerCase();
        return name.endsWith(".java")
            ||name.endsWith(".jsx")
            ||name.endsWith(".py")
            ||name.endsWith(".js")
            ||name.endsWith(".cpp")
            ||name.endsWith(".c")
            ||name.endsWith(".cs")
            ||name.endsWith(".xml")
            ||name.endsWith(".properties")
            ||name.endsWith(".jsx")
            ||name.endsWith(".ts")
            ||name.endsWith(".tsx")
            ||name.endsWith(".go")
            ||name.endsWith(".rb")
            ||name.endsWith(".php")
            ||name.endsWith(".swift")
            ||name.endsWith(".kt")
            ||name.endsWith(".kts")
            ||name.endsWith(".scala")
            ||name.endsWith(".rs")
            ||name.endsWith(".dart")
            ;
    }

}
