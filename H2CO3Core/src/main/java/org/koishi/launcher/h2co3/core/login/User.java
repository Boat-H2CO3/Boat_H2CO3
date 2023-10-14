package org.koishi.launcher.h2co3.core.login;

public class User {
    private String username;
    private String password;
    private String api;
    private String apiUrl;
    private String accessToken;
    private String clientToken;
    private long expiresAt;
    private String msaRefreshToken;
    private String profileId;
    private String xuid;

    public User() {
    }
    public User(String username, String password, String api, String apiUrl, String accessToken, String clientToken, long expiresAt, String msaRefreshToken, String profileId, String xuid) {
        this.username = username;
        this.password = password;
        this.api = api;
        this.apiUrl = apiUrl;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.expiresAt = expiresAt;
        this.msaRefreshToken = msaRefreshToken;
        this.profileId = profileId;
        this.xuid = xuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMsaRefreshToken() {
        return msaRefreshToken;
    }

    public void setMsaRefreshToken(String msaRefreshToken) {
        this.msaRefreshToken = msaRefreshToken;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getXuid() {
        return xuid;
    }

    public void setXuid(String xuid) {
        this.xuid = xuid;
    }
}