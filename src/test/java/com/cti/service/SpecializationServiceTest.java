package com.cti.service;

import com.cti.exception.SpecializationExistsException;
import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.payload.request.SpecializationAddRequest;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    public void addSpecializationTest() {
        String name = "test";

        SpecializationAddRequest specializationAddRequest = new SpecializationAddRequest();
        specializationAddRequest.setName(name);

        Mockito.when(specializationRepository.findByName(name)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> this.specializationService.addSpecialization(specializationAddRequest));

        verify(specializationRepository, times(1)).save(any(Specialization.class));
    }

    @Test
    public void addSpecializationWhenExists() {
        String name = "test";

        Specialization specialization = new Specialization();
        specialization.setName("test");

        SpecializationAddRequest specializationAddRequest = new SpecializationAddRequest();
        specializationAddRequest.setName(name);

        Mockito.when(specializationRepository.findByName(name)).thenReturn(Optional.of(specialization));

        assertThrows(SpecializationExistsException.class, () -> this.specializationService.addSpecialization(specializationAddRequest));

        verify(specializationRepository, times(0)).save(any(Specialization.class));
    }

}
