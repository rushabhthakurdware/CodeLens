package com.codelens.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.File;
import org.springframework.stereotype.Component;

@Component

public class HashUtils {
    public static  String calculateHash(File file) {
        try(FileInputStream fis = new FileInputStream(file)){
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[]byteArray = new byte[1024];
            int bytesCount = 0;
            
            while((bytesCount = fis.read(byteArray))!=-1){
                digest.update(byteArray , 0 , bytesCount);
            }

            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        }catch(Exception e){
            throw new RuntimeException("Could not calculate hash for " + file.getName());
        }
    }
}
