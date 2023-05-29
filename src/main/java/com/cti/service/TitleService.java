package com.cti.service;

import com.cti.exception.TitleExistsException;
import com.cti.models.Teacher;
import com.cti.models.Title;
import com.cti.payload.request.TitleAddRequest;
import com.cti.repository.TeacherRepository;
import com.cti.repository.TitleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TitleService {

    private final TitleRepository titleRepository;
    private final TeacherRepository teacherRepository;

    public TitleService(
            TitleRepository titleRepository,
            TeacherRepository teacherRepository
    ) {
        this.titleRepository = titleRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<Title> getAllTitles() {
        return titleRepository.findAll();
    }

    public String addTitle(TitleAddRequest titleAddRequest) throws TitleExistsException {
        Title title;

        if (titleRepository.findByName(titleAddRequest.getName()).isPresent()) {
            throw new TitleExistsException();
        }

        title = new Title();
        title.setName(titleAddRequest.getName());
        titleRepository.save(title);

        return titleAddRequest.getName();
    }

    public List<Title> deleteTitle(String name) {
        List<Teacher> teachers;

        // Set as null as teacher titles that have this name
        teachers = teacherRepository.findByTitle(name);
        for (Teacher teacher : teachers) {
            teacher.setTitle(null);
            teacherRepository.save(teacher);
        }

        titleRepository.deleteByName(name);

        return titleRepository.findAll();
    }
}
