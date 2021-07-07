package com.agriguardian.repository;

import com.agriguardian.entity.AppUserTeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTeamGroupRepository extends CrudRepository<AppUserTeamGroup, Long> {
}
