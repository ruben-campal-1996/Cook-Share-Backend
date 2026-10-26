package dev.ruben.cookandshare.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ruben.cookandshare.auth.dto.UserDetailsDTORequest;
import dev.ruben.cookandshare.auth.dto.UserDetailsDTOResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api-endpoint}/user-details")
public class UserDetailsController {

    private final UserProfileService userProfileService;
    private final AvatarStorageService avatarStorageService;

    public UserDetailsController(
            UserProfileService userProfileService,
            AvatarStorageService avatarStorageService) {
        this.userProfileService = userProfileService;
        this.avatarStorageService = avatarStorageService;
    }

    @GetMapping
    public ResponseEntity<UserDetailsDTOResponse> findCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.findByEmail(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<UserDetailsDTOResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserDetailsDTORequest request) {
        return ResponseEntity.ok(userProfileService.updateByEmail(authentication.getName(), request));
    }

    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUploadResponse> uploadAvatar(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(new AvatarUploadResponse(avatarStorageService.store(file)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentUser(Authentication authentication) {
        userProfileService.deactivateByEmail(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
