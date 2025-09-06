package com.lovesoongalarm.lovesoongalarm.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("api/v1/health-check")
    public BaseResponse<Void> healthCheck(){
        return BaseResponse.success(null);
    }
}
