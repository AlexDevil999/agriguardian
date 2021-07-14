package com.agriguardian.service;

import com.agriguardian.entity.TeamGroup;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.repository.TeamGroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TeamGroupService {
    private final TeamGroupRepository teamGroupRepository;

    public TeamGroup save(TeamGroup tg) {
        try {
            return teamGroupRepository.save(tg);
        } catch (Exception e) {
            log.error("[save] failed to save a teamGroup {}; rsn: {}", tg, e.getMessage());
            throw new InternalErrorException("failed to save teamGroup; rsn: " + e.getMessage());
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
}
