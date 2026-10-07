package dev.ruben.cookandshare.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "users_auth")
public class UserAuthEntity {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false, unique = true, length = 150)
    private String email;

    @Column (name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column (name = "change_password", nullable = false)
    private boolean changePassword;

    @Column (name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column (name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    public UserAuthEntity() {
    }

    public UserAuthEntity(Integer id, String email, String passwordHash, boolean changePassword, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.changePassword = changePassword;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash=passwordHash;
    }

    public boolean isMustChangePassword() {
        return changePassword;
    }

    public void setMustChangePassword(boolean changePassword) {
        this.changePassword=changePassword;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
