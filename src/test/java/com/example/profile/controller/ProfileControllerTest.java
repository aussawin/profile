package com.example.profile.controller;

import com.example.profile.model.Profile;
import com.example.profile.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    @WithMockUser
    void getAll_returnsProfileList() throws Exception {
        when(profileService.findAll()).thenReturn(List.of(profile));

        mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsProfile() throws Exception {
        when(profileService.findById(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returnsNotFound() throws Exception {
        when(profileService.findById(99L)).thenThrow(new NoSuchElementException("Profile not found with id: 99"));

        mockMvc.perform(get("/api/profiles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validProfile_returnsCreated() throws Exception {
        when(profileService.create(any(Profile.class))).thenReturn(profile);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void update_existingId_returnsUpdatedProfile() throws Exception {
        when(profileService.update(eq(1L), any(Profile.class))).thenReturn(profile);

        mockMvc.perform(put("/api/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void delete_existingId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/profiles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new NoSuchElementException("Profile not found with id: 99"))
                .when(profileService).delete(99L);

        mockMvc.perform(delete("/api/profiles/99"))
                .andExpect(status().isNotFound());
    }
}
