package com.jobportal.service;

import com.jobportal.dto.LoginRequestDTO;
import com.jobportal.dto.LoginResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.UserRequestDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    UserDTO register(UserRequestDTO userRequestDTO);

    LoginResponseDTO refreshToken(String refreshToken);

    void logout();

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    Boolean validateToken(String token);
}