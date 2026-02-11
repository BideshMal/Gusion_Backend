package com.bidesh.OJ.Gusion.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.bidesh.OJ.Gusion.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    // This was the missing method causing the error!
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (id, email, role) VALUES (:id, :email, :role) ON CONFLICT (id) DO NOTHING", nativeQuery = true)
    void insertUserSafe(UUID id, String email, String role);
}