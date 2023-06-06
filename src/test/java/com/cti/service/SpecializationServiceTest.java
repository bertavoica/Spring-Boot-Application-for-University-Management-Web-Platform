package com.cti.service;

import com.cti.exception.SpecializationExistsException;
import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.payload.request.SpecializationAddRequest;
import com.cti.payload.request.SpecializationUpdateRequest;
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

import java.util.List;
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

    @Test
    public void updateSpecializationTest() {
        String name = "test";
        String uniqueId = "test";

        SpecializationUpdateRequest specializationUpdateRequest = new SpecializationUpdateRequest();
        specializationUpdateRequest.setName(name);
        specializationUpdateRequest.setUniqueId(uniqueId);

        Specialization specialization = new Specialization();
        specialization.setName("test1");
        specialization.setUniqueId(uniqueId);

        Mockito.when(this.specializationRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(specialization));
        Mockito.when(this.specializationRepository.findByName(name)).thenReturn(Optional.empty());

        Teacher teacher = new Teacher();
        teacher.setSpecialization("specialization");

        Mockito.when(this.teacherRepository.findBySpecialization("specialization")).thenReturn(List.of(teacher));

        assertDoesNotThrow(() -> this.specializationService.updateSpecialization(specializationUpdateRequest));

        verify(teacherRepository, times(1)).save(teacher);
        verify(specializationRepository, times(1)).save(specialization);
    }

}
