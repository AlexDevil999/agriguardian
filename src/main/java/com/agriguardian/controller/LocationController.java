package com.agriguardian.controller;

import com.agriguardian.dto.LocationDto;
import com.agriguardian.dto.ViolationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {


    //for storing of history
    @PostMapping("/report")
    public ViolationDto notifyLocation(@Valid @RequestBody List<LocationDto> locations, Errors errors, Principal principal) {
        Collections.sort(locations);

        return ViolationDto.builder().userId(1L).violatedZones(Arrays.asList(1L)).build();
    }
}
