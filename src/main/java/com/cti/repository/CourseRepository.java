package com.cti.repository;

import com.cti.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    void deleteByUniqueId(String uniqueId);

    Optional<Course> findByUniqueId(String courseUniqueId);
}
