package dev.ruben.cookandshare.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AvatarStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
    };
    private static final byte[] JPEG_SIGNATURE = {
            (byte) 0xff, (byte) 0xd8, (byte) 0xff
    };

    private final Path imageDirectory;

    public AvatarStorageService(
            @Value("${app.upload.avatar-directory:src/main/resources/static/images}") String imageDirectory) {
        this.imageDirectory = Path.of(imageDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw invalidImage();
        }

        String extension = getAllowedExtension(file.getOriginalFilename());
        try {
            byte[] contents = file.getBytes();
            if (!hasMatchingSignature(contents, extension)) {
                throw invalidImage();
            }

            Files.createDirectories(imageDirectory);
            String filename = UUID.randomUUID() + extension;
            Files.write(imageDirectory.resolve(filename), contents);
            return "/images/" + filename;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not store avatar image", exception);
        }
    }

    private String getAllowedExtension(String originalFilename) {
        if (originalFilename == null) {
            throw invalidImage();
        }
        String filename = Path.of(originalFilename).getFileName().toString();
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0) {
            throw invalidImage();
        }
        String extension = filename.substring(extensionIndex).toLowerCase(Locale.ROOT);
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
            throw invalidImage();
        }
        return extension;
    }

    private boolean hasMatchingSignature(byte[] contents, String extension) {
        if (extension.equals(".png")) {
            return startsWith(contents, PNG_SIGNATURE);
        }
        return startsWith(contents, JPEG_SIGNATURE);
    }

    private boolean startsWith(byte[] contents, byte[] signature) {
        if (contents.length < signature.length) {
            return false;
        }
        for (int index = 0; index < signature.length; index++) {
            if (contents[index] != signature[index]) {
                return false;
            }
        }
        return true;
    }

    private ResponseStatusException invalidImage() {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Avatar must be a JPG, JPEG or PNG image no larger than 5 MB");
    }
}
