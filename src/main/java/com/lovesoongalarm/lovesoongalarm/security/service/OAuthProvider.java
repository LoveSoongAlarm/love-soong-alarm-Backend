package com.lovesoongalarm.lovesoongalarm.security.service;

public interface OAuthProvider {
    void requestRevoke(String serialId);
}
