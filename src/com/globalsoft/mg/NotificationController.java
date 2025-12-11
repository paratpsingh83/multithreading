package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<NotificationDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Notifications retrieved successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<NotificationDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Notification retrieved successfully"));
    }

    @GetMapping("/api/v1/unread")
    public ResponseEntity<ApiResponseDTO<List<NotificationDTO>>> getUnread() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getUnread(),"Unread notifications retrieved successfully"));
    }

    @PutMapping("/api/v1/{id}/read")
    public ResponseEntity<ApiResponseDTO<NotificationDTO>> markAsRead(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.markAsRead(id),"Notification marked as read successfully"));
    }

    @PutMapping("/api/v1/read-all")
    public ResponseEntity<ApiResponseDTO<Void>> markAllAsRead() {
        service.markAllAsRead();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(null,"All notifications marked as read successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Notification deleted successfully"));
    }
}