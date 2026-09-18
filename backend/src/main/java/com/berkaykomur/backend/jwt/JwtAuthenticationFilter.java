package com.berkaykomur.backend.jwt;

import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        final String username;

        try {
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtService.isTokenValidWithoutDB(jwt)) {

                    Long userId = jwtService.extractUserId(jwt);

                    // 2. Veritabanına GİTMEDEN, elimizdeki verilerle bir UserEntity yaratıyoruz
                    UserEntity dummyUser = UserEntity.builder()
                            .id(userId)
                            .username(username)
                            // Şifre veya email gibi alanlara Controller'da ihtiyacın yoksa null kalabilir.
                            .build();

                    // 3. Bunu senin CustomUserDetails sınıfına sarıyoruz
                    CustomUserDetails userDetails = new CustomUserDetails(dummyUser);

                    // 4. Spring Security'ye String değil, objenin kendisini veriyoruz!
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            java.util.List.of()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.warn("JWT doğrulaması basarisiz: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}