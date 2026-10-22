package dev.ruben.cookandshare.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;

public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, Integer> {
    @EntityGraph(attributePaths = "role")
    Optional<UserDetailsEntity> findByUserId(Integer userId);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndUserIdNot(String nickname, Integer userId);
}
