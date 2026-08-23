package com.example.abtesting.service;

import com.example.abtesting.entity.Applicant;

public interface ApplicantV2Service {
    String createApplication(Applicant applicant) throws Exception;
    Applicant getUser (String applicationId, String userId) throws Exception;

}
