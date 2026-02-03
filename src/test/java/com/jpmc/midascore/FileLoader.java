package com.jpmc.midascore;

import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.InputStream;

@Component
public class FileLoader {

    public String[] loadStrings(String path) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(path);

            // ✅ VERY IMPORTANT: file not found
            if (inputStream == null) {
                return new String[0];
            }

            String fileText = IOUtils.toString(inputStream, "UTF-8");

            // ✅ handle empty file
            if (fileText.isBlank()) {
                return new String[0];
            }

            return fileText.split(System.lineSeparator());

        } catch (Exception e) {
            // ✅ NEVER return null
            return new String[0];
        }
    }
}

