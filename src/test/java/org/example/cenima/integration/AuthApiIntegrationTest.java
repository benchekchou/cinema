package org.example.cenima.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cenima.dto.LoginRequestDTO;
import org.example.cenima.dto.RegisterRequestDTO;
import org.example.cenima.entity.AppUser;
import org.example.cenima.entity.Role;
import org.example.cenima.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- /register tests ---

    @Test
    void register_shouldCreateAccount_andReturnCreated() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email("newuser@cinema.local")
                .password("MyPass123!")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@cinema.local"))
                .andExpect(jsonPath("$.message").value("Account created successfully"));

        AppUser saved = appUserRepository.findByEmail("newuser@cinema.local").orElseThrow();
        assertThat(saved.getRole()).isEqualTo(Role.CLIENT);
        assertThat(saved.isEnabled()).isTrue();
        assertThat(passwordEncoder.matches("MyPass123!", saved.getPassword())).isTrue();
    }

    @Test
    void register_shouldReject_whenEmailAlreadyExists() throws Exception {
        appUserRepository.save(AppUser.builder()
                .email("existing@cinema.local")
                .password(passwordEncoder.encode("Pass123!"))
                .role(Role.CLIENT)
                .enabled(true)
                .build());

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email("existing@cinema.local")
                .password("MyPass123!")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email already in use")));
    }

    @Test
    void register_shouldReject_whenPasswordTooWeak() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email("weak@cinema.local")
                .password("weak")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void register_shouldReject_whenEmailInvalid() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email("not-an-email")
                .password("MyPass123!")
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("email"));
    }

    // --- /login tests ---

    @Test
    void login_shouldReturnJwt_whenCredentialsValid() throws Exception {
        appUserRepository.save(AppUser.builder()
                .email("login@cinema.local")
                .password(passwordEncoder.encode("MyPass123!"))
                .role(Role.CLIENT)
                .enabled(true)
                .build());

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("login@cinema.local")
                .password("MyPass123!")
                .build();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("login@cinema.local"))
                .andExpect(jsonPath("$.role").value("ROLE_CLIENT"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsInvalid() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("nobody@cinema.local")
                .password("WrongPass1!")
                .build();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid authentication credentials"));
    }

    @Test
    void login_shouldReturn400_whenPayloadEmpty() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("")
                .password("")
                .build();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
    }
}
