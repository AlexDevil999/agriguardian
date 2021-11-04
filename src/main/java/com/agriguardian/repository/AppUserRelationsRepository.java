package com.agriguardian.repository;

import com.agriguardian.entity.manyToMany.AppUserRelations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRelationsRepository extends CrudRepository<AppUserRelations, Long> {

}
