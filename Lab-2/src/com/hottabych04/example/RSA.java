package com.hottabych04.example;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RSA {
    private BigInteger n, d, e;
    private int bitLength = 1024;

    public RSA() {
        generateKeys();
    }

    private void generateKeys() {
        SecureRandom r = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, r);
        BigInteger q = BigInteger.probablePrime(bitLength, r);
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.valueOf(65537);
        d = e.modInverse(phi);
    }

    // Шифрование сообщения с правильной обработкой байтов
    public byte[] encrypt(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        // Преобразуем байты в положительное число
        BigInteger m = new BigInteger(1, messageBytes);
        // Проверяем, что сообщение меньше модуля n
        if (m.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Сообщение слишком длинное");
        }
        BigInteger c = m.modPow(e, n);
        return c.toByteArray();
    }

    // Расшифрование сообщения с правильной обработкой байтов
    public String decrypt(byte[] encryptedMessage) {
        BigInteger c = new BigInteger(encryptedMessage);
        BigInteger m = c.modPow(d, n);

        // Получаем байты из BigInteger, убирая ведущий нуль если он есть
        byte[] messageBytes = m.toByteArray();
        if (messageBytes[0] == 0) {
            messageBytes = Arrays.copyOfRange(messageBytes, 1, messageBytes.length);
        }

        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    public BigInteger[] getPublicKey() {
        return new BigInteger[]{e, n};
    }

    private BigInteger[] getPrivateKey() {
        return new BigInteger[]{d, n};
    }

    public static void main(String[] args) {
        try {
            RSA rsa = new RSA();

            // Тестовое сообщение
            String message = "Привет, мир!";
            System.out.println("Исходное сообщение: " + message);

            // Шифрование
            byte[] encrypted = rsa.encrypt(message);
            System.out.println("Зашифрованное сообщение (hex): " + bytesToHex(encrypted));

            // Расшифрование
            String decrypted = rsa.decrypt(encrypted);
            System.out.println("Расшифрованное сообщение: " + decrypted);

        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Вспомогательный метод для вывода байтов в шестнадцатеричном формате
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}