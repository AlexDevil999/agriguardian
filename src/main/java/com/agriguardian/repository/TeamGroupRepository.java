package com.agriguardian.repository;

import com.agriguardian.entity.TeamGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamGroupRepository extends CrudRepository<TeamGroup, Long> {
}
