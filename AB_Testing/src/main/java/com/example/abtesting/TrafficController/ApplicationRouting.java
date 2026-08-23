package com.example.abtesting.TrafficController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRouting {

    private int rolloutPercent = 0;

    public ApplicationRouting( @Value("${ab.applicant-rollout-percent:0}") int rolloutPercent){
        this.rolloutPercent = rolloutPercent;
    }

    private static int bucket(String userId){
        int hashUserId = userId.hashCode();
        // Ensure non-negative & bucket user id.
        return Math.abs(hashUserId) % 100;
    }

    public boolean shouldRouteToNewVersion(String userId){
        if (rolloutPercent <= 0){
            return false;
        }
        if (rolloutPercent >= 100) {
            return true;
        }

        int bucket = ApplicationRouting.bucket(userId);
        System.out.println("Routing to : ");
        return bucket < rolloutPercent;
    }
}
