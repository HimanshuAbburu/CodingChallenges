package com.example.abtesting.service;

import com.example.abtesting.entity.Applicant;

public interface ApplicantService {
    String createApplicant(Applicant applicant) throws Exception;
    Applicant getUser (String id) throws Exception;
}
