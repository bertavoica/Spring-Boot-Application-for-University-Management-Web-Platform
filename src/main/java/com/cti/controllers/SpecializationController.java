package com.cti.controllers;

import com.cti.models.Specialization;
import com.cti.models.Teacher;
import com.cti.payload.request.SpecializationAddRequest;
import com.cti.payload.request.SpecializationMemberAddRequest;
import com.cti.payload.request.SpecializationMemberModifyRequest;
import com.cti.payload.request.SpecializationUpdateRequest;
import com.cti.repository.SpecializationRepository;
import com.cti.repository.TeacherRepository;
import com.cti.service.SpecializationService;
import com.cti.service.UserService;
import com.cti.service.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/specialization-controller")
public class SpecializationController {

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private SpecializationService specializationService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllSpecializations() {
        return ResponseEntity.ok(specializationRepository.findAll());
    }


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSpecialization(@Valid @RequestBody SpecializationAddRequest specializationAddRequest,
                                               Principal principal) {
        Specialization specialization;

        if (specializationRepository.findByName(specializationAddRequest.getName()).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationExists").get(userService.getPreferredLanguage(principal)) + " " + specializationAddRequest.getName());

        specialization = new Specialization(specializationAddRequest);
        specializationRepository.save(specialization);

        return ResponseEntity.ok(Utils.languageDictionary.get("SpecializationAdded").get(userService.getPreferredLanguage(principal)) + " " + specializationAddRequest.getName());
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSpecialization(@Valid @RequestBody SpecializationUpdateRequest specializationUpdateRequest,
                                                  Principal principal) {
        Specialization specialization;
        Optional<Specialization> optionalSpecialization;
        List<Teacher> teachers;

        optionalSpecialization = specializationRepository.findByUniqueId(specializationUpdateRequest.getUniqueId());
        if (!optionalSpecialization.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationNotExists").get(userService.getPreferredLanguage(principal)));

        if (specializationRepository.findByName(specializationUpdateRequest.getName()).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationExists").get(userService.getPreferredLanguage(principal)) + " " + specializationUpdateRequest.getName());

        specialization = optionalSpecialization.get();
        teachers = teacherRepository.findBySpecialization(specialization.getName());
        for (Teacher teacher : teachers) {
            teacher.setSpecialization(specializationUpdateRequest.getName());
            teacherRepository.save(teacher);
        }
        specialization.setName(specializationUpdateRequest.getName());
        specializationRepository.save(specialization);

        return ResponseEntity.ok(Utils.languageDictionary.get("SpecializationUpdated").get(userService.getPreferredLanguage(principal)));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSpecialization(@RequestParam(name = "uniqueId", defaultValue = "") java.lang.String uniqueId,
                                                  Principal principal) {

        specializationRepository.deleteByUniqueId(uniqueId);

        return ResponseEntity.ok(Utils.languageDictionary.get("SpecializationDeleted").get(userService.getPreferredLanguage(principal)));
    }

    @PostMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSpecializationMember(@Valid @RequestBody SpecializationMemberAddRequest specializationMemberAddRequest,
                                                     Principal principal) {
        Specialization teacherSpecialization, superiorSpecialization, inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        Teacher teacher;
        Optional<Teacher> optionalTeacher;

        teacherSpecialization = specializationService.getTeacherSpecialization(specializationMemberAddRequest.getName());

        if (teacherSpecialization != null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherBelongsSpecialization").get(userService.getPreferredLanguage(principal)) + " " + teacherSpecialization.getName());

        optionalSpecialization = specializationRepository.findByName(specializationMemberAddRequest.getSpecializationName());
        if (!optionalSpecialization.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationNotExists").get(userService.getPreferredLanguage(principal))  + " " + specializationMemberAddRequest.getSpecializationName());
        inputSpecialization = optionalSpecialization.get();

        if (specializationMemberAddRequest.getSuperior() != null && !specializationMemberAddRequest.getSuperior().equals("")) {
            superiorSpecialization = specializationService.getTeacherSpecialization(specializationMemberAddRequest.getSuperior());
            if (superiorSpecialization == null)
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SuperiorNotBelongsSpecialization").get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSuperior());
            if (!superiorSpecialization.getName().equals(specializationMemberAddRequest.getSpecializationName()))
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SuperiorDifferentSpecialization").get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSuperior());
        }

        optionalTeacher = teacherRepository.findByUsername(specializationMemberAddRequest.getName());
        if (!optionalTeacher.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherNotExists").get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getName());

        teacher = optionalTeacher.get();
        if (inputSpecialization.getTeachers().contains(teacher.getUsername()))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherBelongsSpecialization").get(userService.getPreferredLanguage(principal)));

        if (specializationMemberAddRequest.getSuperior() != null && !specializationMemberAddRequest.getSuperior().equals(""))
            teacher.setSuperior(specializationMemberAddRequest.getSuperior());

        teacher.setSpecialization(inputSpecialization.getName());
        inputSpecialization.getTeachers().add(teacher.getUsername());

        teacherRepository.save(teacher);
        specializationRepository.save(inputSpecialization);

        return ResponseEntity.ok(Utils.languageDictionary.get("TeacherAddedSpecialization").get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSpecializationName());
    }

    @DeleteMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSpecializationMember(@RequestParam(name = "stringName", defaultValue = "") java.lang.String stringName,
                                                        @RequestParam(name = "username", defaultValue = "") java.lang.String username,
                                                        Principal principal) {

        Specialization inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        int totalChildNodes;
        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        List<Teacher> teacherList;

        optionalSpecialization = specializationRepository.findByName(stringName);
        if (!optionalSpecialization.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationNotExists").get(userService.getPreferredLanguage(principal)));
        inputSpecialization = optionalSpecialization.get();

        // Check if teacher exists in the input specialization
        if (!inputSpecialization.getTeachers().contains(username))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherNotBelongsSpecialization").get(userService.getPreferredLanguage(principal)));

        teacherList = specializationService.provideTeachers(inputSpecialization.getTeachers());
        // Check if the deleted teacher is superior for any other teacher
        totalChildNodes = 0;
        for (Teacher teacherFromSpecialization : teacherList) {
            if (teacherFromSpecialization.getSuperior() != null && teacherFromSpecialization.getSuperior().equals(username))
                totalChildNodes++;
        }

        if (totalChildNodes > 0)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherHasSuperiors").get(userService.getPreferredLanguage(principal)) + " " + totalChildNodes);

        // Check if teacher is in its repository
        optionalTeacher = teacherRepository.findByUsername(username);
        if (!optionalTeacher.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherNotExists").get(userService.getPreferredLanguage(principal)));
        teacher = optionalTeacher.get();

        inputSpecialization.getTeachers().remove(username);
        teacher.setSpecialization(null);
        teacher.setSuperior(null);

        teacherRepository.save(teacher);
        specializationRepository.save(inputSpecialization);

        return ResponseEntity.ok(Utils.languageDictionary.get("TeacherRemovedSpecialization").get(userService.getPreferredLanguage(principal)));
    }

    @GetMapping(value="/member")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSpecializationGraph(@RequestParam(name = "stringName", defaultValue = "") java.lang.String stringName,
                                                    Principal principal) throws JSONException {

        Specialization inputSpecialization;
        Optional<Specialization> optionalSpecialization;
        List<Teacher> teacherList;

        JSONObject nodeValue;
        JSONArray result = new JSONArray();
        JSONArray node;

        optionalSpecialization = specializationRepository.findByName(stringName);
        if (!optionalSpecialization.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SpecializationNotExists").get(userService.getPreferredLanguage(principal)));
        inputSpecialization = optionalSpecialization.get();

        node = new JSONArray();
        node.put(stringName);
        node.put("");
        node.put("");
        result.put(node);

        teacherList = specializationService.provideTeachers(inputSpecialization.getTeachers());

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
        System.out.println("Res: " + result.toString());
        return ResponseEntity.ok(result.toString());
    }

    @PutMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifySpecializationMember(@Valid @RequestBody SpecializationMemberModifyRequest specializationMemberModifyRequest,
                                                        Principal principal) {

        Specialization teacherSpecialization, superiorSpecialization;
        boolean changedTitle = false;
        List<Teacher> teacherList;

        teacherSpecialization = specializationService.getTeacherSpecialization(specializationMemberModifyRequest.getName());

        if (teacherSpecialization == null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherNotBelongsSpecialization").get(userService.getPreferredLanguage(principal)) + " " + specializationMemberModifyRequest.getName());

        if (!teacherSpecialization.getName().equals(specializationMemberModifyRequest.getSpecializationName()))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherBelongsDifferentSpecialization").get(userService.getPreferredLanguage(principal)));

        teacherList = specializationService.provideTeachers(teacherSpecialization.getTeachers());

        if (specializationMemberModifyRequest.getSuperiorName() == null || specializationMemberModifyRequest.getSuperiorName().equals("")) {
            for (Teacher teacher : teacherList) {
                if (teacher.getUsername().equals(specializationMemberModifyRequest.getName())) {
                    teacher.setSuperior(null);
                    teacherRepository.save(teacher);
                    break;
                }
            }
        } else {
            superiorSpecialization = specializationService.getTeacherSpecialization(specializationMemberModifyRequest.getSuperiorName());
            if (superiorSpecialization == null)
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SuperiorNotBelongsSpecialization").get(userService.getPreferredLanguage(principal)));

            if (!superiorSpecialization.getName().equals(specializationMemberModifyRequest.getSpecializationName()))
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("SuperiorDifferentSpecialization").get(userService.getPreferredLanguage(principal)));

            for (Teacher teacher : teacherList) {
                if (teacher.getUsername().equals(specializationMemberModifyRequest.getName())) {
                    teacher.setSuperior(specializationMemberModifyRequest.getSuperiorName());
                    teacherRepository.save(teacher);
                    break;
                }
            }
        }

        specializationRepository.save(teacherSpecialization);

        return ResponseEntity.ok(Utils.languageDictionary.get("SuperiorModified").get(userService.getPreferredLanguage(principal)));
    }
}
