package com.example.abtesting.service.adapters.impl;

import com.example.abtesting.TrafficController.ApplicationRouting;
import com.example.abtesting.entity.Applicant;
import com.example.abtesting.exception.ApplicationException;
import com.example.abtesting.service.adapters.ApplicationAdapterService;

public class RoutingApplicationAdapter implements ApplicationAdapterService {

    private final ApplicationAdapterService v1;
    private final ApplicationAdapterService v2;
    private final ApplicationRouting routing;

    public RoutingApplicationAdapter(
            ApplicationAdapterService v1,
            ApplicationAdapterService v2,
            ApplicationRouting routing) {
        this.v1 = v1;
        this.v2 = v2;
        this.routing = routing;
    }

    private ApplicationAdapterService resolve(String userId) {
        boolean routeToV2 = routing.shouldRouteToNewVersion(userId);
        System.out.println("[AB-Routing] userId=" + userId + " -> routed to " + (routeToV2 ? "V2" : "V1"));
        return routeToV2 ? v2 : v1;

    }

    @Override
    public String createApplication(Applicant applicant) throws ApplicationException {
        return resolve(applicant.getUserId().toString()).createApplication(applicant);
    }

    @Override
    public Applicant getApplication(String applicationId, String userId) throws ApplicationException {
        return resolve(userId).getApplication(applicationId, userId);
    }
}
