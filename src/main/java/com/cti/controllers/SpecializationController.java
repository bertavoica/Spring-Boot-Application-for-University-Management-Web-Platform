package com.cti.controllers;

import com.cti.exception.*;
import com.cti.payload.request.SpecializationAddRequest;
import com.cti.payload.request.SpecializationMemberAddRequest;
import com.cti.payload.request.SpecializationMemberModifyRequest;
import com.cti.payload.request.SpecializationUpdateRequest;
import com.cti.repository.SpecializationRepository;
import com.cti.service.SpecializationService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/specialization-controller")
public class SpecializationController {
    @Autowired
    private SpecializationService specializationService;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
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
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.TEACHER_ADDED_SPECIALIZATION).get(userService.getPreferredLanguage(principal)) + " " + specializationMemberAddRequest.getSpecializationName());
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
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_HAS_SUPERIORS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @GetMapping(value="/member")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSpecializationGraph(@RequestParam(name = "stringName", defaultValue = "") java.lang.String stringName,
                                                    Principal principal) throws JSONException {
        try {
            return ResponseEntity.ok(this.specializationService.getSpecializationGraph(stringName));
        } catch (SpecializationNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SPECIALIZATION_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @PutMapping(value="/member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifySpecializationMember(@Valid @RequestBody SpecializationMemberModifyRequest specializationMemberModifyRequest,
                                                        Principal principal) {
        try {
            this.specializationService.modifySpecializationMember(specializationMemberModifyRequest);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.SUPERIOR_MODIFIED).get(userService.getPreferredLanguage(principal)));
        } catch (TeacherNotBelongsSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_NOT_BELONGS_SPECIALIZATION).get(userService.getPreferredLanguage(principal)) + " " + specializationMemberModifyRequest.getName());
        } catch (TeacherBelongsDifferentSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_BELONS_DIFFERENT_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        } catch (SuperiorNotBelongsSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SUPERIOR_NOT_BELONGS_TO_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        } catch (SuperiorDifferentSpecializationException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.SUPERIOR_DIFFERENT_SPECIALIZATION).get(userService.getPreferredLanguage(principal)));
        }
    }
}
