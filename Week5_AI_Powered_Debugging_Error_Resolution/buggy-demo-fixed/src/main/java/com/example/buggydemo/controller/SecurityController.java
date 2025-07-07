package com.example.buggydemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
public class SecurityController {
    private static final Set<String> ALLOWED_DOMAINS = Set.of("example.com", "localhost");

    @GetMapping("/redirect")
    public ResponseEntity<?> redirect(@RequestParam String url, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host != null && ALLOWED_DOMAINS.contains(host)) {
                response.sendRedirect(url);
                return null;
            } else {
                return ResponseEntity.badRequest().body("Invalid redirect domain");
            }
        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body("Malformed URL");
        }
    }
} 