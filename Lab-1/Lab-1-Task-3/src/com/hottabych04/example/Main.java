package com.hottabych04.example;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("encrypt") && !args[0].equals("decrypt"))) {
            System.out.println("Usage: java FileEncryption <encrypt|decrypt>");
            return;
        }

        String keyPath = "Lab-1/Lab-1-Task-3/key.txt";
        String inputPath = "Lab-1/Lab-1-Task-3/input.txt";
        String encryptedPath = "Lab-1/Lab-1-Task-3/encrypted.txt";
        String decryptedPath = "Lab-1/Lab-1-Task-3/decrypted.txt";

        try {
            byte[] encryptionKey = loadKey(keyPath);
            byte[] key = args[0].equals("encrypt") ? encryptionKey : createDecryptionKey(encryptionKey);
            if (args[0].equals("encrypt")) {
                processFile(inputPath, encryptedPath, key);
            } else {
                processFile(encryptedPath, decryptedPath, key);
            }
            System.out.println("Operation completed successfully");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static byte[] loadKey(String keyFile) throws IOException {
        byte[] key = new byte[256];
        try (FileInputStream fis = new FileInputStream(keyFile)) {
            if (fis.read(key) != 256) {
                throw new IOException("Файл ключа должен содержать ровно 256 байт.");
            }
        }
        return key;
    }

    private static byte[] createDecryptionKey(byte[] encryptionKey) {
        byte[] decryptionKey = new byte[256];
        for (int i = 0; i < 256; i++) {
            decryptionKey[encryptionKey[i] & 0xFF] = (byte) i;
        }
        return decryptionKey;
    }

    private static void processFile(String inputFile, String outputFile, byte[] key) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            int b;
            while ((b = fis.read()) != -1) {
                fos.write(key[b & 0xFF]);
            }
        }
    }
}
