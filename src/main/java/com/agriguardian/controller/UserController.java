package com.agriguardian.controller;

import com.agriguardian.dto.AddUserMasterDto;
import com.agriguardian.dto.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.AppUserTeamGroup;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.service.AppUserService;
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
@RequestMapping("/api/v1/users")
public class UserController {
    private final AppUserService appUserService;
    private final AppUserTeamGroupRepository appUserTeamGroupRepository;

    @PostMapping("/master")
    public ResponseUserDto addUserMaster(@Valid @RequestBody AddUserMasterDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);

        if (appUserService.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("user " + dto.getUsername() + " already exists");
        }

        AppUser appUser = dto.buildUser();

        //todo redo
        long time = System.currentTimeMillis();
        appUser.setStatus(Status.ACTIVATED);
        appUser.setUserRole(UserRole.USER_MASTER);
        appUser.setOtp("1111111");
        appUser.setCreatedOnMs(time);
        appUser.setOtpCreatedOnMs(time);

        appUser.addCreditCard(dto.buildCreditCard());
        appUser.addUserInfo(dto.buildUserInfo());



        AppUser saved = appUserService.save(appUser);

        //todo fixme
        if (dto.getWithTeamGroup()) {
            TeamGroup tg = TeamGroup.builder()
                    .vulnerableInvitationCode("000000")
                    .guardianInvitationCode("111111")
                    .name(dto.getName() + "'s group")
                    .owner(appUser)
                    .build();

            AppUserTeamGroup autg = appUser.buildTeamGroupLink(tg, GroupRole.GUARDIAN);
//            appUserTeamGroupRepository.save(autg);

        }

        return ResponseUserDto.of(saved);
    }

//    @PreAuthorize("hasAuthority('USER_MASTER')")
//    @PostMapping("/follower")
//    public ResponseUserDto addUserFollower(@Valid @RequestBody AddUserFollowerDto dto, Errors errors, Principal principal) {
//        ValidationDto.handleErrors(errors);
//
//        //todo move to service
//        AppUser admin = appUserService.findByUsername(principal.getName()).get();
////        AppUserTeamGroup aptg = admin.getAppUserTeamGroups().stream().filter(utg -> dto.getGroupId().equals(utg.getTeamGroup().getId()))
////                .findAny().orElseThrow(() -> new NotFoundException("group " + dto.getGroupId() + " not found"));
//
//        if (appUserService.existsByUsername(dto.getUsername())) {
//            throw new BadRequestException("user " + dto.getUsername() + " already exists");
//        }
//
//        AppUser appUser = dto.buildUser();
//
//        //todo redo
//        long time = System.currentTimeMillis();
//        appUser.setStatus(Status.ACTIVATED);
//        appUser.setUserRole(UserRole.USER_FOLLOWER);
//        appUser.setOtp("1111111");
//        appUser.setCreatedOnMs(time);
//        appUser.setOtpCreatedOnMs(time);
//
////        appUser.setAppUserTeamGroups(Collections.singleton(aptg));
//
////        userService.save(appUser);
//
//        return new ResponseUserDto(appUserService.save(appUser));
//    }
}
