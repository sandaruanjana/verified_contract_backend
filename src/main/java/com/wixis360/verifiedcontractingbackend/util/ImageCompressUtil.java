package com.wixis360.verifiedcontractingbackend.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageCompressUtil {
    private ImageCompressUtil() { throw new IllegalStateException("Image compress util class");}

    public static byte[] compressImage(MultipartFile multipartFile) {
        float quality = 0.5f;
        String imageName = multipartFile.getOriginalFilename();
        String imageExtension = imageName.substring(imageName.lastIndexOf(".") + 1);

        BufferedImage bufferedImage = null;

        if (imageExtension.equalsIgnoreCase("jpg") || imageExtension.equalsIgnoreCase("jpeg")) {
            try {
                bufferedImage = ImageIO.read(multipartFile.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
                // jpg needs BufferedImage.TYPE_INT_RGB
                // png needs BufferedImage.TYPE_INT_ARGB

                // create a blank, RGB, same width and height
                bufferedImage = new BufferedImage(
                        originalImage.getWidth(),
                        originalImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                // draw a white background and puts the originalImage on it.
                bufferedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Returns an Iterator containing all currently registered ImageWriters that claim to be able to encode the named format.
        // You don't have to register one yourself; some are provided.
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Check the api value that suites your needs.
        // A compression quality setting of 0.0 is most generically interpreted as "high compression is important,"
        // while a setting of 1.0 is most generically interpreted as "high image quality is important."
        imageWriteParam.setCompressionQuality(quality);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // MemoryCacheImageOutputStream: An implementation of ImageOutputStream that writes its output to a regular
        // OutputStream, i.e. the ByteArrayOutputStream.
        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(baos);
        // Sets the destination to the given ImageOutputStream or other Object.
        imageWriter.setOutput(imageOutputStream);

        IIOImage image = new IIOImage(bufferedImage, null, null);

        try {
            imageWriter.write(null, image, imageWriteParam);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            imageWriter.dispose();
        }

        return baos.toByteArray();
    }

    public static byte[] compressImage(File file) {
        float quality = 0.5f;
        String imageName = file.getName();
        String imageExtension = imageName.substring(imageName.lastIndexOf(".") + 1);

        BufferedImage bufferedImage = null;

        if (imageExtension.equalsIgnoreCase("jpg") || imageExtension.equalsIgnoreCase("jpeg")) {
            try {
                bufferedImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedImage originalImage = ImageIO.read(file);
                // jpg needs BufferedImage.TYPE_INT_RGB
                // png needs BufferedImage.TYPE_INT_ARGB

                // create a blank, RGB, same width and height
                bufferedImage = new BufferedImage(
                        originalImage.getWidth(),
                        originalImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                // draw a white background and puts the originalImage on it.
                bufferedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Returns an Iterator containing all currently registered ImageWriters that claim to be able to encode the named format.
        // You don't have to register one yourself; some are provided.
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Check the api value that suites your needs.
        // A compression quality setting of 0.0 is most generically interpreted as "high compression is important,"
        // while a setting of 1.0 is most generically interpreted as "high image quality is important."
        imageWriteParam.setCompressionQuality(quality);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // MemoryCacheImageOutputStream: An implementation of ImageOutputStream that writes its output to a regular
        // OutputStream, i.e. the ByteArrayOutputStream.
        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(baos);
        // Sets the destination to the given ImageOutputStream or other Object.
        imageWriter.setOutput(imageOutputStream);

        IIOImage image = new IIOImage(bufferedImage, null, null);

        try {
            imageWriter.write(null, image, imageWriteParam);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            imageWriter.dispose();
        }

        return baos.toByteArray();
    }
}
