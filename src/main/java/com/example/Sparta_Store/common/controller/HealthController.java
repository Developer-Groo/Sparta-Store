package com.example.Sparta_Store.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
