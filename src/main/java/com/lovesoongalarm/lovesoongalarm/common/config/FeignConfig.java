package com.lovesoongalarm.lovesoongalarm.common.config;

import com.lovesoongalarm.lovesoongalarm.LovesoongalarmApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;


@Configuration
@EnableFeignClients(basePackageClasses = LovesoongalarmApplication.class)
public class FeignConfig {
}
