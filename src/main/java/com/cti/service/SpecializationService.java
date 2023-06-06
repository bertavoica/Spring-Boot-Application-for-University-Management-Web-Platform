package com.cti.service;

import com.cti.exception.*;
import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.payload.request.SpecializationAddRequest;
import com.cti.payload.request.SpecializationMemberAddRequest;
import com.cti.payload.request.SpecializationMemberModifyRequest;
import com.cti.payload.request.SpecializationUpdateRequest;
import com.cti.repository.SpecializationRepository;
import com.cti.repository.TeacherRepository;
import fit.ColumnFixture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService extends ColumnFixture {

    private final SpecializationRepository specializationRepository;

    private final TeacherRepository teacherRepository;

    public SpecializationService(SpecializationRepository specializationRepository, TeacherRepository teacherRepository) {
        this.specializationRepository = specializationRepository;
        this.teacherRepository = teacherRepository;
    }

    public void addSpecialization(SpecializationAddRequest specializationAddRequest) throws SpecializationExistsException {
        Specialization specialization;

        if (specializationRepository.findByName(specializationAddRequest.getName()).isPresent()) {
            throw new SpecializationExistsException();
        }

        specialization = new Specialization(specializationAddRequest);
        specializationRepository.save(specialization);
    }

    public void updateSpecialization(SpecializationUpdateRequest specializationUpdateRequest) throws SpecializationNotExistsException, SpecializationExistsException {
        Specialization specialization;
        Optional<Specialization> optionalSpecialization;
        List<Teacher> teachers;

        optionalSpecialization = specializationRepository.findByUniqueId(specializationUpdateRequest.getUniqueId());
        if (!optionalSpecialization.isPresent()) {
            throw new SpecializationNotExistsException();
        }

        if (specializationRepository.findByName(specializationUpdateRequest.getName()).isPresent()) {
            throw new SpecializationExistsException();
        }

        specialization = optionalSpecialization.get();
        teachers = teacherRepository.findBySpecialization(specialization.getName());
        for (Teacher teacher : teachers) {
            teacher.setSpecialization(specializationUpdateRequest.getName());
            teacherRepository.save(teacher);
        }
        specialization.setName(specializationUpdateRequest.getName());
        specializationRepository.save(specialization);
    }

    public void deleteSpecialization(String uniqueId) {
        specializationRepository.deleteByUniqueId(uniqueId);
    }

    public void addSpecializationMember(SpecializationMemberAddRequest specializationMemberAddRequest) throws TeacherBelongsToSpecializationException, SpecializationNotExistsException, SuperiorNotBelongsSpecializationException, SuperiorDifferentSpecializationException, TeacherNotExistsException {
        Specialization teacherSpecialization;
        Specialization superiorSpecialization;
        Specialization inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        Teacher teacher;
        Optional<Teacher> optionalTeacher;

        teacherSpecialization = getTeacherSpecialization(specializationMemberAddRequest.getName());

        if (teacherSpecialization != null) {
            throw new TeacherBelongsToSpecializationException();
        }

        optionalSpecialization = specializationRepository.findByName(specializationMemberAddRequest.getSpecializationName());
        if (!optionalSpecialization.isPresent()) {
            throw new SpecializationNotExistsException();
        }
        inputSpecialization = optionalSpecialization.get();

        if (specializationMemberAddRequest.getSuperior() != null && !specializationMemberAddRequest.getSuperior().equals("")) {
            superiorSpecialization = getTeacherSpecialization(specializationMemberAddRequest.getSuperior());
            if (superiorSpecialization == null) {
                throw new SuperiorNotBelongsSpecializationException();
            }

            if (!superiorSpecialization.getName().equals(specializationMemberAddRequest.getSpecializationName())) {
                throw new SuperiorDifferentSpecializationException();
            }
        }

        optionalTeacher = teacherRepository.findByUsername(specializationMemberAddRequest.getName());
        if (!optionalTeacher.isPresent()) {
            throw new TeacherNotExistsException();
        }

        teacher = optionalTeacher.get();
        if (inputSpecialization.getTeachers().contains(teacher.getUsername())) {
            throw new TeacherBelongsToSpecializationException();
        }

        if (specializationMemberAddRequest.getSuperior() != null && !specializationMemberAddRequest.getSuperior().equals(""))
            teacher.setSuperior(specializationMemberAddRequest.getSuperior());

        teacher.setSpecialization(inputSpecialization.getName());
        inputSpecialization.getTeachers().add(teacher.getUsername());

        teacherRepository.save(teacher);
        specializationRepository.save(inputSpecialization);
    }

    public void deleteSpecializationMember(String stringName, String username) throws SpecializationNotExistsException, TeacherNotBelongsSpecializationException, TeacherHasSuperiorsException, TeacherNotExistsException {
        Specialization inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        int totalChildNodes;
        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        List<Teacher> teacherList;

        optionalSpecialization = specializationRepository.findByName(stringName);
        if (!optionalSpecialization.isPresent()) {
            throw new SpecializationNotExistsException();
        }
        inputSpecialization = optionalSpecialization.get();

        // Check if teacher exists in the input specialization
        if (!inputSpecialization.getTeachers().contains(username)) {
            throw new TeacherNotBelongsSpecializationException();
        }

        teacherList = provideTeachers(inputSpecialization.getTeachers());
        // Check if the deleted teacher is superior for any other teacher
        totalChildNodes = 0;
        for (Teacher teacherFromSpecialization : teacherList) {
            if (teacherFromSpecialization.getSuperior() != null && teacherFromSpecialization.getSuperior().equals(username))
                totalChildNodes++;
        }

        if (totalChildNodes > 0) {
//            return totalChildNodes;
            throw new TeacherHasSuperiorsException();
        }

        // Check if teacher is in its repository
        optionalTeacher = teacherRepository.findByUsername(username);
        if (!optionalTeacher.isPresent()) {
            throw new TeacherNotExistsException();
        }

        teacher = optionalTeacher.get();

        inputSpecialization.getTeachers().remove(username);
        teacher.setSpecialization(null);
        teacher.setSuperior(null);

        teacherRepository.save(teacher);
        specializationRepository.save(inputSpecialization);
    }

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

    public String getSpecializationGraph(String stringName) throws SpecializationNotExistsException, JSONException {
        Specialization inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        List<Teacher> teacherList;

        JSONObject nodeValue;
        JSONArray result = new JSONArray();
        JSONArray node;

        optionalSpecialization = specializationRepository.findByName(stringName);
        if (!optionalSpecialization.isPresent()) {
            throw new SpecializationNotExistsException();
        }
        inputSpecialization = optionalSpecialization.get();

        node = new JSONArray();
        node.put(stringName);
        node.put("");
        node.put("");
        result.put(node);

        teacherList = provideTeachers(inputSpecialization.getTeachers());

        for (Teacher teacher : teacherList) {
            if (teacher.getSuperior() == null) {
                node = new JSONArray();
                nodeValue = new JSONObject();
                nodeValue.put("v", teacher.getUsername());
                nodeValue.put("f", teacher.getUsername() + "<div style=\"color:red; font-style:italic\">" + (teacher.getTitle() == null ? "?" : teacher.getTitle()) + "</div>");
                node.put(nodeValue);
                node.put(stringName);
                node.put("");
                result.put(node);

            } else {
                node = new JSONArray();
                nodeValue = new JSONObject();
                nodeValue.put("v", teacher.getUsername());
                nodeValue.put("f", teacher.getUsername() + "<div style=\"color:red; font-style:italic\">" + (teacher.getTitle() == null ? "?" : teacher.getTitle()) + "</div>");
                node.put(nodeValue);
                node.put(teacher.getSuperior());
                node.put("");
                result.put(node);
            }
        }


        return result.toString();
    }

    public void modifySpecializationMember(SpecializationMemberModifyRequest specializationMemberModifyRequest) throws TeacherNotBelongsSpecializationException, TeacherBelongsDifferentSpecializationException, SuperiorNotBelongsSpecializationException, SuperiorDifferentSpecializationException {
        Specialization teacherSpecialization;
        Specialization superiorSpecialization;
        List<Teacher> teacherList;

        teacherSpecialization = getTeacherSpecialization(specializationMemberModifyRequest.getName());

        if (teacherSpecialization == null) {
            throw new TeacherNotBelongsSpecializationException();
        }

        if (!teacherSpecialization.getName().equals(specializationMemberModifyRequest.getSpecializationName())) {
            throw new TeacherBelongsDifferentSpecializationException();
        }

        teacherList = provideTeachers(teacherSpecialization.getTeachers());

        if (specializationMemberModifyRequest.getSuperiorName() == null || specializationMemberModifyRequest.getSuperiorName().equals("")) {
            for (Teacher teacher : teacherList) {
                if (teacher.getUsername().equals(specializationMemberModifyRequest.getName())) {
                    teacher.setSuperior(null);
                    teacherRepository.save(teacher);
                    break;
                }
            }
        } else {
            superiorSpecialization = getTeacherSpecialization(specializationMemberModifyRequest.getSuperiorName());
            if (superiorSpecialization == null) {
                throw new SuperiorNotBelongsSpecializationException();
            }

            if (!superiorSpecialization.getName().equals(specializationMemberModifyRequest.getSpecializationName())) {
                throw new SuperiorDifferentSpecializationException();
            }

            for (Teacher teacher : teacherList) {
                if (teacher.getUsername().equals(specializationMemberModifyRequest.getName())) {
                    teacher.setSuperior(specializationMemberModifyRequest.getSuperiorName());
                    teacherRepository.save(teacher);
                    break;
                }
            }
        }

        specializationRepository.save(teacherSpecialization);
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

}
