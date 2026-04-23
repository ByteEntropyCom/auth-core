package com.byteentropy.auth_core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
    
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7 ) {
        String token = authHeader.substring(7);
        
        try {
            String username = jwtUtils.extractUsername(token);
            List<String> permissions = jwtUtils.extractPermissions(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendErrorResponse(response, "Token has expired. Please login again.");
            return; // Stop the filter chain here
        } catch (io.jsonwebtoken.JwtException e) {
            sendErrorResponse(response, "Invalid token.");
            return; // Stop the filter chain here
        }
    }

    filterChain.doFilter(request, response);
}

    // Helper method to write JSON directly to the response
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }


}