package org.koishi.launcher.h2co3.core;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class H2CO3Auth {

    public static String getPlayerName() {
        return H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, null);
    }

    public static void setPlayerName(String properties) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, properties);
    }

    public static String getAuthSession() {
        return H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_AUTH_SESSION, "0");
    }

    public static void setAuthSession(String session) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_SESSION, session);
    }

    public static String getUserProperties() {
        return H2CO3Tools.getH2CO3ValueString("user_properties", "{}");
    }

    public static void setUserProperties(String properties) {
        H2CO3Tools.setBoatValue("user_properties", properties);
    }

    public static String getUserType() {
        return H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USER_TYPE, "mojang");
    }

    public static void setUserType(String type) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_USER_TYPE, type);
    }

    public static String getAuthUUID() {
        return H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_UUID, "0000-0000-0000-0000");
    }

    public static void setAuthUUID(String uuid) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_UUID, uuid);
    }

    public static String getAuthAccessToken() {
        return H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_TOKEN, "0");
    }

    public static void setAuthAccessToken(String token) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_TOKEN, token);
    }

    private static final List<UserBean> userList = new ArrayList<>();

    static File usersFile = new File(H2CO3Tools.H2CO3_SETTING_DIR, "h2co3_users.json");

    public static void addUserToJson(String name, String email, String password, String userType, String apiUrl, String authSession, String uuid, String skinTexture, String token, String refreshToken, String clientToken, Boolean isOffline, boolean isSelected) {
        try {
            String content = readFileContent(usersFile);
            JSONObject json = new JSONObject(content.isEmpty() ? "{}" : content);

            JSONObject userData = new JSONObject();
            userData.put(H2CO3Tools.LOGIN_USER_EMAIL, email);
            userData.put(H2CO3Tools.LOGIN_USER_PASSWORD, password);
            userData.put(H2CO3Tools.LOGIN_USER_TYPE, userType);
            userData.put(H2CO3Tools.LOGIN_API_URL, apiUrl);
            userData.put(H2CO3Tools.LOGIN_AUTH_SESSION, authSession);
            userData.put(H2CO3Tools.LOGIN_UUID, uuid);
            userData.put(H2CO3Tools.LOGIN_USER_SKINTEXTURE, skinTexture);
            userData.put(H2CO3Tools.LOGIN_TOKEN, token);
            userData.put(H2CO3Tools.LOGIN_REFRESH_TOKEN, refreshToken);
            userData.put(H2CO3Tools.LOGIN_CLIENT_TOKEN, clientToken);
            userData.put(H2CO3Tools.LOGIN_IS_OFFLINE, isOffline);
            userData.put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
            userData.put(H2CO3Tools.LOGIN_INFO, new JSONArray().put(0, name).put(1, isOffline));
            json.put(name, userData);

            writeFileContent(usersFile, json.toString());
            parseJsonToUser();
        } catch (JSONException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseJsonToUser() throws IOException {
        String content = readFileContent(usersFile);
        if (TextUtils.isEmpty(content) || content.equals("{}")) {
            return;
        }

        try {
            JSONObject usersObj = new JSONObject(content);
            Iterator<String> keys = usersObj.keys();
            while (keys.hasNext()) {
                String userName = keys.next();
                JSONObject userObj = usersObj.getJSONObject(userName);

                UserBean user = new UserBean();
                user.setUserName(userName);
                user.setUserEmail(userObj.optString(H2CO3Tools.LOGIN_USER_EMAIL, ""));
                user.setUserPassword(userObj.optString(H2CO3Tools.LOGIN_USER_PASSWORD, ""));
                user.setUserType(userObj.optString(H2CO3Tools.LOGIN_USER_TYPE, ""));
                user.setApiUrl(userObj.optString(H2CO3Tools.LOGIN_API_URL, ""));
                user.setAuthSession(userObj.optString(H2CO3Tools.LOGIN_AUTH_SESSION, ""));
                user.setUuid(userObj.optString(H2CO3Tools.LOGIN_UUID, ""));
                user.setSkinTexture(userObj.optString(H2CO3Tools.LOGIN_USER_SKINTEXTURE, ""));
                user.setToken(userObj.optString(H2CO3Tools.LOGIN_TOKEN, ""));
                user.setRefreshToken(userObj.optString(H2CO3Tools.LOGIN_REFRESH_TOKEN, ""));
                user.setClientToken(userObj.optString(H2CO3Tools.LOGIN_CLIENT_TOKEN, ""));
                user.setIsSelected(userObj.optBoolean(H2CO3Tools.LOGIN_IS_SELECTED, false));
                user.setIsOffline(userObj.optBoolean(H2CO3Tools.LOGIN_IS_OFFLINE, true));

                JSONArray loginInfoArray = userObj.optJSONArray(H2CO3Tools.LOGIN_INFO);
                if (loginInfoArray != null && loginInfoArray.length() >= 2) {
                    user.setUserInfo(loginInfoArray.optString(0, ""));
                    user.setUserPassword(loginInfoArray.optString(1, ""));
                }

                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void reSetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
    }

    public static List<UserBean> getUserList() {
        return userList;
    }

    public static void setUserState(UserBean user) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, user.getUserName());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_EMAIL, user.getUserEmail());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_PASSWORD, user.getUserPassword());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, user.getUserType());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_API_URL, user.getApiUrl());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, user.getAuthSession());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, user.getUuid());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_SKINTEXTURE, user.getSkinTexture());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, user.getToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_REFRESH_TOKEN, user.getRefreshToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_CLIENT_TOKEN, user.getClientToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_INFO, user.getUserInfo());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_IS_OFFLINE, user.getIsOffline());
    }

    public static String getUserJson() {
        try {
            return readFileContent(usersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setUserJson(String json) {
        try {
            writeFileContent(usersFile, json);
            parseJsonToUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data, StandardCharsets.UTF_8);
        }
    }

    private static void writeFileContent(File file, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            fos.write(data);
        }
    }
}