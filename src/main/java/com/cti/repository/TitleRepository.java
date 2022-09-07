package com.cti.repository;

import com.cti.models.Title;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TitleRepository extends MongoRepository<Title, String> {

    void deleteByName(String name);

    Optional<Title> findByName(String name);
}
