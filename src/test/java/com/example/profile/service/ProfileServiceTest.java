package com.example.profile.service;

import com.example.profile.model.Profile;
import com.example.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setUsername("testuser");
        profile.setEmail("test@example.com");
        profile.setStatus(Profile.Status.ACTIVE);
    }

    @Test
    void findAll_returnsAllProfiles() {
        when(profileRepository.findAll()).thenReturn(List.of(profile));

        List<Profile> result = profileService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_existingId_returnsProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Profile result = profileService.findById(1L);

        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.findById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_newProfile_savesAndReturns() {
        when(profileRepository.existsByUsername("testuser")).thenReturn(false);
        when(profileRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile result = profileService.create(profile);

        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(profileRepository).save(profile);
    }

    @Test
    void create_duplicateUsername_throwsException() {
        when(profileRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> profileService.create(profile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("testuser");
    }

    @Test
    void create_duplicateEmail_throwsException() {
        when(profileRepository.existsByUsername("testuser")).thenReturn(false);
        when(profileRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> profileService.create(profile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    void delete_existingId_deletesProfile() {
        when(profileRepository.existsById(1L)).thenReturn(true);

        profileService.delete(1L);

        verify(profileRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(profileRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> profileService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_existingId_updatesEmailAndStatus() {
        Profile existing = new Profile();
        existing.setUsername("testuser");
        existing.setEmail("old@example.com");
        existing.setStatus(Profile.Status.PENDING);

        Profile updated = new Profile();
        updated.setUsername("testuser");
        updated.setEmail("new@example.com");
        updated.setStatus(Profile.Status.ACTIVE);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(profileRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        Profile result = profileService.update(1L, updated);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getStatus()).isEqualTo(Profile.Status.ACTIVE);
    }

    @Test
    void update_duplicateEmail_throwsException() {
        Profile existing = new Profile();
        existing.setUsername("testuser");
        existing.setEmail("old@example.com");
        existing.setStatus(Profile.Status.PENDING);

        Profile updated = new Profile();
        updated.setEmail("taken@example.com");
        updated.setStatus(Profile.Status.ACTIVE);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(profileRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> profileService.update(1L, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken@example.com");
    }
}
