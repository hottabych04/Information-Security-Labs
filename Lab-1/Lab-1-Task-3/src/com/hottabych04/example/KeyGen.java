package com.hottabych04.example;

import java.io.*;
import java.util.Random;

public class KeyGen {
    public static void generateKey(String filename) throws IOException {
        // Create array with values 0-255
        byte[] key = new byte[256];
        for (int i = 0; i < 256; i++) {
            key[i] = (byte) i;
        }

        // Shuffle array using Fisher-Yates algorithm
        Random rnd = new Random();
        for (int i = key.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Swap elements
            byte temp = key[index];
            key[index] = key[i];
            key[i] = temp;
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(key);
        }
    }

    public static boolean verifyKey(String filename) throws IOException {
        // Read key file
        byte[] key = new byte[256];
        try (FileInputStream fis = new FileInputStream(filename)) {
            if (fis.read(key) != 256) {
                System.out.println("Error: Key file must be exactly 256 bytes");
                return false;
            }
        }

        // Check if each value appears exactly once
        boolean[] found = new boolean[256];
        for (byte b : key) {
            int index = b & 0xFF;  // Convert to unsigned
            if (found[index]) {
                System.out.println("Error: Value " + index + " appears multiple times");
                return false;
            }
            found[index] = true;
        }

        // Check if any values are missing
        for (int i = 0; i < 256; i++) {
            if (!found[i]) {
                System.out.println("Error: Value " + i + " is missing");
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            String filename = "Lab-1/Lab-1-Task-3/key.txt";
            generateKey(filename);

            if (verifyKey(filename)) {
                System.out.println("Key file generated and verified successfully!");
                System.out.println("Key file '" + filename + "' is ready to use.");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
