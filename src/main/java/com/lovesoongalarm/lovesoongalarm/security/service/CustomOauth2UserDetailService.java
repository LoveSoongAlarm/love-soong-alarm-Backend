package com.lovesoongalarm.lovesoongalarm.security.service;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EPlatform;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserSecurityForm;
import com.lovesoongalarm.lovesoongalarm.security.info.UserPrincipal;
import com.lovesoongalarm.lovesoongalarm.security.info.factory.Oauth2UserInfo;
import com.lovesoongalarm.lovesoongalarm.security.info.factory.Oauth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserDetailService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        EPlatform platform = EPlatform.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );
        log.info("oauth 제공자 정보 가져오기 성공, 제공자 = {}", platform);

        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory
                .getOauth2UserInfo(platform, super.loadUser(userRequest).getAttributes());
        log.info("oauth 사용자 정보 가져오기 성공");
        log.info("attributes = {}", oauth2UserInfo.getAttributes().toString());

        UserSecurityForm securityForm = userRepository
                .findUserSecurityFromBySerialId(oauth2UserInfo.getId())
                .orElseGet(() -> {
                    log.info("새로운 사용자 접근, 저장 로직 진입");

                    // 새 User 엔티티 생성 및 저장
                    User newUser = userRepository.save(
                            User.create(oauth2UserInfo.getId(), platform, ERole.USER, EUserStatus.INACTIVE));
                    return UserSecurityForm.invoke(newUser);
                });
        log.info("oauth2 사용자 조회 성공");

        return UserPrincipal.create(securityForm, oauth2UserInfo.getAttributes());
    }
}

