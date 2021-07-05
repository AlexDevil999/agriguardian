package com.agriguardian.repository;

import com.agriguardian.entity.UserTeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTeamGroupRepository extends CrudRepository<UserTeamGroup, Long> {
}
