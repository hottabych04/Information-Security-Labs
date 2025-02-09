package com.hottabych04.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String filePath = "Lab-1/Lab-1-Task-1/veni_vidi_vici.doc";

        try {
            Path path = Paths.get(filePath);
            long fileSize = Files.size(path);

            System.out.println("Файл: " + filePath);
            System.out.println("Размер файла в байтах: " + fileSize);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }
}
