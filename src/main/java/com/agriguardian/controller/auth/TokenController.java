package com.agriguardian.controller.auth;

import com.agriguardian.dto.auth.AuthRequestDto;
import com.agriguardian.dto.auth.AuthResponseDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.security.JwtProvider;
import com.agriguardian.service.security.PasswordEncryptor;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
class TokenController {
    private final AppUserService appUserService;
    private final JwtProvider jwtProvider;
    private final PasswordEncryptor passwordEncryptor;


    @PostMapping(value = "/auth")
    public AuthResponseDto generateTokens(@Valid @RequestBody AuthRequestDto request, Errors errors) {
        ValidationDto.handleErrors(errors);

        Optional<AppUser> user = appUserService.findByUsername(request.getUsername().toLowerCase().trim());

        if (!user.isPresent() || !passwordEncryptor.matches(request.getPassword(), user.get().getPassword())) {
            throw new BadCredentialsException("Bad credentials") ;
        }

        if (Status.REGISTRATION == user.get().getStatus()) {
            throw new AccessDeniedException("Unfinished registration: required confirmation of email");
        }

        if (Status.DEACTIVATED == user.get().getStatus()) {
            throw new AccessDeniedException("Account status is " + user.get().getStatus() + ". Activate your account first");
        }

        log.debug("[generateTokens] for user: " + user.get().getId() + "; username: " + user.get().getUsername());
        return jwtProvider.token(user.get());
    }

    @GetMapping(value = "/refresh")
    public AuthResponseDto refreshTokens(@RequestHeader("Authorization") String refreshBearer) {
        if (refreshBearer != null && refreshBearer.startsWith("Bearer ")) {
            String token = refreshBearer.substring(7);
            //todo add storing and verifying of DB
            //todo add token invalidation
            jwtProvider.validateSign(token);

            AppUser user = appUserService.findByUsername(jwtProvider.getOwner(token))
                    .orElseThrow(() -> new NotFoundException("Owner of the token not found"));


            if (Status.DEACTIVATED == user.getStatus()) {
                throw new AccessDeniedException("Account status is " + user.getStatus() + ". Activate your account first");
            }

            AuthResponseDto jwt = jwtProvider.token(user);
            log.debug("refreshTokens: " + jwt);
            return jwt;
        } else {
            log.warn("[refreshTokens] incorrect authorization header: " + refreshBearer);
            throw new BadRequestException("Incorrect authorization header");
        }
    }
}