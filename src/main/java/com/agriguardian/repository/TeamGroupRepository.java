package com.agriguardian.repository;

import com.agriguardian.entity.TeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamGroupRepository extends CrudRepository<TeamGroup, Long> {

    boolean existsByGuardianInvitationCode(String code);

    boolean existsByVulnerableInvitationCode(String code);

    Optional<TeamGroup> findByGuardianInvitationCode(String code);

    Optional<TeamGroup> findByVulnerableInvitationCode(String code);
}
