package com.example.abtesting.repository;


import com.example.abtesting.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {}
