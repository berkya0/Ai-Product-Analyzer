package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
          mockUser=UserEntity.builder()
                .id(1L)
                .password("password123")
                .username("berkay").build();

    }

    @Test
    @DisplayName("loadUserByUsername: Kullanıcı bulunduğunda UserDetails dönmeli")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        String username="berkay";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        assertNotNull(userDetails);
        assertEquals(username,userDetails.getUsername());
        verify(userRepository,times(1)).findByUsername(username);
    }
    @Test
    @DisplayName("usernameNotFoundException: Kullanıcı adına göre kullanıcı bulunamazsa hata dönmeli")
    void loadUserByUsername_WhenUserDoesNotExist_ShouldReturnUsernameNotFoundException() {
        String username="berkay";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("Kullanıcı bulunamadı: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);

    }
}