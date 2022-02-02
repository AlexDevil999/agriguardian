package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserTeamGroupRepository extends CrudRepository<AppUserTeamGroup, Long> {
    Optional<AppUserTeamGroup> findByAppUserIdAndTeamGroup(long id, TeamGroup teamGroup);
    Boolean existsByAppUserIdAndTeamGroup(long id, TeamGroup teamGroup);

}
