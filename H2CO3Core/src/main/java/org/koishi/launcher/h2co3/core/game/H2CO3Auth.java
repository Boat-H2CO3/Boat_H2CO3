package org.koishi.launcher.h2co3.core.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class H2CO3Auth {

    private static final String USER_PROPERTIES = "user_properties";
    private static final String LOGIN_USER_TYPE = "mojang";
    private static final String LOGIN_UUID = "0000-0000-0000-0000";
    private static final String LOGIN_TOKEN = "0";
    private static final String LOGIN_INFO = "login_info";
    private static final String LOGIN_IS_OFFLINE = "login_is_offline";
    private static final String LOGIN_IS_SELECTED = "login_is_selected";
    private static final List<UserBean> userList = new ArrayList<>();
    public static File serversFile = new File(H2CO3Tools.H2CO3_SETTING_DIR + "/h2co3_servers.json");
    public static File usersFile = new File(H2CO3Tools.H2CO3_SETTING_DIR, "h2co3_users.json");

    public static String getPlayerName() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, null, String.class);
    }

    public static void setPlayerName(String properties) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, properties);
    }

    public static String getAuthSession() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, "0", String.class);
    }

    public static void setAuthSession(String session) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, session);
    }

    public static String getUserProperties() {
        return H2CO3Tools.getH2CO3Value(USER_PROPERTIES, "{}", String.class);
    }

    public static void setUserProperties(String properties) {
        H2CO3Tools.setH2CO3Value(USER_PROPERTIES, properties);
    }

    public static String getUserType() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, LOGIN_USER_TYPE, String.class);
    }

    public static void setUserType(String type) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, type);
    }

    public static String getAuthUUID() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_UUID, LOGIN_UUID, String.class);
    }

    public static void setAuthUUID(String uuid) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, uuid);
    }

    public static String getAuthAccessToken() {
        return H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_TOKEN, LOGIN_TOKEN, String.class);
    }

    public static void setAuthAccessToken(String token) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, token);
    }

    public static void addUserToJson(String name, String email, String password, String userType, String apiUrl, String authSession, String uuid, String skinTexture, String token, String refreshToken, String clientToken, Boolean isOffline, boolean isSelected) {
        try {
            JSONObject json;
            if (usersFile.exists()) {
                json = new JSONObject(readFileContent(usersFile));
            } else {
                json = new JSONObject();
            }
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
            parseJsonToUser(json);
        } catch (JSONException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseJsonToUser(JSONObject usersObj) throws IOException {
        if (usersObj == null || usersObj.length() == 0) {
            return;
        }

        try {
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

    public static void resetUserState() {
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
            JSONObject jsonObject = new JSONObject(json);
            writeFileContent(usersFile, json);
            parseJsonToUser(jsonObject);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileContent(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        if (!file.canRead()) {
            throw new IOException("No permission to read file: " + file.getPath());
        }
        StringBuilder content = new StringBuilder((int) file.length());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            char[] buffer = new char[8192];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, bytesRead);
            }
        }
        return content.toString();
    }

    private static void writeFileContent(File file, String content) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getPath());
            }
        }
        if (!file.isFile()) {
            throw new IOException("Not a valid file: " + file.getPath());
        }
        if (!file.canWrite()) {
            throw new IOException("No permission to write to file: " + file.getPath());
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }
}