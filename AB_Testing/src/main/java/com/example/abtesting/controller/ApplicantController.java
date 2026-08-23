package com.example.abtesting.controller;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.service.adapters.ApplicationAdapterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicantController {
    private final ApplicationAdapterService applicationService;

    public ApplicantController(ApplicationAdapterService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<String> create(@Validated @RequestBody Applicant applicant) throws Exception {
        return new ResponseEntity<>(applicationService.createApplication(applicant), HttpStatus.CREATED);
    }

    @GetMapping ("/{applicationId}/users/{userId}")
    public ResponseEntity<Applicant> getUser(
            @PathVariable String userId,
            @Validated @PathVariable UUID applicationId) throws Exception {
        return new ResponseEntity<>(applicationService.getApplication(applicationId.toString(), userId), HttpStatus.OK);
    }
}
