package com.agriguardian.repository;

import com.agriguardian.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    void deleteByAppUserId(Long appUserId);
    UserInfo getUserInfoByAppUserId(Long appUserId);

}
