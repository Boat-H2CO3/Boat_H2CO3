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
                user.setUserEmail(userObj.getString(H2CO3Tools.LOGIN_USER_EMAIL));
                user.setUserPassword(userObj.getString(H2CO3Tools.LOGIN_USER_PASSWORD));
                user.setUserType(userObj.getString(H2CO3Tools.LOGIN_USER_TYPE));
                user.setApiUrl(userObj.getString(H2CO3Tools.LOGIN_API_URL));
                user.setAuthSession(userObj.getString(H2CO3Tools.LOGIN_AUTH_SESSION));
                user.setUuid(userObj.getString(H2CO3Tools.LOGIN_UUID));
                user.setSkinTexture(userObj.getString(H2CO3Tools.LOGIN_USER_SKINTEXTURE));
                user.setToken(userObj.getString(H2CO3Tools.LOGIN_TOKEN));
                user.setRefreshToken(userObj.getString(H2CO3Tools.LOGIN_REFRESH_TOKEN));
                user.setClientToken(userObj.getString(H2CO3Tools.LOGIN_CLIENT_TOKEN));
                user.setIsSelected(userObj.getBoolean(H2CO3Tools.LOGIN_IS_SELECTED));
                user.setIsOffline(userObj.getBoolean(H2CO3Tools.LOGIN_IS_OFFLINE));

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
    public static List<UserBean> getUserList(){
        return userList;
    }
}
