package com.example.abtesting.service.adapters.impl;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.exception.ApplicationException;
import com.example.abtesting.service.ApplicantService;
import com.example.abtesting.service.adapters.ApplicationAdapterService;

public class ApplicationAdapterImpl implements ApplicationAdapterService {

    private final ApplicantService applicantService;

    public ApplicationAdapterImpl(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    @Override
    public String createApplication(Applicant applicant) throws ApplicationException {

    try {
        return applicantService.createApplicant(applicant);
    } catch (Exception e) {
      throw new ApplicationException("Application submission failed", e);
    }
  }

    @Override
    public Applicant getApplication(String applicationId, String userId) throws ApplicationException {

        try {
            return applicantService.getUser(applicationId);
        } catch (Exception e) {
            throw new ApplicationException("V1 get failed", e);
        }
    }

}