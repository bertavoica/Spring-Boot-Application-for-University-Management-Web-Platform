package com.cti.repository;

import com.cti.models.Specialization;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpecializationRepository extends MongoRepository<Specialization, java.lang.String> {

    Optional<Specialization> findByUniqueId(java.lang.String uniqueId);

    void deleteByUniqueId(java.lang.String uniqueId);

    Optional<Specialization> findByName(java.lang.String name);
}
