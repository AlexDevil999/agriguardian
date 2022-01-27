package com.agriguardian.service;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserRelations;
import com.agriguardian.enums.Relation;
import com.agriguardian.enums.Restrictions;
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
import java.util.*;
import java.util.stream.Collectors;

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
    private final AppUserRelationsRepository appUserRelationsRepository;
    private final UserInfoRepository userInfoRepository;


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
    public AppUser saveUserFollowerIfNotExist(AppUser appUser, Status status, Set<TeamGroup> teamGroups, AppUser master) {
        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        try {
            AppUser followerToSave = setAppUserDetails(appUser, status, UserRole.USER_FOLLOWER);

            AppUser follower = userRepo.save(followerToSave);

            teamGroupService.saveVulnerableToTeamGroups(follower,teamGroups);

            AppUserRelations masterToFollowerRelation = masterToFollowerRelation(master, follower, Relation.created);

            appUserRelationsRepository.save(masterToFollowerRelation);

            return follower;
        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteFollowerFromUser(Long id, AppUser master) {
        if(!userRepo.existsById(id)){
            throw new NotFoundException("user: " + id + "was not found");
        }
        if(!appUserRelationsRepository.findByControllerAndUserFollowerAndRelation(master,userRepo.findById(id).get(),Relation.created).isPresent()){
            throw new ConflictException(master.getUsername() + "may not delete user " + id);
        }
        try {
            userRepo.deleteById(id);

        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to delete user {}; rsn: {}", id, e.getMessage());
            throw new InternalErrorException("failed to delete devices; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(String username) {
        if (!existsByUsername(username)) {
            throw new BadRequestException("user " + username + " does not exists");
        }

        try {
            List<AppUser> created = appUserRelationsRepository.findByControllerAndRelation(userRepo.findByUsername(username).get(), Relation.created).stream().map(appUserRelations -> appUserRelations.getUserFollower()).collect(Collectors.toList());

            if(created.size()!=0) {
                created.stream().forEach(appUser -> userRepo.delete(appUser));

            }
            userRepo.deleteByUsername(username);
        } catch (Exception e) {
            log.error("[deleteUser] failed to delete a user {}; rsn: {}", username, e.getMessage());
            throw new InternalErrorException("failed to delete user; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser saveUserDeviceIfNotExist(AppUser appUser, Status status, Set<TeamGroup> teamGroups, AppUser creator) {

        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        if (existsByMacAddress(appUser.getMacAddress())) {
            throw new BadRequestException("device with mac address " + appUser.getMacAddress() + " already exists");
        }

        try {
            AppUser deviceToSave = setAppUserDetails(appUser, status, UserRole.USER_FOLLOWER);

            AppUser device = userRepo.save(deviceToSave);
            device.setUsername("device_" + device.getId());
            device = userRepo.save(deviceToSave);

            teamGroupService.saveDeviceToTeamGroups(device,teamGroups);

            AppUserRelations creatorToDeviceRelation = masterToFollowerRelation(creator,device,Relation.created);

            appUserRelationsRepository.save(creatorToDeviceRelation);

            return device;
        } catch (Exception e) {
                log.error("[saveUserFollowerIfNotExist] failed to save a user {}; rsn: {}", appUser, e.getMessage());
                throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
            }
    }

    @Transactional
    public AppUser saveUserBeaconIfNotExist(AppUser appUser, Status status, Set<TeamGroup> teamGroups, AppUser creator) {
        if (existsByUsername(appUser.getUsername())) {
            throw new BadRequestException("user " + appUser.getUsername() + " already exists");
        }

        if (existsByMacAddress(appUser.getMacAddress())) {
            throw new BadRequestException("device with mac address " + appUser.getMacAddress() + " already exists");
        }

        try {
            AppUser deviceToSave = setAppUserDetails(appUser, status, UserRole.USER_FOLLOWER);
            deviceToSave.setRestrictions(Restrictions.cannotSendGpsData);

            AppUser device = userRepo.save(deviceToSave);
            device.setUsername("beacon_" + device.getId());
            device = userRepo.save(deviceToSave);

            teamGroupService.saveDeviceToTeamGroups(device,teamGroups);

            AppUserRelations creatorToDeviceRelation = masterToFollowerRelation(creator,device,Relation.created);

            appUserRelationsRepository.save(creatorToDeviceRelation);

            return device;
        } catch (Exception e) {
            log.error("[saveUserFollowerIfNotExist] failed to save a user {}; rsn: {}", appUser, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
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

    public AppUser setNewPasswordForUser(AppUser user, String oldPassword, String newPassword){
        if(!passwordEncryptor.matches(oldPassword,user.getPassword())){
            throw new ConflictException("password is not correct");
        }
        if(oldPassword.equals(newPassword)){
            throw new ConflictException("new password should differ from old");
        }

        user.setPassword(passwordEncryptor.encode(newPassword));

        try{
            return userRepo.save(user);
        }
        catch (Exception e){
            log.error("[setNewPasswordForUser] failed to set new password; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to save user. rsn: " + e.getMessage());
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
        log.debug("[sendEmailConfirmationForUser] trying to send an email for user: {}", appUser.getUsername());
        emailSenderService.send(appUser.getUsername(),EmailSender.buildEmail(appUser.getUsername(), appUser.getOtp()));
    }

    public List<AppUserRelations> getAllUserRelations(AppUser master){
        return appUserRelationsRepository.findByController(master);
    }

    public Map<AppUser, String> getAllRelatedWithRelationType(AppUser master){
        List<AppUserRelations> userRelations = getAllUserRelations(master);
        Map<AppUser, String> subAccountIdAndRelation =
                userRelations.stream().collect(Collectors.
                        toMap(appUserRelations -> appUserRelations.getUserFollower(),
                                appUserRelations -> appUserRelations.getRelation().name()));

        return subAccountIdAndRelation;
    }

    @Transactional
    public AppUser editUser(AppUser editedVariantOfUser, AppUser thisUser) {

        if(existsByUsername(editedVariantOfUser.getUsername())&&!thisUser.getUsername().equalsIgnoreCase(editedVariantOfUser.getUsername()))
            throw new ConflictException(editedVariantOfUser.getUsername() + " already exists");

        thisUser.editUser(editedVariantOfUser);
        Optional.ofNullable(editedVariantOfUser.getPassword()).ifPresent(password -> setPasswordForUser(thisUser,editedVariantOfUser.getPassword()));
        try{
            return userRepo.save(thisUser);
        } catch (Exception e){
            log.error("[editUser] failed to edit a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to edit a user; rsn: " + e.getMessage());
        }
    }

    public boolean masterCanEditVulnerable(AppUser master , AppUser follower){
        Optional<AppUserRelations> relation = appUserRelationsRepository.findByControllerAndUserFollowerAndRelation(master,follower,Relation.created);

        return relation.isPresent();
    }

    public AppUser setPasswordForUser(AppUser appUser, String password){
        appUser.setPassword(passwordEncryptor.encode(password));
        return appUser;
    }

    private boolean existsByMacAddress(String macAddress) {
        try {
            return userRepo.existsByMacAddress(macAddress);
        } catch (Exception e) {
            log.error("[existsByMacAddress] failed to retrieve a user; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a user; rsn: " + e.getMessage());
        }
    }

    private boolean usersOtpCodeIsValid(AppUser appUser){
        return Long.sum(appUser.getOtpCreatedOnMs(),lifetimeMs)>System.currentTimeMillis();
    }

    private AppUser setAppUserDetails(AppUser appUser, Status status, UserRole userRole) {
        long time = System.currentTimeMillis();
        appUser.setStatus(status);
        appUser.setUserRole(userRole);
        appUser.setOtp(RandomCodeGenerator.generateConfirmationCode());
        appUser.setCreatedOnMs(time);
        appUser.setOtpCreatedOnMs(time);
        appUser.setPassword(passwordEncryptor.encode(appUser.getPassword()));
        return appUser;
    }

    private AppUserRelations masterToFollowerRelation(AppUser master, AppUser follower, Relation relation) {
        AppUserRelations masterToFollowerRelation = new AppUserRelations();
        masterToFollowerRelation.setRelation(relation);
        masterToFollowerRelation.setController(master);
        masterToFollowerRelation.setUserFollower(follower);

        return masterToFollowerRelation;
    }


}
