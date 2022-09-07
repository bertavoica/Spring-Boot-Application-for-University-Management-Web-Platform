package com.cti.repository;

import com.cti.models.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends MongoRepository<Teacher, String> {

    Optional<Teacher> findByUsername(String username);

    void deleteByUsername(String username);

    List<Teacher> findByUsernameContainingIgnoreCase(String username);


    List<Teacher> findByTitle(String name);

    List<Teacher> findBySpecialization(String name);
}
