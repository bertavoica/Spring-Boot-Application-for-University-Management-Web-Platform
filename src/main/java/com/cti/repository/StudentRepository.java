package com.cti.repository;

import com.cti.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByUsername(String username);

    void deleteByUsername(String username);

    List<Student> findByGroup(String groupName);

    List<Student> findByUsernameContainingIgnoreCase(String username);

    @Query("{ 'coursesIds': ?0 }")
    List<Student> findByCourseId(String uniqueId);
}
