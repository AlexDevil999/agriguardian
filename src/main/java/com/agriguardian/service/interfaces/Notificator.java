package com.agriguardian.service.interfaces;

import com.agriguardian.dto.MessageDto;
import com.agriguardian.entity.AppUser;

import java.util.List;
import java.util.Set;

public interface Notificator {
    void notifyUsers(Set<AppUser> recipients, MessageDto message);
}
