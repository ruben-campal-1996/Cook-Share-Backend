package dev.ruben.cookandshare.auth;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

class AvatarStorageServiceTest {

    private static final byte[] PNG_CONTENTS = {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0x01
    };
    private static final byte[] JPEG_CONTENTS = {
            (byte) 0xff, (byte) 0xd8, (byte) 0xff, 0x01
    };

    @TempDir
    Path imageDirectory;

    @Test
    void storesImageWithGeneratedNameAndReturnsPublicUrl() throws Exception {
        AvatarStorageService service = new AvatarStorageService(imageDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", PNG_CONTENTS);

        String avatarUrl = service.store(file);

        assertTrue(avatarUrl.startsWith("/images/"));
        assertEquals(".png", avatarUrl.substring(avatarUrl.lastIndexOf('.')));
        assertArrayEquals(PNG_CONTENTS, Files.readAllBytes(imageDirectory.resolve(
                avatarUrl.substring("/images/".length()))));
    }

    @Test
    void acceptsJpegImages() throws Exception {
        AvatarStorageService service = new AvatarStorageService(imageDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpeg", "image/jpeg", JPEG_CONTENTS);

        String avatarUrl = service.store(file);

        assertEquals(".jpeg", avatarUrl.substring(avatarUrl.lastIndexOf('.')));
        assertArrayEquals(JPEG_CONTENTS, Files.readAllBytes(imageDirectory.resolve(
                avatarUrl.substring("/images/".length()))));
    }

    @Test
    void rejectsUnsupportedExtensionsAndMismatchedContent() {
        AvatarStorageService service = new AvatarStorageService(imageDirectory.toString());
        MockMultipartFile unsupported = new MockMultipartFile(
                "file", "avatar.gif", "image/gif", PNG_CONTENTS);
        MockMultipartFile mismatched = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", PNG_CONTENTS);

        ResponseStatusException unsupportedException = assertThrows(
                ResponseStatusException.class, () -> service.store(unsupported));
        ResponseStatusException mismatchedException = assertThrows(
                ResponseStatusException.class, () -> service.store(mismatched));

        assertEquals(HttpStatus.BAD_REQUEST, unsupportedException.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, mismatchedException.getStatusCode());
    }

    @Test
    void rejectsFilesLargerThanFiveMegabytes() {
        AvatarStorageService service = new AvatarStorageService(imageDirectory.toString());
        byte[] oversizedImage = new byte[5 * 1024 * 1024 + 1];
        oversizedImage[0] = PNG_CONTENTS[0];
        oversizedImage[1] = PNG_CONTENTS[1];
        oversizedImage[2] = PNG_CONTENTS[2];
        oversizedImage[3] = PNG_CONTENTS[3];
        oversizedImage[4] = PNG_CONTENTS[4];
        oversizedImage[5] = PNG_CONTENTS[5];
        oversizedImage[6] = PNG_CONTENTS[6];
        oversizedImage[7] = PNG_CONTENTS[7];
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", oversizedImage);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.store(file));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
