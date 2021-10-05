package com.agriguardian.service;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.repository.TeamGroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TeamGroupService {
    private final TeamGroupRepository teamGroupRepository;
    private final AppUserService appUserService;
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
    public TeamGroup deleteFromTeamGroup(AppUser deleter, Long tgId, Long appUserToDeleteId){
        AppUser userToDelete = appUserService.findById(appUserToDeleteId).orElseThrow
                (() -> new NotFoundException("user with id: "+appUserToDeleteId + " was not found"));
        TeamGroup editedTeamGroup = findById(tgId).orElseThrow
                (() -> new NotFoundException("group with id "+tgId+" was not found"));

        if(!editedTeamGroup.containsUser(userToDelete))
            throw new ConflictException("group does not contain user: "+ appUserToDeleteId);

        if(!editedTeamGroup.extractAdmins().contains(deleter))
            throw new ConflictException("group does not contain guardian: "+ deleter.getId());

        if(editedTeamGroup.extractAdmins().contains(userToDelete)){
            if(!editedTeamGroup.getOwner().equals(deleter)){
                throw new ConflictException("only owner can remove guardians");
            }
        }
        editedTeamGroup.removeAppUserFromGroup(appUserTeamGroupRepository.getByAppUserId(appUserToDeleteId));
        if(userToDelete.getUserRole().equals(UserRole.USER_FOLLOWER)){
            if(userToDelete.getAppUserTeamGroups().size()==0){
                appUserService.deleteUser(userToDelete.getUsername());
            }
        }

        return save(editedTeamGroup);
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
}
