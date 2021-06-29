package com.agriguardian.controller;

import com.agriguardian.dto.AddUserDto;
import com.agriguardian.util.DtoValidationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@Log4j2
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody AddUserDto dto, Errors errors, Principal principal) {
        DtoValidationUtil.handleErrors(errors);


        //todo store new user
        return null;
    }
}
