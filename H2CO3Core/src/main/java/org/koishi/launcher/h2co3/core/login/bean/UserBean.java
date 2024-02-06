package org.koishi.launcher.h2co3.core.login.bean;

import android.graphics.drawable.Drawable;

public class UserBean {
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userType;
    private String apiUrl;
    private String authSession;
    private String uuid;
    private String skinTexture;
    private String token;
    private String refreshToken;
    private String clientToken;
    private String userInfo;
    private boolean isOffline;
    private boolean isSelected;
    private Drawable userIcon; // 添加用户头像字段

    public UserBean() {
        // 构造方法
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAuthSession() {
        return authSession;
    }

    public void setAuthSession(String authSession) {
        this.authSession = authSession;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSkinTexture() {
        return skinTexture;
    }

    public void setSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public boolean getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Drawable getUserIcon() {
        return userIcon;
    }

    // 设置和获取用户头像的方法
    public void setUserIcon(Drawable userIcon) {
        this.userIcon = userIcon;
    }
}