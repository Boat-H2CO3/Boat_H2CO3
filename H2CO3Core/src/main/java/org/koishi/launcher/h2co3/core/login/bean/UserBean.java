package org.koishi.launcher.h2co3.core.login.bean;

public class UserBean {
    private String userName; // 用户名
    private String userEmail; // 用户邮箱
    private String userPassword; // 用户密码
    private String userType; // 用户类型
    private String apiUrl; // API地址
    private String authSession; // 授权会话
    private String uuid; // 用户唯一标识
    private String skinTexture; // 皮肤纹理
    private String token; // 令牌
    private String refreshToken; // 刷新令牌
    private String clientToken; // 客户端令牌
    private String userInfo; // 用户信息
    private boolean isOffline; // 是否离线
    private boolean isSelected; // 是否被选中

    public UserBean() {

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setAuthSession(String authSession) {
        this.authSession = authSession;
    }

    public String getAuthSession() {
        return authSession;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
    }

    public String getSkinTexture() {
        return skinTexture;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public boolean getIsOffline() {
        return isOffline;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}