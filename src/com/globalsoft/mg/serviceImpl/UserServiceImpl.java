//package com.jobportal.serviceImpl;
//
//import com.jobportal.dto.UserDTO;
//import com.jobportal.dto.UserRequestDTO;
//import com.jobportal.entity.User;
//import com.jobportal.exception.ExceptionUtils;
//import com.jobportal.repository.UserRepository;
//import com.jobportal.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public UserDTO create(UserRequestDTO userRequestDTO) {
//        // Validate email uniqueness
//        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
//            throw ExceptionUtils.duplicateResource("User", "email", userRequestDTO.getEmail());
//        }
//
//        // Validate username uniqueness
//        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
//            throw ExceptionUtils.duplicateResource("User", "username", userRequestDTO.getUsername());
//        }
//
//        // Validate password strength
//        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().length() < 6) {
//            throw ExceptionUtils.validationError("Password must be at least 6 characters long");
//        }
//
//        User user = new User();
//        user.setUsername(userRequestDTO.getUsername());
//        user.setEmail(userRequestDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
//        user.setUserType(userRequestDTO.getUserType());
//        user.setFirstName(userRequestDTO.getFirstName());
//        user.setLastName(userRequestDTO.getLastName());
//        user.setPhone(userRequestDTO.getPhone());
//        user.setProfilePicture(userRequestDTO.getProfilePicture());
//        user.setIsActive(userRequestDTO.getIsActive() != null ? userRequestDTO.getIsActive() : true);
//
//        User savedUser = userRepository.save(user);
//        return UserDTO.fromEntity(savedUser);
//    }
//
//    @Override
//    public UserDTO getById(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", id));
//        return UserDTO.fromEntity(user);
//    }
//
//    @Override
//    public List<UserDTO> getAll() {
//        return userRepository.findAll()
//                .stream()
//                .map(UserDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public UserDTO update(Long id, UserRequestDTO userRequestDTO) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", id));
//
//        // Validate email uniqueness if changing email
//        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().equals(user.getEmail())) {
//            if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
//                throw ExceptionUtils.duplicateResource("User", "email", userRequestDTO.getEmail());
//            }
//            user.setEmail(userRequestDTO.getEmail());
//        }
//
//        // Validate username uniqueness if changing username
//        if (userRequestDTO.getUsername() != null && !userRequestDTO.getUsername().equals(user.getUsername())) {
//            if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
//                throw ExceptionUtils.duplicateResource("User", "username", userRequestDTO.getUsername());
//            }
//            user.setUsername(userRequestDTO.getUsername());
//        }
//
//        // Update password if provided
//        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
//            if (userRequestDTO.getPassword().length() < 6) {
//                throw ExceptionUtils.validationError("Password must be at least 6 characters long");
//            }
//            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
//        }
//
//        // Update other fields
//        if (userRequestDTO.getFirstName() != null) {
//            user.setFirstName(userRequestDTO.getFirstName());
//        }
//        if (userRequestDTO.getLastName() != null) {
//            user.setLastName(userRequestDTO.getLastName());
//        }
//        if (userRequestDTO.getPhone() != null) {
//            user.setPhone(userRequestDTO.getPhone());
//        }
//        if (userRequestDTO.getProfilePicture() != null) {
//            user.setProfilePicture(userRequestDTO.getProfilePicture());
//        }
//        if (userRequestDTO.getIsActive() != null) {
//            user.setIsActive(userRequestDTO.getIsActive());
//        }
//
//        User updatedUser = userRepository.save(user);
//        return UserDTO.fromEntity(updatedUser);
//    }
//
//    @Override
//    @Transactional
//    public void delete(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", id));
//
//        // Check if user has active placements
//        if (!user.getApplications().isEmpty()) {
//            throw ExceptionUtils.businessError("Cannot delete user with active applications");
//        }
//
//        // Instead of hard delete, soft delete by deactivating
//        user.setIsActive(false);
//        userRepository.save(user);
//    }
//
//    @Override
//    public UserDTO getProfile(Long id) {
//        return getById(id);
//    }
//
//    @Override
//    public Long getConsultantCount() {
//        return userRepository.countActiveConsultants();
//    }
//
//    @Override
//    public List<UserDTO> getConsultants() {
//        return userRepository.findAllActiveConsultants()
//                .stream()
//                .map(UserDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public UserDTO updateProfile(Long id, UserRequestDTO userRequestDTO) {
//        // For profile updates, don't allow changing user type or username
//        UserRequestDTO sanitizedRequest = new UserRequestDTO();
//        sanitizedRequest.setFirstName(userRequestDTO.getFirstName());
//        sanitizedRequest.setLastName(userRequestDTO.getLastName());
//        sanitizedRequest.setPhone(userRequestDTO.getPhone());
//        sanitizedRequest.setProfilePicture(userRequestDTO.getProfilePicture());
//
//        return update(id, sanitizedRequest);
//    }
//
//    @Override
//    public Boolean existsByEmail(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//    @Override
//    public Boolean existsByUsername(String username) {
//        return userRepository.existsByUsername(username);
//    }
//}


package com.jobportal.serviceImpl;

import com.jobportal.dto.UserDTO;
import com.jobportal.dto.UserRequestDTO;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO create(UserRequestDTO userRequestDTO) {
        log.info("Creating new user with email: {}", userRequestDTO.getEmail());

        validateUserCreation(userRequestDTO);

        User user = buildUserEntity(userRequestDTO);
        User savedUser = userRepository.save(user);

        log.info("Successfully created user with ID: {}", savedUser.getUserId());
        return UserDTO.fromEntity(savedUser);
    }

    @Override
    public UserDTO getById(Long id) {
        log.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return ExceptionUtils.resourceNotFound("User", "id", id);
                });

        return UserDTO.fromEntity(user);
    }

    @Override
    public List<UserDTO> getAll() {
        log.debug("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserRequestDTO userRequestDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with ID: {}", id);
                    return ExceptionUtils.resourceNotFound("User", "id", id);
                });

        validateUserUpdate(user, userRequestDTO);
        updateUserEntity(user, userRequestDTO);

        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user with ID: {}", id);

        return UserDTO.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Soft deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion with ID: {}", id);
                    return ExceptionUtils.resourceNotFound("User", "id", id);
                });

        // Industry practice: Soft delete instead of hard delete
        performSoftDelete(user);
        userRepository.save(user);

        log.info("Successfully soft deleted user with ID: {}", id);
    }

    @Override
    public UserDTO getProfile(Long id) {
        return getById(id);
    }

    @Override
    public Long getConsultantCount() {
        log.debug("Fetching active consultant count");
        return userRepository.countActiveConsultants();
    }

    @Override
    public List<UserDTO> getConsultants() {
        log.debug("Fetching all active consultants");

        return userRepository.findAllActiveConsultants()
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long id, UserRequestDTO userRequestDTO) {
        log.info("Updating profile for user ID: {}", id);

        // Create sanitized request for profile update (restrict certain fields)
        UserRequestDTO sanitizedRequest = createSanitizedProfileRequest(userRequestDTO);
        return update(id, sanitizedRequest);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ===== BUSINESS METHODS =====

    public Long getNewUsersCountLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.countUsersCreatedAfter(thirtyDaysAgo);
    }

    public Long getNewConsultantsCountLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.countConsultantsCreatedAfter(thirtyDaysAgo);
    }

    public List<UserDTO> getUsersByCriteria(User.UserType userType, Boolean isActive,
                                            LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching users by criteria - type: {}, active: {}, date range: {} to {}",
                userType, isActive, startDate, endDate);

        return userRepository.findUsersByCriteria(userType, isActive, startDate, endDate)
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void deactivateInactiveUsers(int daysInactive) {
//        log.info("Deactivating users inactive for {} days", daysInactive);
//
//        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysInactive);
//        List<User> inactiveUsers = userRepository.findInactiveUsersSince(cutoffDate);
//
//        if (!inactiveUsers.isEmpty()) {
//            inactiveUsers.forEach(user -> {
//                user.setIsActive(false);
//                log.debug("Deactivated user ID: {}", user.getUserId());
//            });
//            userRepository.saveAll(inactiveUsers);
//            log.info("Deactivated {} inactive users", inactiveUsers.size());
//        }
//    }

    // ===== PRIVATE HELPER METHODS =====

    private void validateUserCreation(UserRequestDTO userRequestDTO) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Duplicate email during user creation: {}", userRequestDTO.getEmail());
            throw ExceptionUtils.duplicateResource("User", "email", userRequestDTO.getEmail());
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            log.warn("Duplicate username during user creation: {}", userRequestDTO.getUsername());
            throw ExceptionUtils.duplicateResource("User", "username", userRequestDTO.getUsername());
        }

        // Validate password strength
        validatePassword(userRequestDTO.getPassword());
    }

    private void validateUserUpdate(User existingUser, UserRequestDTO userRequestDTO) {
        // Validate email uniqueness if changing email
        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
                log.warn("Duplicate email during user update: {}", userRequestDTO.getEmail());
                throw ExceptionUtils.duplicateResource("User", "email", userRequestDTO.getEmail());
            }
        }

        // Validate username uniqueness if changing username
        if (userRequestDTO.getUsername() != null && !userRequestDTO.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
                log.warn("Duplicate username during user update: {}", userRequestDTO.getUsername());
                throw ExceptionUtils.duplicateResource("User", "username", userRequestDTO.getUsername());
            }
        }

        // Validate password if provided
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            validatePassword(userRequestDTO.getPassword());
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw ExceptionUtils.validationError("Password must be at least 8 characters long");
        }

        // Industry practice: Add more password strength checks
        if (!password.matches(".*[A-Z].*")) {
            throw ExceptionUtils.validationError("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw ExceptionUtils.validationError("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw ExceptionUtils.validationError("Password must contain at least one digit");
        }
    }

//    private User buildUserEntity(UserRequestDTO userRequestDTO) {
//        return User.builder()
//                .username(userRequestDTO.getUsername())
//                .email(userRequestDTO.getEmail())
//                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
//                .userType(userRequestDTO.getUserType())
//                .firstName(userRequestDTO.getFirstName())
//                .lastName(userRequestDTO.getLastName())
//                .phone(userRequestDTO.getPhone())
//                .profilePicture(userRequestDTO.getProfilePicture())
//                .isActive(userRequestDTO.getIsActive() != null ? userRequestDTO.getIsActive() : true)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }

    private User buildUserEntity(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setUserType(userRequestDTO.getUserType());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhone(userRequestDTO.getPhone());
        user.setProfilePicture(userRequestDTO.getProfilePicture());
        user.setIsActive(userRequestDTO.getIsActive() != null ? userRequestDTO.getIsActive() : true);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }


    private void updateUserEntity(User user, UserRequestDTO userRequestDTO) {
        // Update basic fields
        if (userRequestDTO.getEmail() != null) {
            user.setEmail(userRequestDTO.getEmail());
        }
        if (userRequestDTO.getUsername() != null) {
            user.setUsername(userRequestDTO.getUsername());
        }
        if (userRequestDTO.getFirstName() != null) {
            user.setFirstName(userRequestDTO.getFirstName());
        }
        if (userRequestDTO.getLastName() != null) {
            user.setLastName(userRequestDTO.getLastName());
        }
        if (userRequestDTO.getPhone() != null) {
            user.setPhone(userRequestDTO.getPhone());
        }
        if (userRequestDTO.getProfilePicture() != null) {
            user.setProfilePicture(userRequestDTO.getProfilePicture());
        }
        if (userRequestDTO.getIsActive() != null) {
            user.setIsActive(userRequestDTO.getIsActive());
        }

        // Update password if provided
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        // Set update timestamp
        user.setUpdatedAt(LocalDateTime.now());
    }

    private void performSoftDelete(User user) {
        // Industry practice: Soft delete with deactivation and audit trail
        user.setIsActive(false);
//        user.setDeletedAt(LocalDateTime.now());
        user.setEmail(user.getEmail() + "_deleted_" + System.currentTimeMillis());
        user.setUsername(user.getUsername() + "_deleted_" + System.currentTimeMillis());
    }

    private UserRequestDTO createSanitizedProfileRequest(UserRequestDTO originalRequest) {
        // Profile updates should not change sensitive fields
        UserRequestDTO sanitized = new UserRequestDTO();
        sanitized.setFirstName(originalRequest.getFirstName());
        sanitized.setLastName(originalRequest.getLastName());
        sanitized.setPhone(originalRequest.getPhone());
        sanitized.setProfilePicture(originalRequest.getProfilePicture());
        return sanitized;
    }
}