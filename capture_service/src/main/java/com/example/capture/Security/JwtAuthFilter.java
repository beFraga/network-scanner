package com.example.capture.Security;

import com.example.capture.Repository.UserRepository;
import com.example.common.UserInfo.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String email = null;

        // Extracts token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
                if (email != null) {
                    email = email.trim();
                }
            } catch (ExpiredJwtException e) {
                System.out.println("⚠️ JWT expired!! : " + e.getMessage());
            } catch (JwtException e) {
                System.out.println("❌ JWT invalid: " + e.getMessage());
            }
        }

        // If token is valid but the context is not, then authenticate it
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = userRepository.findByEmail(email).orElse(null);
            if (userDetails != null && jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Context authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("🔐 User authenticated via JWT: " + email);
            }
        }

        // Continues the requisition flow
        filterChain.doFilter(request, response);
    }
}
