package com.cti.repository;

import com.cti.models.Course;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import static org.testng.Assert.*;

@DataMongoTest
public class CourseRepositoryTest extends AbstractMongoClientConfiguration {

    @Autowired
    private CourseRepository courseRepository;

    @BeforeMethod

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Override
    protected String getDatabaseName() {
        return "UniversityDB";
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }


    @Test
    public void testFindByUniqueId() {
        Course course = new Course();
        courseRepository.save(course);

        Optional<Course> foundCourse = courseRepository.findByUniqueId("test123");
        assertTrue(foundCourse.isPresent());
        assertEquals(foundCourse.get().getCompleteName(), "Test Course");
        assertEquals(foundCourse.get().getDescription(), "Test Description");
        assertEquals(foundCourse.get().getUniqueId(), "test123");
    }

    @Test
    public void testDeleteByUniqueId() {
        Course course = new Course();
        courseRepository.save(course);

        courseRepository.deleteByUniqueId("test123");
        Optional<Course> deletedCourse = courseRepository.findByUniqueId("test123");
        assertFalse(deletedCourse.isPresent());
    }
}