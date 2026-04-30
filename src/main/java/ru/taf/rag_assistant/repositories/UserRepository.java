package ru.taf.rag_assistant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.taf.rag_assistant.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
