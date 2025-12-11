package com.jobportal.service;

import com.jobportal.dto.UserDTO;
import com.jobportal.dto.UserRequestDTO;

import java.util.List;

public interface UserService {
    UserDTO create(UserRequestDTO userRequestDTO);

    UserDTO getById(Long id);

    List<UserDTO> getAll();

    UserDTO update(Long id, UserRequestDTO userRequestDTO);

    void delete(Long id);

    UserDTO getProfile(Long id);

    Long getConsultantCount();

    List<UserDTO> getConsultants();

    UserDTO updateProfile(Long id, UserRequestDTO userRequestDTO);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}