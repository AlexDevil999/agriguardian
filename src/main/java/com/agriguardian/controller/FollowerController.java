package com.agriguardian.controller;

import com.agriguardian.dto.DeleteDevicesDto;
import com.agriguardian.dto.ResponseTeamGroupDto;
import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/follower")
public class FollowerController {
    private final TeamGroupService teamGroupService;
    private final AppUserService appUserService;

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/delete/{groupId}/{childId}")
    public ResponseTeamGroupDto deleteFollowerFromGroup(@PathVariable(name = "groupId") long groupId, @PathVariable(name = "childId") long childId, Principal principal) {
        TeamGroup thisGroup = teamGroupService.findById(groupId).orElseThrow(() -> new NotFoundException("group with id: " + groupId + " does not exists"));
        AppUser followerToDeleteFromGroup = appUserService.findById(childId).orElseThrow(() -> new NotFoundException("user with id: " + childId + "does not exists"));
        AppUser deleter = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        thisGroup = teamGroupService.removeControlledFollowerFromTeamGroup(deleter,followerToDeleteFromGroup,thisGroup);
        return ResponseTeamGroupDto.of(thisGroup);
    }

}
