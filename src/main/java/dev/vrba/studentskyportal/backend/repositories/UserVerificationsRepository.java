package dev.vrba.studentskyportal.backend.repositories;

import dev.vrba.studentskyportal.backend.entities.UserVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationsRepository extends CrudRepository<UserVerification, Long> {
}
