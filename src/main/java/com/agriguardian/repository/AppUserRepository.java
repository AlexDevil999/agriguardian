package com.agriguardian.repository;

import com.agriguardian.entity.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByMacAddress(String macAddress);

    AppUser findByMacAddress(String macAddress);

    void deleteByUsername(String username);

    void deleteByMacAddress(String macAddress);

    Optional<AppUser> findByRefreshToken(String refreshToken);

}
