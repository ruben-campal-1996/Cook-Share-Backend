package dev.ruben.cookandshare.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.auth.entity.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
    Optional<UserRoleEntity> findByName(String name);
}
