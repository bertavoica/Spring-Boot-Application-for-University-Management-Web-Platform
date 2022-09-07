package com.cti.repository;

import com.cti.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {

    void deleteByUniqueId(String uniqueId);

    Optional<Course> findByUniqueId(String courseUniqueId);
}
