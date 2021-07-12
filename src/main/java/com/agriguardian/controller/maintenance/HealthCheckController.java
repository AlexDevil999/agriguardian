package com.agriguardian.controller.maintenance;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping(value = "/health")
@Log4j2
@AllArgsConstructor
public class HealthCheckController {

    @GetMapping("/check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/date")
    public ResponseEntity<String> getInitialDate() {
        return ResponseEntity.ok(new Date().toString());
    }

    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok("12.07");
    }
}
