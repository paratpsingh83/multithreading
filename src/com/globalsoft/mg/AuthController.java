//package com.jobportal.controller;
//
//import com.jobportal.dto.*;
//import com.jobportal.service.AuthService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService service;
//
//    @PostMapping("/api/v1/login")
//    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.login(dto),"Login successful"));
//    }
//
//    @PostMapping("/api/v1/register")
//    public ResponseEntity<ApiResponseDTO<UserDTO>> register(@Valid @RequestBody UserRequestDTO dto) {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiResponseDTO.success( service.register(dto),"User registered successfully"));
//    }
//
//    @PostMapping("/api/v1/refresh-token")
//    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.refreshToken(refreshToken),"Token refreshed successfully"));
//    }
//
//    @PostMapping("/api/v1/logout")
//    public ResponseEntity<ApiResponseDTO<Void>> logout() {
//        service.logout();
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( null,"Logout successful"));
//    }
//
//    @PostMapping("/api/v1/forgot-password")
//    public ResponseEntity<ApiResponseDTO<Void>> forgotPassword(@RequestParam String email) {
//        service.forgotPassword(email);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( null,"Password reset email sent successfully"));
//    }
//
//    @PostMapping("/api/v1/reset-password")
//    public ResponseEntity<ApiResponseDTO<Void>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
//        service.resetPassword(token, newPassword);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( null,"Password reset successfully"));
//    }
//}


package com.globalsoft.mg;

import com.jobportal.dto.ApiResponseDTO;
import com.jobportal.dto.LoginRequestDTO;
import com.jobportal.dto.LoginResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.UserRequestDTO;
import com.jobportal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/api/v1/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.login(dto), "Login successful"));
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> register(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(service.register(dto), "User registered successfully"));
    }

    @PostMapping("/api/v1/refresh-token")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.refreshToken(refreshToken), "Token refreshed successfully"));
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout() {
        service.logout();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(null, "Logout successful"));
    }

    @PostMapping("/api/v1/forgot-password")
    public ResponseEntity<ApiResponseDTO<Void>> forgotPassword(@RequestParam String email) {
        service.forgotPassword(email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(null, "Password reset email sent successfully"));
    }

    @PostMapping("/api/v1/reset-password")
    public ResponseEntity<ApiResponseDTO<Void>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        service.resetPassword(token, newPassword);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(null, "Password reset successfully"));
    }
}
