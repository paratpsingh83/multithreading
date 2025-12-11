package com.jobportal.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private UserDTO user;



    public LoginResponseDTO(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}