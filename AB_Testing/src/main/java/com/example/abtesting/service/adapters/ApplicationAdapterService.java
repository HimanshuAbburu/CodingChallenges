package com.example.abtesting.service.adapters;

import com.example.abtesting.entity.Applicant;
import com.example.abtesting.exception.ApplicationException;

import java.util.UUID;

public interface ApplicationAdapterService {
    String createApplication(Applicant applicant) throws ApplicationException;
    Applicant getApplication(String applicationId, String userId) throws ApplicationException;

}
