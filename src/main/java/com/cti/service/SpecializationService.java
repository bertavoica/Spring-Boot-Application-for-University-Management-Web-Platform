package com.cti.service;

import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.repository.SpecializationRepository;
import com.cti.repository.TeacherRepository;
import fit.ColumnFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService extends ColumnFixture {

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public SpecializationService() {}

    public Specialization getTeacherSpecialization(java.lang.String username) {

        Teacher teacher;
        Optional<Teacher> optionalTeacher;
        Optional<Specialization> optionalSpecialization;

        optionalTeacher = teacherRepository.findByUsername(username);
        if (!optionalTeacher.isPresent())
            return null;

        teacher = optionalTeacher.get();
        optionalSpecialization = specializationRepository.findByName(teacher.getSpecialization());
        return optionalSpecialization.orElse(null);

    }

    public List<Teacher> provideTeachers(List<java.lang.String> teacherNames) {
        List<Teacher> teacherList;
        Optional<Teacher> optionalTeacher;

        teacherList = new ArrayList<>();
        for (java.lang.String teacherName : teacherNames) {
            optionalTeacher = teacherRepository.findByUsername(teacherName);
            optionalTeacher.ifPresent(teacherList::add);
        }

        return teacherList;
    }

}
