package com.agriguardian.dto;

import com.agriguardian.entity.Subscription;
import lombok.Builder;

@Builder
public class SubscriptionDto {
    private long id;

    public static SubscriptionDto of(Subscription subscription) {
        return subscription == null ? null :
                SubscriptionDto.builder()
                        .id(subscription.getId())
                        .build();
    }
}
