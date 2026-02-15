package com.example.studyrats.security;

import com.example.studyrats.service.firebase.FirebaseService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.util.List;

public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;

    public FirebaseAuthFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String json = """
                {
                    "error": "Firebase incomplete",
                    "message": "%s"
                }
                """.formatted("authHeader: "+authHeader);

                response.getWriter().write(json);
                response.getWriter().flush();
                return;
            } else {
                String token = authHeader.substring(7);
                FirebaseToken decodedToken = firebaseService.verifyToken(token);
                request.setAttribute("firebaseUser", decodedToken);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                decodedToken.getUid(),
                                null,
                                List.of() // sem roles por enquanto
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = """
            {
                "error": "Firebase catch",
                "message": "%s"
            }
            """.formatted(e.getMessage());

            response.getWriter().write(json);
            response.getWriter().flush();
            return;
        }
    }
}