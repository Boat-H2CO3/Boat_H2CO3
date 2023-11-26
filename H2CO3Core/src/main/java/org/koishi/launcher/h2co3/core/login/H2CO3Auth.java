package org.koishi.launcher.h2co3.core.login;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class H2CO3Auth {
    private static final List<UserBean> userList = new ArrayList<>();

    public static void addUserToJson(String name, String email, String password, String userType, String apiUrl, String authSession, String uuid, String skinTexture, String token, String refreshToken, String clientToken, Boolean isOffline, boolean isSelected) {
        try {
            String usersJson = H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, "");
            JSONObject json = new JSONObject(usersJson.isEmpty() ? "{}" : usersJson);

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

            H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USERS, json.toString());
            parseJsonToUser();
        } catch (JSONException ignored) {
        }
    }

    public static void parseJsonToUser() {
        String usersJson = H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, "");
        if (TextUtils.isEmpty(usersJson) || usersJson.equals("{}")) {
            return;
        }

        try {
            JSONObject usersObj = new JSONObject(usersJson);
            Iterator<String> keys = usersObj.keys();
            while (keys.hasNext()) {
                String userName = keys.next();
                JSONObject userObj = usersObj.getJSONObject(userName);

                UserBean user = new UserBean();
                user.setUserName(userName);
                user.setUserEmail(getString(userObj, H2CO3Tools.LOGIN_USER_EMAIL));
                user.setUserPassword(getString(userObj, H2CO3Tools.LOGIN_USER_PASSWORD));
                user.setUserType(getString(userObj, H2CO3Tools.LOGIN_USER_TYPE));
                user.setApiUrl(getString(userObj, H2CO3Tools.LOGIN_API_URL));
                user.setAuthSession(getString(userObj, H2CO3Tools.LOGIN_AUTH_SESSION));
                user.setUuid(getString(userObj, H2CO3Tools.LOGIN_UUID));
                user.setSkinTexture(getString(userObj, H2CO3Tools.LOGIN_USER_SKINTEXTURE));
                user.setToken(getString(userObj, H2CO3Tools.LOGIN_TOKEN));
                user.setRefreshToken(getString(userObj, H2CO3Tools.LOGIN_REFRESH_TOKEN));
                user.setClientToken(getString(userObj, H2CO3Tools.LOGIN_CLIENT_TOKEN));
                user.setIsSelected(getBoolean(userObj, H2CO3Tools.LOGIN_IS_SELECTED));
                user.setIsOffline(getBoolean(userObj, H2CO3Tools.LOGIN_IS_OFFLINE));

                JSONArray loginInfoArray = userObj.getJSONArray(H2CO3Tools.LOGIN_INFO);
                if (loginInfoArray.length() >= 2) {
                    user.setUserInfo(loginInfoArray.getString(0));
                    user.setUserPassword(loginInfoArray.getString(1));
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

    public static String getString(JSONObject jsonObject, String key) {
        return jsonObject.optString(key, "");
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        return jsonObject.optBoolean(key, false);
    }

    public static List<UserBean> getUserList() {
        return userList;
    }

    public static void setUserState(UserBean user) {
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, user.getUserName());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_USER_EMAIL, user.getUserEmail());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_USER_PASSWORD, user.getUserPassword());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_USER_TYPE, user.getUserType());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_API_URL, user.getApiUrl());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_SESSION, user.getAuthSession());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_UUID, user.getUuid());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_USER_SKINTEXTURE, user.getSkinTexture());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_TOKEN, user.getToken());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_REFRESH_TOKEN, user.getRefreshToken());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_CLIENT_TOKEN, user.getClientToken());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_INFO, user.getUserInfo());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_IS_OFFLINE, user.getIsOffline());
        H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_IS_SELECTED, user.isSelected());
    }
}