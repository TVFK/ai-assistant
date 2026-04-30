package ru.taf.rag_assistant.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taf.rag_assistant.entities.User;
import ru.taf.rag_assistant.entities.api.AuthResponse;
import ru.taf.rag_assistant.entities.api.LoginRequest;
import ru.taf.rag_assistant.repositories.UserRepository;
import ru.taf.rag_assistant.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        User user = userRepo.findByUsername(req.username()).orElseThrow();
        String token = jwtService.generate(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name()));
    }
}
