package com.example.studyrats.security;

import com.example.studyrats.service.firebase.FirebaseService;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;

public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;

    public FirebaseAuthFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                FirebaseToken decodedToken = firebaseService.verifyToken(token);

                request.setAttribute("firebaseUser", decodedToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}