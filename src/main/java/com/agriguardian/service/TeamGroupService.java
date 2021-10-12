package com.agriguardian.service;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AppUserRepository;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.repository.TeamGroupRepository;
import com.agriguardian.util.RandomCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TeamGroupService {
    private final TeamGroupRepository teamGroupRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserTeamGroupRepository appUserTeamGroupRepository;

    public TeamGroup save(TeamGroup tg) {
        try {
            return teamGroupRepository.save(tg);
        } catch (Exception e) {
            log.error("[save] failed to save a teamGroup {}; rsn: {}", tg, e.getMessage());
            throw new InternalErrorException("failed to save teamGroup; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public TeamGroup deleteAppUserFromTeamGroup(AppUser deleter, Long tgId, Long appUserToDeleteId){
        AppUserTeamGroup appUserTeamGroupToDelete =appUserTeamGroupRepository.getByAppUserId(appUserToDeleteId).orElseThrow
                (() -> new NotFoundException("user with id: "+appUserToDeleteId + " was not found in a group"));
        AppUser appUserToDelete = appUserTeamGroupToDelete.getAppUser();
        TeamGroup editedTeamGroup = findById(tgId).orElseThrow
                (() -> new NotFoundException("group with id "+tgId+" was not found"));

        if(appUserToDelete.equals(deleter))
            throw new ConflictException("prohibited to delete yourself");

        if(!editedTeamGroup.containsUser(appUserToDelete))
            throw new ConflictException("group does not contain user: "+ appUserToDeleteId);

        if(!editedTeamGroup.extractAdmins().contains(deleter))
            throw new ConflictException("group does not contain guardian: "+ deleter.getId());

        if(editedTeamGroup.extractAdmins().contains(appUserToDelete)){
            if(!editedTeamGroup.getOwner().equals(deleter)){
                throw new ConflictException("only owner can remove guardians");
            }
        }

        try {
            editedTeamGroup.removeAppUserTeamGroupFromGroup(appUserTeamGroupToDelete);

            if (appUserToDelete.getUserRole().equals(UserRole.USER_FOLLOWER)) {
                if (appUserToDelete.getAppUserTeamGroups().stream()
                        .filter(appUserTeamGroup -> !appUserTeamGroup.getTeamGroup().equals(editedTeamGroup))
                        .collect(Collectors.toSet()).size() == 0) {
                    appUserRepository.deleteByUsername(appUserToDelete.getUsername());
                }
            }

            return save(editedTeamGroup);
        }
        catch (Exception e){
            log.error("[deleteFromTeamGroup] failed to delete a user {} from tg {}; rsn: {}", deleter.getUsername(),tgId, e.getMessage());
            throw new InternalErrorException("failed to delete user from tg; rsn: " + e.getMessage());
        }
    }

    public Optional<TeamGroup> findById(Long id) {
        return teamGroupRepository.findById(id);
    }

    public boolean existsByGuardianCode(String gc) {
        return teamGroupRepository.existsByGuardianInvitationCode(gc);
    }

    public boolean existsByVulnerableCode(String gc) {
        return teamGroupRepository.existsByVulnerableInvitationCode(gc);
    }

    public Optional<TeamGroup> findByGuardianCode(String gc) {
        return teamGroupRepository.findByGuardianInvitationCode(gc);
    }

    public Optional<TeamGroup> findByVulnerableCode(String gc) {
        return teamGroupRepository.findByVulnerableInvitationCode(gc);
    }

    public Optional<TeamGroup> findByInvitationCode(String invitationCode) {
        Optional<TeamGroup> tg = teamGroupRepository.findByGuardianInvitationCode(invitationCode);
        if (!tg.isPresent()) {
            tg = teamGroupRepository.findByVulnerableInvitationCode(invitationCode);
        }
        return tg;
    }

    //todo check if it fits here
    public void createTeamGroupForUser(AppUser user){
        TeamGroup tg = teamGroupRepository.save(createTeamGroup(user));
        AppUserTeamGroup autg = user.addTeamGroup(tg, GroupRole.GUARDIAN);
        appUserTeamGroupRepository.save(autg);
    }

    //todo check if it fits here
    @Transactional
    public void saveVulnerableToTeamGroups(AppUser follower, Set<TeamGroup> teamGroups){
        teamGroups.forEach(teamGroup -> {
            teamGroupRepository.findById(teamGroup.getId()).orElseThrow(() -> new NotFoundException("TeamGroup not found: " + teamGroup));
            AppUserTeamGroup autg = follower.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
            appUserTeamGroupRepository.save(autg);
        });
    }

    //todo check if it fits here
    @Transactional
    public void saveDeviceToTeamGroups(AppUser device, Set<TeamGroup> teamGroups){
        teamGroups.forEach(teamGroup -> {
            teamGroupRepository.findById(teamGroup.getId()).orElseThrow(() -> new NotFoundException("TeamGroup not found: " + teamGroup));
            AppUserTeamGroup autg = device.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
            appUserTeamGroupRepository.save(autg);
        });
    }

    public TeamGroup createTeamGroup(AppUser u) {
        if (u.getTeamGroup() != null) {
            throw new BadRequestException("user " + u.getUsername() + "already have group (id " + u.getTeamGroup().getId() + ")");
        }

        //todo add verification for uniqueness
        String guardianInvitationCode = RandomCodeGenerator.generateInvitationCode();
        String vulnerableInvitationCode;
        do {
            vulnerableInvitationCode = RandomCodeGenerator.generateInvitationCode();
        } while (vulnerableInvitationCode.equals(guardianInvitationCode));


        TeamGroup tg = TeamGroup.builder()
                .guardianInvitationCode(guardianInvitationCode)
                .vulnerableInvitationCode(vulnerableInvitationCode)
                .name(u.getUserInfo().getName() + "'s group")
                .owner(u)
                .appUserTeamGroups(new HashSet())
                .build();

        u.setTeamGroup(tg);
        return tg;
    }
}
