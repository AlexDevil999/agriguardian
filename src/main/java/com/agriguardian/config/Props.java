package com.agriguardian.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class Props {
    @Value("${device.password}")
    private String devicePass;
}
