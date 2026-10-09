package dev.ruben.cookandshare.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.auth.entity.UserAuthEntity;

public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {
    Optional<UserAuthEntity> findByEmail(String email);
}
