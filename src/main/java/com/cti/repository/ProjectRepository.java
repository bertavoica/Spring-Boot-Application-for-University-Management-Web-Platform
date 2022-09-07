package com.cti.repository;

import com.cti.models.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends MongoRepository<Project, String> {
    void deleteByUniqueId(String uniqueId);

    Optional<Project> findByUniqueId(String uniqueId);

    List<Project> findByOwner(String username);
}
