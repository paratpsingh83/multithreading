package com.jobportal.dto;

import com.jobportal.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private User.UserType userType;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePicture;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<UserSkillDTO> skills;


        public static UserDTO fromEntity(User user) {
            UserDTO dto = new UserDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setUserType(user.getUserType());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPhone(user.getPhone());
            dto.setProfilePicture(user.getProfilePicture());
            dto.setIsActive(user.getIsActive());
            dto.setCreatedAt(user.getCreatedAt());
            // Map skills if needed
            return dto;
        }

}