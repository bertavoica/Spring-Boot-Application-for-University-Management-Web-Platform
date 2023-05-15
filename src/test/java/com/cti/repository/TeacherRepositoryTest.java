package com.cti.repository;

import com.cti.models.Course;
import com.cti.models.Project;
import com.cti.models.Teacher;
import com.cti.payload.request.TeacherAddRequest;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

public class TeacherRepositoryTest {

    @Test
    public void testFindByUsername() {
        TeacherRepository repository = Mockito.mock(TeacherRepository.class);
        TeacherAddRequest teacherAddRequest = new TeacherAddRequest();
        teacherAddRequest.setUsername("username");
        Mockito.when(repository.findByUsername("username")).thenReturn(Optional.of(new Teacher(teacherAddRequest)));

        Optional<Teacher> teacher = repository.findByUsername("username");
        assertEquals(teacher.get().getUsername(), "username");
    }
}
