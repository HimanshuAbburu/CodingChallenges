package com.example.abtesting.service.impl;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.repository.ApplicantRepository;
import com.example.abtesting.service.ApplicantService;
import org.springframework.stereotype.Service;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;

    public ApplicantServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    public String createApplicant(Applicant applicant){
        return applicantRepository.save(applicant).getId().toString();
    }

    @Override
    public Applicant getUser(String id){
        return applicantRepository.getReferenceById(id);
    }
}
