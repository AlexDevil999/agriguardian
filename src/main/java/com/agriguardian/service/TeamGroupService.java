package com.agriguardian.service;

import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.*;
import com.agriguardian.util.RandomCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TeamGroupService {
    private final TeamGroupRepository teamGroupRepository;
    private final AppUserTeamGroupRepository appUserTeamGroupRepository;
    private final AppUserRelationsRepository appUserRelationsRepository;
    private final AlertBluetoothZoneRepository alertBluetoothZoneRepository;
    private final AlertGeoZoneRepository alertGeoZoneRepository;

    public TeamGroup save(TeamGroup tg) {
        try {
            return teamGroupRepository.save(tg);
        } catch (Exception e) {
            log.error("[save] failed to save a teamGroup {}; rsn: {}", tg, e.getMessage());
            throw new InternalErrorException("failed to save teamGroup; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public TeamGroup deleteAppUserFromTeamGroupByTeamGroupAdmin(AppUser deleter, Long tgId, Long appUserToDeleteId){

        TeamGroup editedTeamGroup = findById(tgId).orElseThrow
                (() -> new NotFoundException("group with id "+tgId+" was not found"));

        AppUserTeamGroup appUserTeamGroupToDelete = appUserTeamGroupRepository.findByAppUserIdAndTeamGroup(appUserToDeleteId,editedTeamGroup).orElseThrow
                (() -> new NotFoundException("user with id: "+appUserToDeleteId + " was not found in a group"));

        AppUser appUserToDelete = appUserTeamGroupToDelete.getAppUser();

        if(appUserToDelete.equals(deleter))
            throw new ConflictException("Prohibited to delete yourself");

        if(!editedTeamGroup.extractAdmins().contains(deleter))
            throw new ConflictException("group does not contain guardian: "+ deleter.getId());

        if(editedTeamGroup.extractAdmins().contains(appUserToDelete)){
            if(!editedTeamGroup.getOwner().equals(deleter)){
                throw new ConflictException("only owner can remove guardians");
            }
        }


        if(!editedTeamGroup.getOwner().equals(deleter)) {
            if (appUserToDelete.getUserRole().equals(UserRole.USER_FOLLOWER)) {
                if (!appUserRelationsRepository.findByControllerAndUserFollower(deleter, appUserToDelete).isPresent()){
                    throw new ConflictException("master can delete only his followers");
                }
            }
        }

        try {
            editedTeamGroup.removeAppUserTeamGroupFromGroup(appUserTeamGroupToDelete);

            removeAppUserFromTeamGroupZones(appUserToDelete, editedTeamGroup);

            return save(editedTeamGroup);
        }
        catch (Exception e){
            log.error("[deleteFromTeamGroup] failed to delete a user {} from tg {}; rsn: {}", appUserToDelete,tgId, e.getMessage());
            throw new InternalErrorException("failed to delete user from tg; rsn: " + e.getMessage());
        }
    }

    public Optional<TeamGroup> findById(Long id) {
        try {
            return teamGroupRepository.findById(id);
        }
        catch (Exception e){
            log.error("[findById] failed to find teamGroup by id{}; rsn: {}", id, e.getMessage());
            throw new InternalErrorException("failed to find teamGroup by id; rsn: " + e.getMessage());
        }
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
        try {
            Optional<TeamGroup> tg = teamGroupRepository.findByGuardianInvitationCode(invitationCode);
            if (!tg.isPresent()) {
                tg = teamGroupRepository.findByVulnerableInvitationCode(invitationCode);
            }
            return tg;
        }
        catch (Exception e){
            log.error("[findByInvitationCode] failed to find teamGroup by InvitationCode {}; rsn: {}", invitationCode, e.getMessage());
            throw new InternalErrorException("failed to find teamGroup by InvitationCode; rsn: " + e.getMessage());
        }
    }

    //todo check if it fits here
    @Transactional
    public void createTeamGroupForUser(AppUser user){
        if(teamGroupRepository.existsByOwner(user)){
            throw new ConflictException("user already has a teamGroup");
        }
        try {
            TeamGroup tg = teamGroupRepository.save(createTeamGroup(user));
            AppUserTeamGroup autg = user.addTeamGroup(tg, GroupRole.GUARDIAN);
            appUserTeamGroupRepository.save(autg);
        }
        catch (Exception e){
            log.error("[createTeamGroupForUser] failed to create Team Group For User {}; rsn: {}", user.getUsername(), e.getMessage());
            throw new InternalErrorException(" failed to create Team Group For User; rsn: " + e.getMessage());
        }
    }

    //todo check if it fits here
    @Transactional
    public void saveVulnerableToTeamGroups(AppUser follower, Set<TeamGroup> teamGroups){
            teamGroups.forEach(teamGroup -> {
                teamGroupRepository.findById(teamGroup.getId()).orElseThrow(() -> new NotFoundException("TeamGroup not found: " + teamGroup));
                    AppUserTeamGroup autg = follower.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
                    try{
                        appUserTeamGroupRepository.save(autg);
                    }
                catch (Exception e){
                    log.error("[saveVulnerableToTeamGroups] failed to save Vulnerable {} To Team Groups {}; rsn: {}",follower.getUsername(), Arrays.toString(teamGroups.toArray()), e.getMessage());
                    throw new InternalErrorException(" failed to save Vulnerable To Team Groups: " + e.getMessage());
                }
            });

    }

    //todo check if it fits here
    @Transactional
    public void saveDeviceToTeamGroups(AppUser device, Set<TeamGroup> teamGroups){
        teamGroups.forEach(teamGroup -> {
            teamGroupRepository.findById(teamGroup.getId()).orElseThrow(() -> new NotFoundException("TeamGroup not found: " + teamGroup));
            AppUserTeamGroup autg = device.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
            try {
                appUserTeamGroupRepository.save(autg);
            }
            catch (Exception e){
                log.error("[saveDeviceToTeamGroups] failed to save device {} To Team Groups {}; rsn: {}",device.getTeamGroup(), Arrays.toString(teamGroups.toArray()), e.getMessage());
                throw new InternalErrorException(" failed to save device To Team Groups: " + e.getMessage());
            }
        });
    }

    private TeamGroup createTeamGroup(AppUser u) {
        if (u.getTeamGroup() != null) {
            throw new BadRequestException("user " + u.getUsername() + "already have group (id " + u.getTeamGroup().getId() + ")");
        }

        String guardianInvitationCode = generateUniqueInvitationCode();
        String vulnerableInvitationCode = generateUniqueInvitationCode();

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

    @Transactional
    public void addUserToTeamGroup(AppUser user,TeamGroup teamGroup, GroupRole groupRole) {
        if(appUserTeamGroupRepository.findByAppUserIdAndTeamGroup(user.getId(), teamGroup).isPresent()){
            throw new ConflictException("user " + user.getUsername() + "is already in this teamGroup");
        }
        AppUserTeamGroup autg = user.addTeamGroup(teamGroup, groupRole);
        appUserTeamGroupRepository.save(autg);
    }

    @Transactional
    public TeamGroup removeControlledFollowerFromTeamGroup(AppUser deleter, AppUser follower, TeamGroup teamGroup){
        if(!appUserRelationsRepository.findByControllerAndUserFollower(deleter,follower).isPresent()){
            throw new ConflictException("user " + deleter.getUsername() + " is not allowed to remove user "+ follower.getUsername());
        }
        AppUserTeamGroup appUserTeamGroupToDelete = appUserTeamGroupRepository.findByAppUserIdAndTeamGroup(follower.getId(),teamGroup)
                .orElseThrow(() -> new NotFoundException("can not find user with id: " + follower.getId() + "in team group: " + teamGroup.getId()));
        try {
            teamGroup.removeAppUserTeamGroupFromGroup(appUserTeamGroupToDelete);

            removeAppUserFromTeamGroupZones(follower, teamGroup);

            return save(teamGroup);
        }
        catch (Exception e){
            log.error("[deleteFromTeamGroup] failed to delete a user {} from tg {}; rsn: {}", follower.getUsername() ,teamGroup.getId(), e.getMessage());
            throw new InternalErrorException("failed to delete user from tg; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public TeamGroup addControlledFollowerToTeamGroup(AppUser deleter, AppUser follower, TeamGroup teamGroup){
        if(!appUserRelationsRepository.findByControllerAndUserFollower(deleter,follower).isPresent()){
            throw new ConflictException("user " + deleter.getUsername() + " is not allowed to manipulate user "+ follower.getUsername());
        }

        try {
            AppUserTeamGroup autg = follower.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
            appUserTeamGroupRepository.save(autg);
            return teamGroupRepository.findById(teamGroup.getId()).get();
        }
        catch (Exception e){
            log.error("[deleteFromTeamGroup] failed to delete a user {} from tg {}; rsn: {}", follower.getUsername() ,teamGroup.getId(), e.getMessage());
            throw new InternalErrorException("failed to delete user from tg; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public TeamGroup setNewCodes(TeamGroup teamGroup, boolean resetVulnerable, boolean resetGuardian){
        try {
            if (resetVulnerable) {
                teamGroup.setVulnerableInvitationCode(generateUniqueInvitationCode());
            }

            if (resetGuardian) {
                teamGroup.setGuardianInvitationCode(generateUniqueInvitationCode());
            }

            return teamGroupRepository.save(teamGroup);

        } catch (Exception e){
            log.error("[setNewCodes] failed refresh codes for tg {}; rsn: {}", teamGroup.getId(), e.getMessage());
            throw new InternalErrorException("failed refresh codes for tg rsn:" + e.getMessage());
        }
    }

    private String generateUniqueInvitationCode(){
        String code;

        do {
            code = RandomCodeGenerator.generateInvitationCode();
        }   while (existsByGuardianCode(code)||existsByVulnerableCode(code));

        return code;
    }

    private void removeAppUserFromTeamGroupZones(AppUser toRemove, TeamGroup teamgroup){
        if(teamgroup.getAlertBluetoothZones()!=null) {
            for (AlertBluetoothZone BLEZone : teamgroup.getAlertBluetoothZones()) {
                BLEZone.removeVulnerable(toRemove);
                alertBluetoothZoneRepository.save(BLEZone);
            }
        }

        if(teamgroup.getAlertGeoZones()!=null) {
            for (AlertGeoZone GEOZone : teamgroup.getAlertGeoZones()) {
                GEOZone.removeVulnerable(toRemove);
                alertGeoZoneRepository.save(GEOZone);
            }
        }
    }


}
