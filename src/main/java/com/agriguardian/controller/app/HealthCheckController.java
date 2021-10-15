package com.agriguardian.controller.app;

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
    private static final String date = new Date().toString();

    @GetMapping("/check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/date")
    public ResponseEntity<String> getInitialDate() {
        log.debug("[getInitialDate] request of date");
        return ResponseEntity.ok(date);
    }

    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok("15.10");
    }
}
