package com.example.abtesting.service.adapters.implementation;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.exception.ApplicationException;
import com.example.abtesting.service.ApplicantV2Service;
import com.example.abtesting.service.adapters.ApplicationAdapterService;

public class ApplicantV2Adapter implements ApplicationAdapterService {
    private final ApplicantV2Service applicantV2Service;

    public ApplicantV2Adapter(ApplicantV2Service applicantV2Service) {
        this.applicantV2Service = applicantV2Service;
    }

    @Override
    public String createApplication(Applicant applicant) throws ApplicationException {
        try {
            return applicantV2Service.createApplication(applicant);
        } catch (Exception e) {
            throw new ApplicationException("V2 create failed", e);
        }
    }

    @Override
    public Applicant getApplication(String applicationId, String userId) throws ApplicationException {
        try {
            return applicantV2Service.getUser(applicationId, userId);
        } catch (Exception e) {
            throw new ApplicationException("V2 get failed", e);
        }

    }
}
