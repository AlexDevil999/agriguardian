package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Iterable<AppUser> findAllByUsername(String username);
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}