package io.hs.bex.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileUtils
{
    public static Resource getUserFileResource( String fileName, String ext, String content ) throws IOException 
    {
        Path tempFile = Files.createTempFile( fileName, "."+ext );
        Files.write(tempFile, content.getBytes());
        File file = tempFile.toFile();
        
        return new FileSystemResource(file);
    }
}
