package dev.vrba.studentskyportal.backend.repositories;

import dev.vrba.studentskyportal.backend.entities.DiscordServer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServersRepository extends CrudRepository<DiscordServer, Long> {
}
