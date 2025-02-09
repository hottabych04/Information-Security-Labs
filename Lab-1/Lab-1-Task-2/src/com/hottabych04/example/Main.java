package com.hottabych04.example;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        String filename = "Lab-1/Lab-1-Task-2/file.txt";
        int[] frequency = new int[256];

        try (FileInputStream fis = new FileInputStream(filename)) {
            int byteRead;
            while ((byteRead = fis.read()) != -1) {
                frequency[byteRead]++;
            }

            // Print results
            System.out.println("Byte frequency in file: " + filename);
            for (int i = 0; i < 256; i++) {
                if (frequency[i] > 0) {
                    System.out.printf("Byte %d (0x%02X): %d times%n",
                            i, i, frequency[i]);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
