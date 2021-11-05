package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.manyToMany.AppUserRelations;
import com.agriguardian.entity.manyToMany.RelationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRelationsRepository extends CrudRepository<AppUserRelations, RelationId> {
    List<AppUserRelations> findByController(AppUser master);
}
