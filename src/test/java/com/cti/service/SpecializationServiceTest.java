package com.cti.service;

import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.repository.SpecializationRepository;
import com.cti.repository.TeacherRepository;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.AssertJUnit.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpecializationServiceTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private SpecializationService specializationService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTeacherSpecializationTest() {
        Teacher teacher = new Teacher();
        teacher.setSpecialization("test");

        Specialization specialization = new Specialization();
        specialization.setName("test");

        Mockito.when(teacherRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(teacher));
        Mockito.when(specializationRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.getTeacherSpecialization("");

        assertEquals("test", result.getName());
    }

    @Test
    public void getTeacherSpecializationTest_invalidTeacher() {
        Specialization specialization = new Specialization();
        specialization.setName("test");

        Mockito.when(teacherRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(specializationRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.getTeacherSpecialization("");

        assertNull(result);
    }

    @Test
    public void getTeacherSpecializationTest_invalidSpecialization() {
        Teacher teacher = new Teacher();
        teacher.setSpecialization("test");

        Mockito.when(teacherRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(teacher));
        Mockito.when(specializationRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

        Specialization result = specializationService.getTeacherSpecialization("");

        assertNull(result);
    }

}
