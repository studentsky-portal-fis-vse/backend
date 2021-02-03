package dev.vrba.studentskyportal.backend.repositories;

import dev.vrba.studentskyportal.backend.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
