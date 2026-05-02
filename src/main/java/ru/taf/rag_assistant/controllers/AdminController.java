package ru.taf.rag_assistant.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.taf.rag_assistant.entities.Role;
import ru.taf.rag_assistant.entities.User;
import ru.taf.rag_assistant.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return userRepo.findAll().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("role", u.getRole().name());
            m.put("enabled", u.isEnabled());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        if (userRepo.findByUsername(body.get("username")).isPresent()) return ResponseEntity.badRequest().body("exists");
        User u = User.builder()
                .username(body.get("username"))
                .password(encoder.encode(body.get("password")))
                .role(Role.valueOf(body.get("role")))
                .enabled(true).build();
        u = userRepo.save(u);
        Map<String, Object> res = new HashMap<>();
        res.put("id", u.getId());
        res.put("username", u.getUsername());
        res.put("role", u.getRole().name());
        res.put("enabled", u.isEnabled());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User u = userRepo.findById(id).orElseThrow();
        u.setRole(Role.valueOf(body.get("role")));
        userRepo.save(u);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.ok("ok");
    }
}