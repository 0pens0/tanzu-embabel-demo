package com.embabel.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for creating ZIP files from generated code.
 */
public class ZipUtility {
    
    /**
     * Creates a ZIP file containing the generated project files.
     * 
     * @param files Map of file paths to file contents
     * @param projectName Name of the project (used as root folder in ZIP)
     * @return ZIP file as byte array
     * @throws IOException if ZIP creation fails
     */
    public static byte[] createProjectZip(Map<String, String> files, String projectName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                String filePath = entry.getKey();
                String content = entry.getValue();
                
                // Create ZIP entry with project name as root folder
                ZipEntry zipEntry = new ZipEntry(projectName + "/" + filePath);
                zos.putNextEntry(zipEntry);
                
                // Write file content
                zos.write(content.getBytes("UTF-8"));
                zos.closeEntry();
            }
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Sanitizes project name for use in file system.
     * 
     * @param projectName Original project name
     * @return Sanitized project name safe for file system
     */
    public static String sanitizeProjectName(String projectName) {
        return projectName
            .toLowerCase()
            .replaceAll("[^a-z0-9\\-]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
}
