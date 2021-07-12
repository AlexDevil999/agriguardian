package com.agriguardian.service;

import com.agriguardian.entity.TeamGroup;
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

    //todo logs and ...
    public TeamGroup save(TeamGroup tg) {
//        return teamGroupRepository.save(tg);
        return null;
    }

    public Optional<TeamGroup> findById(Long id) {
        return teamGroupRepository.findById(id);
    }
}
