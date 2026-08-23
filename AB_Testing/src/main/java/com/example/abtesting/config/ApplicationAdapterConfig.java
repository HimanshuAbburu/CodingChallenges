package com.example.abtesting.config;

import com.example.abtesting.TrafficController.ApplicationRouting;
import com.example.abtesting.service.ApplicantService;
import com.example.abtesting.service.ApplicantV2Service;
import com.example.abtesting.service.adapters.ApplicationAdapterService;
import com.example.abtesting.service.adapters.implementation.ApplicantV2Adapter;
import com.example.abtesting.service.adapters.implementation.ApplicationV1Adapter;
import com.example.abtesting.service.adapters.impl.RoutingApplicationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationAdapterConfig {
    @Bean
    public ApplicationAdapterService v1Adapter(ApplicantService applicantService) {
        return new ApplicationV1Adapter(applicantService);
    }

    @Bean
    public ApplicationAdapterService v2Adapter(ApplicantV2Service applicantV2Service) {
        return new ApplicantV2Adapter(applicantV2Service);
    }

    // This is the ONLY one that should be @Primary / unqualified,
    // since it's the one the controller actually depends on.
    @Bean
    @Primary
    public ApplicationAdapterService applicationAdapterService(
            ApplicationRouting routing,
            @org.springframework.beans.factory.annotation.Qualifier("v1Adapter") ApplicationAdapterService v1,
            @org.springframework.beans.factory.annotation.Qualifier("v2Adapter") ApplicationAdapterService v2) {
        return new RoutingApplicationAdapter(v1, v2, routing);
    }
}
