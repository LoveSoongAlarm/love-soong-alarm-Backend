package com.lovesoongalarm.lovesoongalarm.external.kakao.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "kakaoClient",
        url = "https://kapi.kakao.com")
public interface KakaoFeignClient {
    @PostMapping("/v1/user/unlink")
    void unlinkKakaoServer(
            @RequestHeader(name = "Authorization") String adminKey,
            @RequestParam(name = "target_id_type") String targetIdType,
            @RequestParam(name = "target_id") Long targetId
    );
}

