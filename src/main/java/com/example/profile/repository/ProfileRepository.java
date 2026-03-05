package com.example.profile.repository;

import com.example.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUsername(String username);

    Optional<Profile> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
