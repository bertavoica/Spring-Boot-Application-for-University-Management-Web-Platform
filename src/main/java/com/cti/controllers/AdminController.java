package com.cti.controllers;

import com.cti.payload.request.AdminUpdateRequest;
import com.cti.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin-controller")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllAdmins(@RequestParam(name = "username", defaultValue = "") String username) {
        return ResponseEntity.ok(this.adminService.prepareAdminList(username));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAdmin(@RequestParam(name = "username", defaultValue = "") String username) {

        this.adminService.deleteByUsername(username);
        return ResponseEntity.ok(this.adminService.prepareAdminList(""));
    }

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateAdmin(@Valid @RequestBody AdminUpdateRequest adminUpdateRequest,
                                         Principal principal) {
        try {
            return ResponseEntity.ok(this.adminService.updateAdmin(adminUpdateRequest, principal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
