package com.jobportal.repository;

import com.jobportal.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {


    List<Placement> findByPlacementStatus(Placement.PlacementStatus status);

    long countByPlacementStatus(Placement.PlacementStatus status);


    List<Placement> findByContractType(Placement.ContractType contractType);

    @Query("SELECT p FROM Placement p WHERE p.application.consultant.userId = :consultantId")
    List<Placement> findByConsultantId(@Param("consultantId") Long consultantId);

    @Query("SELECT p FROM Placement p WHERE p.application.job.company.companyId = :companyId")
    List<Placement> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(p) FROM Placement p WHERE p.placementStatus = 'ACTIVE'")
    Long countActivePlacements();

    @Query("SELECT COUNT(p) FROM Placement p WHERE p.contractType = 'FULL_TIME'")
    Long countFullTimePlacements();

    @Query("SELECT COUNT(p) FROM Placement p WHERE p.contractType = 'CONTRACT'")
    Long countContractPlacements();

    @Query("SELECT COUNT(p) FROM Placement p WHERE p.createdAt >= :startDate")
    Long countPlacementsSince(@Param("startDate") LocalDate startDate);

    @Query("SELECT p FROM Placement p WHERE p.startDate <= CURRENT_DATE AND (p.endDate IS NULL OR p.endDate >= CURRENT_DATE)")
    List<Placement> findActivePlacements();

}