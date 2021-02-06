package dev.vrba.studentskyportal.backend.repositories;

import dev.vrba.studentskyportal.backend.entities.DiscordVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordVerificationsRepository extends CrudRepository<DiscordVerification, Long> {
    Optional<DiscordVerification> findByUserId(long userId);
}
