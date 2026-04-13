package com.rentoki.umafx.util;

import com.rentoki.umafx.enums.MediaResources;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class ImageStorage {
    private static final Path STORAGE_PATH = Path.of(System.getProperty("user.home"), "Documents", "UmaFX", "storage", "images");

    static {
        try {
            Files.createDirectories(STORAGE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String save(Image image) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            if (bufferedImage == null) {
                return null;
            }

            String hash = hashBytes(bufferedImage);
            Path imagePath = STORAGE_PATH.resolve(hash + ".jpg");

            if (!Files.exists(imagePath)) {
                BufferedImage rgbImage = new BufferedImage(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );
                rgbImage.createGraphics().drawImage(bufferedImage, 0, 0, null);

                ImageIO.write(rgbImage, "jpg", imagePath.toFile());
            }

            return hash;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image loadImage(String hash) {
        Path imagePath = STORAGE_PATH.resolve(hash + ".jpg");
        if (Files.exists(imagePath)) {
            return new Image(imagePath.toUri().toString());
        }

        return MediaResources.FALLBACK_ALBUM_ART.getImage();
    }

    private static String hashBytes(BufferedImage bufferedImage) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
        for (int pixel : pixels) {
            buffer.putInt(pixel);
        }

        byte[] hash = digest.digest(buffer.array());

        return HexFormat.of().formatHex(hash);
    }
}
