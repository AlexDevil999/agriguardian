package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.manyToMany.AppUserRelations;
import com.agriguardian.entity.manyToMany.RelationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRelationsRepository extends CrudRepository<AppUserRelations, RelationId> {
    List<AppUserRelations> findByController(AppUser master);

    Optional<AppUserRelations> findByControllerAndUserFollower(AppUser controller, AppUser follower);
}
