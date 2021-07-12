package com.agriguardian.controller;

import com.agriguardian.dto.AddUserFollowerDto;
import com.agriguardian.dto.AddUserMasterDto;
import com.agriguardian.dto.ResponseUserDto;
import com.agriguardian.entity.*;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.service.AppUserService;
import com.agriguardian.util.RandomCodeGenerator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final AppUserService appUserService;
    private final AppUserTeamGroupRepository appUserTeamGroupRepository;


    @GetMapping(value = "/current")
    public AppUser getCurrentUser(Principal principal) {
        log.debug("[getCurrentUser] for: " + principal.getName());
        return appUserService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

    }

    @PostMapping("/master")
    public ResponseUserDto addUserMaster(@Valid @RequestBody AddUserMasterDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);

        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());
        appUser.addCreditCard(dto.buildCreditCard());

        AppUser saved = appUserService.saveUserMasterIfNotExist(appUser, Status.ACTIVATED, dto.getWithTeamGroup());

        return ResponseUserDto.of(saved);
    }

//    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/follower")
    public ResponseUserDto addUserFollower(@Valid @RequestBody AddUserFollowerDto dto, Errors errors) {
//            , Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        //todo move to service
//        AppUser admin = appUserService.findByUsername(principal.getName()).get();
//        AppUserTeamGroup aptg = admin.getAppUserTeamGroups().stream().filter(utg -> dto.getGroupId().equals(utg.getTeamGroup().getId()))
//                .findAny().orElseThrow(() -> new NotFoundException("group " + dto.getGroupId() + " not found"));



        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserFollowerIfNotExist(appUser, Status.ACTIVATED);

        return ResponseUserDto.of(saved);
    }
}
