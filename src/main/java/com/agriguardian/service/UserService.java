package com.agriguardian.service;

import com.agriguardian.entity.User;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    public User save(User user) {
        try {
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("[save] failed to save a user {}; rsn: {}", user, e.getMessage());
            throw new InternalErrorException("failed to save user; rsn: " + e.getMessage());
        }
    }

    public Optional<User> findById(Long id) {
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
}