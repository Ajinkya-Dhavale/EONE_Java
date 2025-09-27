package com.java.eONE.controller;

import com.java.eONE.DTO.NotificationMessageDTO;
import com.java.eONE.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserNotifications(
            @PathVariable Long id,
            @RequestParam(required = false) Integer limit) {

        List<NotificationMessageDTO> notifications = notificationService.getUserNotifications(id, limit);

        if (notifications == null) {
            notifications = List.of(); // return empty list instead of null
        }

        return ResponseEntity.ok(notifications);
    }
}

