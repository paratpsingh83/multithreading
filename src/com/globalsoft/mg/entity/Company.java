package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "companies")
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    
    @Column(nullable = false)
    private String companyName;
    
    private String industry;
    private String sizeRange;
    private String website;
    private String logo;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String taxId;
    
    @Enumerated(EnumType.STRING)
    private CompanyStatus status = CompanyStatus.ACTIVE;
    
    // One-to-Many mappings
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();
    
    // Many-to-Many with User
    @ManyToMany(mappedBy = "companies")
    private List<User> users = new ArrayList<>();
    
    public enum CompanyStatus {
        ACTIVE, INACTIVE, PENDING
    }
}