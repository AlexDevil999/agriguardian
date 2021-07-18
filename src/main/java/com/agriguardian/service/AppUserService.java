package com.agriguardian.service;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AppUserRepository;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.repository.TeamGroupRepository;
import com.agriguardian.service.security.PasswordEncryptor;
import com.agriguardian.util.RandomCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository userRepo;
    private final AppUserTeamGroupRepository autgRepository;
    private final TeamGroupRepository teamGroupRepository;
    private final PasswordEncryptor passwordEncryptor;

    public AppUser save(AppUser appUser) {
        try {
            return userRepo.save(appUser);
        } catch (Exception e) {
            log.error("[save] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser saveUserMasterIfNotExist(AppUser appUser, Status status, Boolean withNewGroup) {
        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        try {
            long time = System.currentTimeMillis();
            appUser.setStatus(status);
            appUser.setUserRole(UserRole.USER_MASTER);
            appUser.setOtp(RandomCodeGenerator.generateConfirmationCode());
            appUser.setCreatedOnMs(time);
            appUser.setOtpCreatedOnMs(time);
            appUser.setPassword(passwordEncryptor.encode(appUser.getPassword()));

            AppUser user = userRepo.save(appUser);
            //todo move into the saparate service
            if (Boolean.TRUE.equals(withNewGroup)) {
                TeamGroup tg = teamGroupRepository.save(createTeamGroup(user));
                AppUserTeamGroup autg = user.addTeamGroup(tg, GroupRole.GUARDIAN);
                autgRepository.save(autg);
            }

            return user;
        } catch (Exception e) {
            log.error("[save] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser saveUserFollowerIfNotExist(AppUser appUser, Status status, Set<TeamGroup> teamGroups) {
        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        try {
            long time = System.currentTimeMillis();
            appUser.setStatus(status);
            appUser.setUserRole(UserRole.USER_FOLLOWER);
            appUser.setOtp(RandomCodeGenerator.generateConfirmationCode());
            appUser.setCreatedOnMs(time);
            appUser.setOtpCreatedOnMs(time);
            appUser.setPassword(passwordEncryptor.encode(appUser.getPassword()));

            AppUser user = userRepo.save(appUser);
            //todo move into the saparate service
            teamGroups.forEach(teamGroup -> {
//                TeamGroup tg = teamGroupRepository.findById(teamGroup).orElseThrow(() -> new NotFoundException("TeamGroup not found: " + teamGroup));
                AppUserTeamGroup autg = user.addTeamGroup(teamGroup, GroupRole.VULNERABLE);
                autgRepository.save(autg);
            });

            return user;
        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
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

    public Optional<AppUser> findById(Long id) {
        try {
            return userRepo.findById(id);
        } catch (IllegalArgumentException e) {
            log.error("[findById] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new BadRequestException("failed to retrieve a user; rsn: " + e.getMessage());
        } catch (Exception e) {
            log.error("[findById] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user. rsn: " + e.getMessage());
        }
    }

    public boolean exists(Long id) {
        try {
            return userRepo.existsById(id);
        } catch (IllegalArgumentException e) {
            log.error("[exists] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new BadRequestException("failed to retrieve a user; rsn: {}" + e.getMessage());
        } catch (Exception e) {
            log.error("[exists] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    public boolean existsByUsername(String username) {
        try {
            return userRepo.existsByUsername(username);
        } catch (Exception e) {
            log.error("[existsByUsername] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    public Optional<AppUser> findByUsername(String username) {
        try {
            return userRepo.findByUsername(username);
        } catch (Exception e) {
            log.error("[findByUsername] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    public AppUser findByUsernameOrThrowNotFound(String username) {
        try {
            return userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("user not found; resource: " + username));
        } catch (Exception e) {
            log.error("[findByUsername] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }
}
