package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
}
