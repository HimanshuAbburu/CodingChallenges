package com.example.abtesting.service.impl;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.repository.ApplicantRepository;
import com.example.abtesting.service.ApplicantV2Service;
import org.springframework.stereotype.Service;

@Service
public class ApplicantV2ServiceImpl implements ApplicantV2Service {

    private final ApplicantRepository applicantRepository;

    public ApplicantV2ServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    public String createApplication(Applicant applicant) throws Exception {
        // new/refactored logic, can call internal methods with any names you like
        if (applicant.getEmail().contains("youmail.com")){
            throw new Exception("Invalid email");
        }
        return applicantRepository.save(applicant).getId();
    }

    @Override
    public Applicant getUser(String applicationId, String userId) throws Exception {
        if (userId.contains("1")){
            throw new Exception("Invalid userId");
        }
        return applicantRepository.getReferenceById(String.valueOf(applicationId));
    }
}
