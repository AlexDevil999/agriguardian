package com.agriguardian.repository;

import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserTeamGroupRepository extends CrudRepository<AppUserTeamGroup, Long> {
    AppUserTeamGroup getByAppUserId(Long appUserId);
}
