package com.jobportal.repository;

import com.jobportal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByStatus(Company.CompanyStatus status);

    Boolean existsByCompanyName(String companyName);

    Boolean existsByTaxId(String taxId);

    @Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Company> findByCompanyNameContaining(String name);
}