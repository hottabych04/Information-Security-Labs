package com.hottabych04.example;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class BMPSteganography {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java BMPSteganography [hide/extract] [input_bmp] [input_txt/output_txt]");
            return;
        }

        String mode = args[0];
        String bmpPath = args[1];
        String txtPath = args[2];

        try {
            if ("hide".equals(mode)) {
                hideInformation(bmpPath, txtPath);
            } else if ("extract".equals(mode)) {
                extractInformation(bmpPath, txtPath);
            } else {
                System.out.println("Invalid mode. Use 'hide' or 'extract'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void hideInformation(String bmpPath, String txtPath) throws IOException {
        // Read the BMP file
        File bmpFile = new File(bmpPath);
        BufferedImage image = ImageIO.read(bmpFile);

        // Read the text file
        byte[] textData = readFileBytes(txtPath);

        int width = image.getWidth();
        int height = image.getHeight();

        if (textData.length + 1 > width * height) { // +1 for EOF
            throw new IOException("Text file too large for this image");
        }

        int index = 0;

        // Hide the text data
        for (int y = 0; y < height && index < textData.length; y++) {
            for (int x = 0; x < width && index < textData.length; x++) {
                int pixel = image.getRGB(x, y);
                int newPixel = hideByteInPixel(pixel, textData[index]);
                image.setRGB(x, y, newPixel);
                index++;
            }
        }

        // Add EOF marker
        if (index < width * height) {
            int x = index % width;
            int y = index / width;
            int pixel = image.getRGB(x, y);
            int newPixel = hideByteInPixel(pixel, (byte)0xFF);
            image.setRGB(x, y, newPixel);
        }

        // Save the modified image
        String outputPath = "hidden_" + bmpPath;
        ImageIO.write(image, "bmp", new File(outputPath));
        System.out.println("Information hidden successfully in: " + outputPath);
    }

    public static void extractInformation(String bmpPath, String outputPath) throws IOException {
        // Read the BMP file
        File bmpFile = new File(bmpPath);
        BufferedImage image = ImageIO.read(bmpFile);

        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream extractedData = new ByteArrayOutputStream();

        // Extract the hidden data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                byte extractedByte = extractByteFromPixel(pixel);

                if (extractedByte == (byte)0xFF) { // EOF found
                    break;
                }

                extractedData.write(extractedByte);
            }
        }

        // Save the extracted data
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(extractedData.toByteArray());
        }
        System.out.println("Information extracted successfully to: " + outputPath);
    }

    private static byte[] readFileBytes(String path) throws IOException {
        File file = new File(path);
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
    }

    private static int hideByteInPixel(int pixel, byte hideByte) {
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        // Clear last 2 bits of each channel
        blue &= 0xFC;
        green &= 0xFC;
        red &= 0xFC;
        alpha &= 0xFC;

        // Hide 2 bits in each channel
        blue |= (hideByte >> 6) & 0x3;
        green |= (hideByte >> 4) & 0x3;
        red |= (hideByte >> 2) & 0x3;
        alpha |= hideByte & 0x3;

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static byte extractByteFromPixel(int pixel) {
        int alpha = (pixel >> 24) & 0x3;
        int red = (pixel >> 16) & 0x3;
        int green = (pixel >> 8) & 0x3;
        int blue = pixel & 0x3;

        return (byte) ((blue << 6) | (green << 4) | (red << 2) | alpha);
    }
}