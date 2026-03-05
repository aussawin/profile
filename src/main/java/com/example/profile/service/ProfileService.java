package com.example.profile.service;

import com.example.profile.model.Profile;
import com.example.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found with id: " + id));
    }

    @Transactional
    public Profile create(Profile profile) {
        if (profileRepository.existsByUsername(profile.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + profile.getUsername());
        }
        if (profileRepository.existsByEmail(profile.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + profile.getEmail());
        }
        return profileRepository.save(profile);
    }

    @Transactional
    public Profile update(Long id, Profile updated) {
        Profile existing = findById(id);
        String newEmail = updated.getEmail();
        if (!existing.getEmail().equals(newEmail) && profileRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already registered: " + newEmail);
        }
        existing.setEmail(newEmail);
        existing.setStatus(updated.getStatus());
        return profileRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException("Profile not found with id: " + id);
        }
        profileRepository.deleteById(id);
    }
}
