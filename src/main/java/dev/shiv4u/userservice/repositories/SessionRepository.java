package dev.shiv4u.userservice.repositories;

import dev.shiv4u.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBytokenAndUser_id(String token, Long userId);
}