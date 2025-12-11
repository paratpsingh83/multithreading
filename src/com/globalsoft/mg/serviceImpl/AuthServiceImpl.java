//package com.jobportal.serviceImpl;
//
//import com.jobportal.dto.LoginRequestDTO;
//import com.jobportal.dto.LoginResponseDTO;
//import com.jobportal.dto.UserDTO;
//import com.jobportal.dto.UserRequestDTO;
//import com.jobportal.entity.User;
//import com.jobportal.exception.AuthenticationException;
//import com.jobportal.exception.ExceptionUtils;
//import com.jobportal.repository.UserRepository;
//import com.jobportal.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
//        // Find user by email or username
//        User user = userRepository.findByEmail(loginRequestDTO.getUsernameOrEmail())
//                .orElseGet(() -> userRepository.findByUsername(loginRequestDTO.getUsernameOrEmail())
//                        .orElseThrow(() -> new AuthenticationException("Invalid credentials")));
//
//        // Validate password
//        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
//            throw new AuthenticationException("Invalid credentials");
//        }
//
//        // Validate user is active
//        if (!user.getIsActive()) {
//            throw new AuthenticationException("Account is deactivated. Please contact administrator.");
//        }
//
//        // Generate JWT token (in real implementation, use JWT library)
//        String token = generateJwtToken(user);
//
//        UserDTO userDTO = UserDTO.fromEntity(user);
//        return new LoginResponseDTO(token, userDTO);
//    }
//
//    @Override
//    @Transactional
//    public UserDTO register(UserRequestDTO userRequestDTO) {
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
//        // Validate user type (restrict admin registration)
//        if (userRequestDTO.getUserType() == User.UserType.ADMIN) {
//            throw ExceptionUtils.businessError("Admin users cannot be registered through this endpoint");
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
//        user.setIsActive(true);
//
//        User savedUser = userRepository.save(user);
//        return UserDTO.fromEntity(savedUser);
//    }
//
//    @Override
//    public LoginResponseDTO refreshToken(String refreshToken) {
//        // Validate refresh token and generate new access token
//        // This is a simplified implementation
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            throw new AuthenticationException("Invalid refresh token");
//        }
//
//        // In real implementation, validate the refresh token and extract user info
//        // For now, return a placeholder implementation
//        throw new UnsupportedOperationException("Refresh token functionality not implemented yet");
//    }
//
//    @Override
//    public void logout() {
//        // In real implementation, this would blacklist the token or remove it from cache
//        // For stateless JWT, client should remove the token
//        // This method is here for completeness
//    }
//
//    @Override
//    @Transactional
//    public void forgotPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "email", email));
//
//        // Generate reset token
//        String resetToken = UUID.randomUUID().toString();
//        // In real implementation, save reset token with expiration to user entity or separate table
//
//        // Send email with reset link (simulated)
//        String resetLink = "https://yourapp.com/reset-password?token=" + resetToken;
//
//        // Log the reset link for demo purposes
//        System.out.println("Password reset link for " + email + ": " + resetLink);
//
//        // In real implementation, send email with reset link
//    }
//
//    @Override
//    @Transactional
//    public void resetPassword(String token, String newPassword) {
//        // Validate token
//        if (token == null || token.isEmpty()) {
//            throw ExceptionUtils.validationError("Reset token is required");
//        }
//
//        // Validate new password
//        if (newPassword == null || newPassword.length() < 6) {
//            throw ExceptionUtils.validationError("Password must be at least 6 characters long");
//        }
//
//        // In real implementation, validate token and find associated user
//        // For demo, we'll use a placeholder user ID
//        Long userId = 1L; // This would come from token validation
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", userId));
//
//        // Update password
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        // In real implementation, invalidate the used token
//    }
//
//    @Override
//    public Boolean validateToken(String token) {
//        // Validate JWT token
//        // This is a simplified implementation
//        if (token == null || token.isEmpty()) {
//            return false;
//        }
//
//        // In real implementation, use JWT library to validate token signature and expiration
//        // For demo, assume any non-empty token starting with "jwt-" is valid
//        return token.startsWith("jwt-");
//    }
//
//    private String generateJwtToken(User user) {
//        // In real implementation, use JWT library to generate token
//        // For demo, create a simple token format
//        String tokenData = user.getUserId() + "|" + user.getUsername() + "|" + user.getUserType() + "|" + System.currentTimeMillis();
//        return "jwt-" + UUID.nameUUIDFromBytes(tokenData.getBytes()).toString();
//    }
//}


package com.jobportal.serviceImpl;

import com.jobportal.dto.LoginRequestDTO;
import com.jobportal.dto.LoginResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.UserRequestDTO;
import com.jobportal.entity.User;
import com.jobportal.exception.AuthenticationException;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements com.jobportal.service.AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByEmail(loginRequestDTO.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByUsername(loginRequestDTO.getUsernameOrEmail())
                        .orElseThrow(() -> new AuthenticationException("Invalid credentials")));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AuthenticationException("Account is deactivated. Please contact administrator.");
        }

        String token = generateJwtToken(user);
        UserDTO userDTO = UserDTO.fromEntity(user);
        return new LoginResponseDTO(token, userDTO);
    }

    @Override
    @Transactional
    public UserDTO register(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw ExceptionUtils.duplicateResource("User", "email", userRequestDTO.getEmail());
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw ExceptionUtils.duplicateResource("User", "username", userRequestDTO.getUsername());
        }

        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().length() < 6) {
            throw ExceptionUtils.validationError("Password must be at least 6 characters long");
        }

        if (userRequestDTO.getUserType() == User.UserType.ADMIN) {
            throw ExceptionUtils.businessError("Admin users cannot be registered through this endpoint");
        }

        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword())); // ✅ encode
        user.setUserType(userRequestDTO.getUserType());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhone(userRequestDTO.getPhone());
        user.setProfilePicture(userRequestDTO.getProfilePicture());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthenticationException("Invalid refresh token");
        }
        throw new UnsupportedOperationException("Refresh token functionality not implemented yet");
    }

    @Override
    public void logout() {
        // stateless JWT-based logout is typically handled client-side (discard token)
        // optionally implement token blacklist
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "email", email));

        String resetToken = UUID.randomUUID().toString();
        String resetLink = "https://yourapp.com/reset-password?token=" + resetToken;

        // TODO: persist resetToken & expiry on user or separate table,
        // and send the resetLink via email
        System.out.println("Password reset link for " + email + ": " + resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isEmpty()) {
            throw ExceptionUtils.validationError("Reset token is required");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw ExceptionUtils.validationError("Password must be at least 6 characters long");
        }

        // TODO: lookup by token; below is placeholder logic
        Long userId = 1L;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return token.startsWith("jwt-");
    }

    private String generateJwtToken(User user) {
        String tokenData = user.getUserId() + "|" + user.getUsername() + "|" + user.getUserType() + "|" + System.currentTimeMillis();
        return "jwt-" + UUID.nameUUIDFromBytes(tokenData.getBytes()).toString();
    }
}