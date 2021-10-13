package com.agriguardian.repository;

import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserTeamGroupRepository extends CrudRepository<AppUserTeamGroup, Long> {
    Optional<AppUserTeamGroup> findByAppUserId(Long appUserId);
}
