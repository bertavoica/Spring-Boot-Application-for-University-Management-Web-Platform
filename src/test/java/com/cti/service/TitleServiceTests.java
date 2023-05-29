package com.cti.service;


import com.cti.exception.TitleExistsException;
import com.cti.models.Teacher;
import com.cti.models.Title;
import com.cti.payload.request.TitleAddRequest;
import com.cti.repository.TeacherRepository;
import com.cti.repository.TitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TitleServiceTests {

    @InjectMocks
    private TitleService titleService;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Get all titles successfully.")
    public void getAllTitlesTest() {
        Mockito.when(this.titleRepository.findAll()).thenReturn(List.of(new Title(), new Title()));

        List<Title> result = this.titleService.getAllTitles();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Add title successfully.")
    public void addTitleTest() throws TitleExistsException {
        TitleAddRequest titleAddRequest = new TitleAddRequest();
        titleAddRequest.setName("test");

        Mockito.when(this.titleRepository.findByName("test")).thenReturn(Optional.empty());

        this.titleService.addTitle(titleAddRequest);

        Mockito.verify(this.titleRepository).save(any(Title.class));
    }

    @Test
    @DisplayName("Add a title when title already exists.")
    public void addTitleWithExistingTitleTest() {
        TitleAddRequest titleAddRequest = new TitleAddRequest();
        titleAddRequest.setName("test");

        Title title = new Title();
        title.setName("test");

        Mockito.when(this.titleRepository.findByName("test")).thenReturn(Optional.of(title));

        assertThrows(TitleExistsException.class, () -> this.titleService.addTitle(titleAddRequest));
    }

    @Test
    @DisplayName("Delete a title successfully.")
    public void deleteTitleTest() {
        List<Teacher> teachers = new ArrayList<>();

        Teacher teacher_1 = new Teacher();
        Teacher teacher_2 = new Teacher();

        teachers.add(teacher_1);
        teachers.add(teacher_2);

        Mockito.when(this.teacherRepository.findByTitle("test")).thenReturn(teachers);

        Mockito.doNothing().when(this.titleRepository).deleteByName("test");

        this.titleService.deleteTitle("test");

        Mockito.verify(this.teacherRepository, Mockito.times(2)).save(any(Teacher.class));
    }
}
