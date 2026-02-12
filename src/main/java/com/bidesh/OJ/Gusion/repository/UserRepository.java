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

    boolean existsByEmail(String email);

    // 🟢 CRITICAL FIX: Added 'name' and 'password' columns with default values
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO users (id, email, role, name, password) 
        VALUES (:id, :email, :role, 'System User', 'default123') 
        ON CONFLICT (id) DO NOTHING
        """, nativeQuery = true)
    void insertUserSafe(UUID id, String email, String role);
}