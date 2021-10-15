package com.agriguardian.controller;

import com.agriguardian.dto.RegistrationConfirmationDto;
import com.agriguardian.dto.ResendConfirmationDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    private final AppUserService appUserService;

    @PostMapping("/confirmation")
    public ResponseUserDto confirmUserMaster
            (@Valid @RequestBody RegistrationConfirmationDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);
        AppUser currentUser =appUserService.findByUsernameOrThrowNotFound(dto.getUsername());
        AppUser saved = appUserService.activateUser(currentUser, dto.getConfirmationCode());
        log.debug("user {} has confirmed email", dto.getUsername());
        return ResponseUserDto.of(saved);
    }

    @PostMapping("/resend")
    public ResponseUserDto resendConfirmation
            (@RequestBody @Valid ResendConfirmationDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);

        log.debug("user {} requesting resending of an email confirmation", dto.getUsername());

        AppUser currentUser =appUserService.findByUsernameOrThrowNotFound(dto.getUsername());
        appUserService.sendEmailConfirmationForUser(currentUser);
        return ResponseUserDto.of(currentUser);
    }
}
