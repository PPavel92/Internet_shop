package com.example.shop.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void writeToFile(String filename, String content) {
        try {
            File directory = new File("receipts");
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}