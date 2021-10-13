package com.agriguardian.service;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.*;
import com.agriguardian.service.interfaces.EmailSender;
import com.agriguardian.service.security.PasswordEncryptor;
import com.agriguardian.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {
    @Value("${code.lifetime}")
    private long lifetimeMs;
    private final AppUserRepository userRepo;
    private final PasswordEncryptor passwordEncryptor;
    private final EmailSenderService emailSenderService;
    private final TeamGroupService teamGroupService;


    public AppUser save(AppUser appUser) {
        try {
            return userRepo.save(appUser);
        } catch (Exception e) {
            log.error("[save] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser activateUser(AppUser appUser, String otp) {
        try {
            if(!usersOtpCodeIsValid(appUser)) {
                throw new ConflictException("lifetime of this code has ended");
            }

            if(!otp.equals(appUser.getOtp()))
                throw new ConflictException("incorrect confirmation code");

            appUser.setStatus(Status.ACTIVATED);
            return userRepo.save(appUser);
        } catch (Exception e) {
            log.error("[activate] failed to activate a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to activate user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser saveUserMasterIfNotExist(AppUser appUser, Status status, Boolean withNewGroup) {
        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        try {
            AppUser masterToSave = setAppUserDetails(appUser, status, UserRole.USER_MASTER);

            AppUser master = userRepo.save(masterToSave);

            if (Boolean.TRUE.equals(withNewGroup)) {
                teamGroupService.createTeamGroupForUser(master);
            }

            sendEmailConfirmationForUser(appUser);

            return master;
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
            AppUser followerToSave = setAppUserDetails(appUser, status, UserRole.USER_FOLLOWER);

            AppUser follower = userRepo.save(followerToSave);

            teamGroupService.saveVulnerableToTeamGroups(follower,teamGroups);

            return follower;
        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }



    @Transactional
    public void deleteDevice(String username , Set<String> macAddresses) {
        if (!existsByUsername(username)) {
            throw new BadRequestException("user " + username + " does not exists");
        }
        for (String macAddress: macAddresses) {
            if(!existsByMacAddress(macAddress))
                throw new NotFoundException("device with mac Address: "+macAddress+ " was not found");
        }

        try {
            //todo change when implement device
            userRepo.deleteByUsername(username);

            macAddresses.forEach(userRepo::deleteByMacAddress);
        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to delete a devices {}; rsn: {}", macAddresses, e.getMessage());
            throw new InternalErrorException("failed to delete user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(String username) {
        if (!existsByUsername(username)) {
            throw new BadRequestException("user " + username + " does not exists");
        }

        try {
            userRepo.deleteByUsername(username);
        } catch (Exception e) {
            log.error("[deleteUser] failed to delete a user {}; rsn: {}", username, e.getMessage());
            throw new InternalErrorException("failed to delete user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser saveUserDeviceIfNotExist(AppUser appUser, Status status, Set<TeamGroup> teamGroups) {
            if (existsByUsername(appUser.getUsername())) {
                throw new BadRequestException("user " + appUser.getUsername() + " already exists");
            }

        if (existsByMacAddress(appUser.getMacAddress())) {
            throw new BadRequestException("device with mac address " + appUser.getMacAddress() + " already exists");
        }

            try {
                AppUser deviceToSave = setAppUserDetails(appUser, status, UserRole.USER_FOLLOWER);

                AppUser device = userRepo.save(deviceToSave);

                teamGroupService.saveDeviceToTeamGroups(device,teamGroups);

                return device;
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

    public AppUser findByRefreshTokenOrThrowNotFound(String refreshToken) {
        try {
            return userRepo.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new NotFoundException("user not found; resource: " + refreshToken));
        } catch (Exception e) {
            log.error("[findByUsername] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    public void sendEmailConfirmationForUser(AppUser appUser){
        if(!appUser.getStatus().equals(Status.REGISTRATION))
            throw new ConflictException("user has already confirmed registration");
        if(!usersOtpCodeIsValid(appUser)) {
            try{
                appUser.setOtp(RandomCodeGenerator.generateConfirmationCode());
                appUser.setOtpCreatedOnMs(System.currentTimeMillis());
                userRepo.save(appUser);
            }
            catch (Exception e){
                log.error("[sendEmailConfirmationForUser] failed to send verification email; rsn: {}", e.getMessage());
                throw new InternalErrorException("failed to set new Otp a user; rsn: " + e.getMessage());
            }
        }
        emailSenderService.send(appUser.getUsername(),EmailSender.buildEmail(appUser.getUsername(), appUser.getOtp()));
    }

    private boolean existsByMacAddress(String macAddress) {
        try {
            return userRepo.existsByUsername(macAddress);
        } catch (Exception e) {
            log.error("[existsByMacAddress] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    private boolean usersOtpCodeIsValid(AppUser appUser){
        return Long.sum(appUser.getOtpCreatedOnMs(),lifetimeMs)>System.currentTimeMillis();
    }

    private AppUser setAppUserDetails(AppUser appUser, Status status, UserRole userFollower) {
        long time = System.currentTimeMillis();
        appUser.setStatus(status);
        appUser.setUserRole(userFollower);
        appUser.setOtp(RandomCodeGenerator.generateConfirmationCode());
        appUser.setCreatedOnMs(time);
        appUser.setOtpCreatedOnMs(time);
        appUser.setPassword(passwordEncryptor.encode(appUser.getPassword()));
        return appUser;
    }


}
