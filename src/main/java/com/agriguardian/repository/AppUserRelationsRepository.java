package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.manyToMany.AppUserRelations;
import com.agriguardian.entity.manyToMany.RelationId;
import com.agriguardian.enums.Relation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRelationsRepository extends CrudRepository<AppUserRelations, RelationId> {
    List<AppUserRelations> findByController(AppUser master);

    Optional<AppUserRelations> findByControllerAndUserFollower(AppUser controller, AppUser follower);

    Optional<AppUserRelations> findByControllerAndUserFollowerAndRelation(AppUser controller, AppUser follower, Relation relation);

    List<AppUserRelations> findByControllerAndRelation(AppUser controller , Relation relation);

}
