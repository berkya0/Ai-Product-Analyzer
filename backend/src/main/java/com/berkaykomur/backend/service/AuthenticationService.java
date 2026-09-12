package com.berkaykomur.backend.service;


import com.berkaykomur.backend.dto.user.AuthResponse;
import com.berkaykomur.backend.dto.user.LoginRequest;
import com.berkaykomur.backend.dto.user.RegisterRequest;
import com.berkaykomur.backend.exception.user.EmailAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UserAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;
<<<<<<< HEAD
import com.berkaykomur.backend.jwt.CustomUserDetails;
=======
>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1
import com.berkaykomur.backend.jwt.JwtService;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Yeni kullanıcı kayıt işlemi başlatıldı: {}", request.username());
        if(userRepository.existsByUsername(request.username())){
            throw new UserAlreadyExistsException("Kullanıcı adı seçilmiş: "+request.username());

        }
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email sisteme kayıtlı: "+request.email());
        }

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla kaydedildi: {}", request.username());

<<<<<<< HEAD
        String token = jwtService.generateToken(new HashMap<>(),  new CustomUserDetails(savedUser));
=======
        String token = jwtService.generateToken(new HashMap<>(), user);
>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1
        return new AuthResponse(savedUser.getId(),request.username(),token);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Giriş denemesi: {}", request.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(()->new UsernameNotFoundException("Kullanıcı adı bulunamadı: "+request.username()));

        log.info("Giriş başarılı: {}", request.username());
<<<<<<< HEAD
        String token = jwtService.generateToken(new HashMap<>(), new CustomUserDetails(user));
=======
        String token = jwtService.generateToken(new HashMap<>(), user);
>>>>>>> 6ee5fa01333b208303ad7c19c60004c8731e00d1
        return new AuthResponse(user.getId(),request.username(),token);
    }
}