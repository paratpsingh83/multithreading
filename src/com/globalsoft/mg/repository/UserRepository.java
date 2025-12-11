////package com.jobportal.repository;
////
////import com.jobportal.entity.User;
////import org.springframework.data.jpa.repository.JpaRepository;
////import org.springframework.data.jpa.repository.Query;
////import org.springframework.data.repository.query.Param;
////import org.springframework.stereotype.Repository;
////
////import java.time.LocalDateTime;
////import java.util.List;
////import java.util.Optional;
////
////@Repository
////public interface UserRepository extends JpaRepository<User, Long> {
////
////    Optional<User> findByEmail(String email);
////
////    Optional<User> findByUsername(String username);
////
////    Boolean existsByEmail(String email);
////
////    Boolean existsByUsername(String username);
////
////    List<User> findByUserType(User.UserType userType);
////
////    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 'CONSULTANT' AND u.isActive = true")
////    Long countActiveConsultants();
////
////    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 'CONSULTANT' AND u.createdAt >= CURRENT_DATE - 30")
////    Long countNewConsultantsThisMonth();
////
////    @Query("SELECT u FROM User u WHERE u.userType = 'CONSULTANT' AND u.isActive = true")
////    List<User> findAllActiveConsultants();
////
////    long countByIsActive(Boolean isActive);
////
////    List<User> findByIsActive(Boolean isActive);
////
////    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= CURRENT_DATE - 30")
////    long countNewUsersThisMonth();
////
////
////    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = 'CONSULTANT' AND u.createdAt >= :startDate")
////    long countNewConsultantsThisMonth(@Param("startDate") LocalDateTime startDate);
////
////    // Method 2: Using derived query method (alternative)
////    long countByUserTypeAndCreatedAtAfter(String userType, LocalDateTime date);
////
////    // Method 3: Using native query (if needed for complex date operations)
////    @Query(value = "SELECT COUNT(*) FROM users u WHERE u.user_type = 'CONSULTANT' AND u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)", nativeQuery = true)
////    long countNewConsultantsThisMonthNative();
////
////
////
////}
//
//package com.jobportal.repository;
//
//import com.jobportal.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    // ===== BASIC CRUD OPERATIONS =====
//    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
//    Boolean existsByEmail(String email);
//    Boolean existsByUsername(String email);
//
//    // ===== BUSINESS QUERIES WITH PROPER NAMING =====
//
//    // User type based queries
//    List<User> findByUserType(User.UserType userType);
//
//    // Active status queries
//    long countByIsActive(Boolean isActive);
//    List<User> findByIsActive(Boolean isActive);
//
//    // ===== PERFORMANCE-OPTIMIZED QUERIES WITH @Query =====
//
//    // Consultant statistics
//    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
//    Long countActiveConsultants();
//
//    @Query("SELECT u FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
//    List<User> findAllActiveConsultants();
//
//    // ===== TIME-BASED QUERIES WITH PARAMETERS (RECOMMENDED) =====
//
//    /**
//     * Count new users created after specified date
//     * Industry best practice: Use parameterized queries for date operations
//     */
//    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
//    long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);
//
//    /**
//     * Count new consultants created after specified date
//     */
//    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.createdAt >= :startDate")
//    long countConsultantsCreatedAfter(@Param("startDate") LocalDateTime startDate);
//
//    // ===== DERIVED QUERIES FOR SIMPLE OPERATIONS =====
//
//    /**
//     * Count users by type created after specified date
//     * Industry practice: Use derived queries for simple conditions
//     */
//    long countByUserTypeAndCreatedAtAfter(User.UserType userType, LocalDateTime date);
//
//    // ===== NATIVE QUERIES FOR COMPLEX OPERATIONS =====
//
//    /**
//     * Native query for complex date operations that are database-specific
//     * Use only when JPQL cannot handle the complexity
//     */
//    @Query(value = """
//        SELECT COUNT(*) FROM users u
//        WHERE u.user_type = 'CONSULTANT'
//        AND u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
//        AND u.is_active = true
//        """, nativeQuery = true)
//    long countActiveConsultantsLast30Days();
//
//    // ===== ADVANCED BUSINESS QUERIES =====
//
//    /**
//     * Find users by multiple criteria with pagination support
//     */
//    @Query("SELECT u FROM User u WHERE " +
//            "(:userType IS NULL OR u.userType = :userType) AND " +
//            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
//            "(:startDate IS NULL OR u.createdAt >= :startDate) AND " +
//            "(:endDate IS NULL OR u.createdAt <= :endDate)")
//    List<User> findUsersByCriteria(
//            @Param("userType") User.UserType userType,
//            @Param("isActive") Boolean isActive,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );
//
//    /**
//     * Get user registration statistics by period
//     */
//    @Query(value = """
//        SELECT DATE(u.created_at) as registrationDate, COUNT(*) as userCount
//        FROM users u
//        WHERE u.created_at >= :startDate
//        GROUP BY DATE(u.created_at)
//        ORDER BY registrationDate DESC
//        """, nativeQuery = true)
//    List<Object[]> getUserRegistrationStats(@Param("startDate") LocalDateTime startDate);
//
//    /**
//     * Find inactive users who haven't logged in for specified days
//     */
//    @Query("SELECT u FROM User u WHERE u.lastLoginDate < :cutoffDate AND u.isActive = true")
//    List<User> findInactiveUsersSince(@Param("cutoffDate") LocalDateTime cutoffDate);
//
//    /**
//     * Bulk update user status
//     */
//    @Query("UPDATE User u SET u.isActive = :isActive WHERE u.lastLoginDate < :cutoffDate")
//    void deactivateInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("isActive") Boolean isActive);
//}


//package com.jobportal.repository;
//
//import com.jobportal.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    // ===== BASIC CRUD OPERATIONS =====
//    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
//    Boolean existsByEmail(String email);
//    Boolean existsByUsername(String username);
//
//    // ===== BUSINESS QUERIES WITH PROPER NAMING =====
//
//    // User type based queries
//    List<User> findByUserType(User.UserType userType);
//
//    // Active status queries
//    long countByIsActive(Boolean isActive);
//    List<User> findByIsActive(Boolean isActive);
//
//    // ===== PERFORMANCE-OPTIMIZED QUERIES WITH @Query =====
//
//    // Consultant statistics
//    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
//    Long countActiveConsultants();
//
//    @Query("SELECT u FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
//    List<User> findAllActiveConsultants();
//
//    // ===== TIME-BASED QUERIES WITH PARAMETERS (RECOMMENDED) =====
//
//    /**
//     * Count new users created after specified date
//     * Industry best practice: Use parameterized queries for date operations
//     */
//    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
//    long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);
//
//    /**
//     * Count new consultants created after specified date
//     */
//    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.createdAt >= :startDate")
//    long countConsultantsCreatedAfter(@Param("startDate") LocalDateTime startDate);
//
//    // ===== DERIVED QUERIES FOR SIMPLE OPERATIONS =====
//
//    /**
//     * Count users by type created after specified date
//     * Industry practice: Use derived queries for simple conditions
//     */
//    long countByUserTypeAndCreatedAtAfter(User.UserType userType, LocalDateTime date);
//
//    // ===== NATIVE QUERIES FOR COMPLEX OPERATIONS =====
//
//    /**
//     * Native query for complex date operations that are database-specific
//     * Use only when JPQL cannot handle the complexity
//     */
//    @Query(value = """
//        SELECT COUNT(*) FROM users u
//        WHERE u.user_type = 'CONSULTANT'
//        AND u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
//        AND u.is_active = true
//        """, nativeQuery = true)
//    long countActiveConsultantsLast30Days();
//
//    // ===== ADVANCED BUSINESS QUERIES =====
//
//    /**
//     * Find users by multiple criteria with pagination support
//     */
//    @Query("SELECT u FROM User u WHERE " +
//            "(:userType IS NULL OR u.userType = :userType) AND " +
//            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
//            "(:startDate IS NULL OR u.createdAt >= :startDate) AND " +
//            "(:endDate IS NULL OR u.createdAt <= :endDate)")
//    List<User> findUsersByCriteria(
//            @Param("userType") User.UserType userType,
//            @Param("isActive") Boolean isActive,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );
//
//    /**
//     * Get user registration statistics by period
//     */
//    @Query(value = """
//        SELECT DATE(u.created_at) as registrationDate, COUNT(*) as userCount
//        FROM users u
//        WHERE u.created_at >= :startDate
//        GROUP BY DATE(u.created_at)
//        ORDER BY registrationDate DESC
//        """, nativeQuery = true)
//    List<Object[]> getUserRegistrationStats(@Param("startDate") LocalDateTime startDate);
//
//    /**
//     * Find inactive users who haven't logged in for specified days
//     */
//    @Query("SELECT u FROM User u WHERE u.lastLoginDate < :cutoffDate AND u.isActive = true")
//    List<User> findInactiveUsersSince(@Param("cutoffDate") LocalDateTime cutoffDate);
//
//    /**
//     * Count users by user type
//     */
//    long countByUserType(User.UserType userType);
//}


package com.jobportal.repository;

import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===== BASIC CRUD OPERATIONS =====
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    // ===== BUSINESS QUERIES WITH PROPER NAMING =====

    // User type based queries
    List<User> findByUserType(User.UserType userType);

    // Active status queries
    long countByIsActive(Boolean isActive);
    List<User> findByIsActive(Boolean isActive);

    // ===== PERFORMANCE-OPTIMIZED QUERIES WITH @Query =====

    // Consultant statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
    Long countActiveConsultants();

    @Query("SELECT u FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.isActive = true")
    List<User> findAllActiveConsultants();

    // ===== TIME-BASED QUERIES WITH PARAMETERS (RECOMMENDED) =====

    /**
     * Count new users created after specified date
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Count new consultants created after specified date
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = com.jobportal.entity.User.UserType.CONSULTANT AND u.createdAt >= :startDate")
    long countConsultantsCreatedAfter(@Param("startDate") LocalDateTime startDate);

    // ===== DERIVED QUERIES FOR SIMPLE OPERATIONS =====

    /**
     * Count users by type created after specified date
     */
    long countByUserTypeAndCreatedAtAfter(User.UserType userType, LocalDateTime date);

    // ===== NATIVE QUERIES FOR COMPLEX OPERATIONS =====

    /**
     * Native query for complex date operations
     */
    @Query(value = """
        SELECT COUNT(*) FROM users u 
        WHERE u.user_type = 'CONSULTANT' 
        AND u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
        AND u.is_active = true
        """, nativeQuery = true)
    long countActiveConsultantsLast30Days();

    // ===== ADVANCED BUSINESS QUERIES =====

    /**
     * Find users by multiple criteria with pagination support
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:userType IS NULL OR u.userType = :userType) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "(:startDate IS NULL OR u.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR u.createdAt <= :endDate)")
    List<User> findUsersByCriteria(
            @Param("userType") User.UserType userType,
            @Param("isActive") Boolean isActive,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get user registration statistics by period
     */
    @Query(value = """
        SELECT DATE(u.created_at) as registrationDate, COUNT(*) as userCount 
        FROM users u 
        WHERE u.created_at >= :startDate 
        GROUP BY DATE(u.created_at) 
        ORDER BY registrationDate DESC
        """, nativeQuery = true)
    List<Object[]> getUserRegistrationStats(@Param("startDate") LocalDateTime startDate);

    /**
     * Count users by user type
     */
    long countByUserType(User.UserType userType);
}