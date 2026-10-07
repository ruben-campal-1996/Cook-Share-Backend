package dev.ruben.cookandshare.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRespository extends JpaRepository<UserAuthEntity, Integer> {
    Optional<UserAuthEntity> findByUserName(String username);
}
