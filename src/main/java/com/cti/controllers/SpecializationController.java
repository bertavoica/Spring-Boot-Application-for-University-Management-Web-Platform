package com.cti.controllers;

import com.cti.exception.*;
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
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
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
        try {
            this.specializationService.addSpecialization(specializationAddRequest);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_ADDED).get(userService.getPreferredLanguage(principal)) + " " + specializationAddRequest.getName());
        } catch (SpecializationExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + specializationAddRequest.getName());
        }
    }

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSpecialization(@Valid @RequestBody SpecializationUpdateRequest specializationUpdateRequest,
                                                  Principal principal) {
        try {
            this.specializationService.updateSpecialization(specializationUpdateRequest);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_UPDATED).get(userService.getPreferredLanguage(principal)));
        } catch (SpecializationExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + specializationUpdateRequest.getName());
        } catch (SpecializationNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSpecialization(@RequestParam(name = "uniqueId", defaultValue = "") java.lang.String uniqueId,
                                                  Principal principal) {

        this.specializationService.deleteSpecialization(uniqueId);
        return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_DELETED).get(userService.getPreferredLanguage(principal)));
    }

    @PostMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSpecializationMember(@Valid @RequestBody SpecializationMemberAddRequest specializationMemberAddRequest,
                                                     Principal principal) {
        try {
            this.specializationService.addSpecializationMember(specializationMemberAddRequest);
        } catch (SpecializationNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_NOT_EXISTS).get(userService.getPreferredLanguage(principal))  + " " + specializationMemberAddRequest.getSpecializationName());
        } catch (TeacherBelongsToSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_BELONGS_TO_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        } catch (SuperiorNotBelongsSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SUPERIOR_NOT_BELONGS_TO_SPECIALIZATION).get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSuperior());
        } catch (TeacherNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getName());
        } catch (SuperiorDifferentSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SUPERIOR_DIFFERENT_SPECIALIZATION).get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSuperior());
        }
    }

    @DeleteMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSpecializationMember(@RequestParam(name = "stringName", defaultValue = "") java.lang.String stringName,
                                                        @RequestParam(name = "username", defaultValue = "") java.lang.String username,
                                                        Principal principal) {
        try {
            this.specializationService.deleteSpecializationMember(stringName, username);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.TEACHER_REMOVED_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        } catch (TeacherNotBelongsSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_NOT_BELONGS_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        } catch (SpecializationNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (TeacherNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (TeacherHasSuperiorsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_HAS_SUPERIORS).get(userService.getPreferredLanguage(principal)) + " " + totalChildNodes);
        }
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
