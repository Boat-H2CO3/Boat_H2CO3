package org.koishi.launcher.h2co3.core.login.microsoft;

import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.Texture.Texture;
import org.koishi.launcher.h2co3.core.login.Texture.TextureType;
import org.koishi.launcher.h2co3.core.login.utils.AuthenticationException;
import org.koishi.launcher.h2co3.core.login.utils.HttpRequest;
import org.koishi.launcher.h2co3.core.login.utils.NetworkUtils;
import org.koishi.launcher.h2co3.core.login.utils.ResponseCodeException;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.gson.tools.TolerableValidationException;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MicrosoftLoginUtils {
    private static final String AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    private static final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MC_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";

    public String mcUuid;
    public String msRefreshToken;
    public String mcName;
    public String mcToken;
    public String tokenType;
    public boolean doesOwnGame;

    public MicrosoftLoginUtils(boolean isRefresh, String authCode) throws IOException, JSONException {
        acquireAccessToken(isRefresh, authCode);
    }

    public static Optional<Map<TextureType, Texture>> getTextures(MinecraftProfileResponse profile) {
        Objects.requireNonNull(profile);

        Map<TextureType, Texture> textures = new EnumMap<>(TextureType.class);

        if (!profile.skins.isEmpty()) {
            textures.put(TextureType.SKIN, new Texture(profile.skins.get(0).url, null));
        }
        // if (!profile.capes.isEmpty()) {
        // textures.put(TextureType.CAPE, new Texture(profile.capes.get(0).url, null);
        // }

        return Optional.of(textures);
    }

    public static MinecraftProfileResponse getMinecraftProfile(String tokenType, String accessToken)
            throws IOException, AuthenticationException {
        HttpURLConnection conn = HttpRequest.GET(MC_PROFILE_URL)
                .authorization(tokenType, accessToken)
                .createConnection();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new NoMinecraftJavaEditionProfileException();
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new ResponseCodeException(new URL(MC_PROFILE_URL), responseCode);
        }

        String result = NetworkUtils.readData(conn);
        return JsonUtils.fromNonNullJson(result, MinecraftProfileResponse.class);
    }

    public static String ofJSONData(Map<String, Object> data) {
        return new JSONObject(data).toString();
    }

    public static String ofFormData(Map<String, String> data) {
        Uri.Builder builder = new Uri.Builder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getEncodedQuery();
    }

    private static void setRequestProperties(HttpURLConnection conn, String contentType, String req) {
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(req.getBytes(StandardCharsets.UTF_8).length));
    }

    private static void setRequestOutput(HttpURLConnection conn, String req) throws IOException {
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try (OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        return H2CO3Tools.read(conn.getInputStream());
    }

    private static void throwResponseError(HttpURLConnection conn) throws IOException {
        String otherErrStr = "";
        String errStr = readResponse(conn);
        Log.i("MicroAuth", "Error code: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + "\n" + errStr);

        if (errStr.contains("NOT_FOUND") &&
                errStr.contains("The server has not found anything matching the request URI")) {
            // TODO localize this
            otherErrStr = "It seems that this Microsoft Account does not own the game. Make sure that you have bought/migrated to your Microsoft account.";
        }

        throw new RuntimeException(otherErrStr + "\n\nMSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ", error stream:\n" + errStr);
    }

    private void acquireAccessToken(boolean isRefresh, String authCode) throws IOException, JSONException {
        URL url = new URL(AUTH_TOKEN_URL);
        Log.i("MicroAuth", "isRefresh=" + isRefresh + ", authCode= " + authCode);
        Map<String, String> data = new HashMap<>();

        data.put("client_id", "00000000402b5328");
        data.put(isRefresh ? "refresh_token" : "code", authCode);
        data.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
        data.put("redirect_url", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        String req = ofFormData(data);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn, "application/x-www-form-urlencoded", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            msRefreshToken = jo.getString("refresh_token");
            Log.i("MicroAuth", "Acess Token = " + jo.getString("access_token"));
            acquireXBLToken(jo.getString("access_token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireXBLToken(String accessToken) throws IOException, JSONException {
        URL url = new URL(XBL_AUTH_URL);

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", accessToken);
        data.put("Properties", properties);
        data.put("RelyingParty", "http://auth.xboxlive.com");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            Log.i("MicroAuth", "Xbl Token = " + jo.getString("Token"));
            acquireXsts(jo.getString("Token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireXsts(String xblToken) throws IOException, JSONException {
        URL url = new URL(XSTS_AUTH_URL);
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("SandboxId", "RETAIL");
        properties.put("UserTokens", Collections.singleton(xblToken));
        data.put("Properties", properties);
        data.put("RelyingParty", "rp://api.minecraftservices.com/");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            String uhs = jo.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            Log.i("MicroAuth", "Xbl Xsts = " + jo.getString("Token") + "; Uhs = " + uhs);
            acquireMinecraftToken(uhs, jo.getString("Token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireMinecraftToken(String xblUhs, String xblXsts) throws IOException, JSONException {
        URL url = new URL(MC_LOGIN_URL);

        Map<String, Object> data = new HashMap<>();
        data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);

        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            Log.i("MicroAuth", "MC token: " + jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            tokenType = jo.getString("token_type");
            checkMcProfile(jo.getString("access_token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void checkMcProfile(String mcAccessToken) throws IOException, JSONException {
        URL url = new URL(MC_PROFILE_URL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s = readResponse(conn);
            Log.i("MicroAuth", "profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = jsonObject.getString("name");
            String uuid = jsonObject.getString("id");
            String uuidDashes = uuid.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            doesOwnGame = true;
            Log.i("MicroAuth", "UserName = " + name);
            Log.i("MicroAuth", "Uuid Minecraft = " + uuidDashes);
            mcName = name;
            mcUuid = uuidDashes;
        } else {
            Log.i("MicroAuth", "It seems that this Microsoft Account does not own the game.");
            doesOwnGame = false;
            throwResponseError(conn);
        }
    }

    public static class MinecraftProfileResponseSkin implements Validation {
        public String id;
        public String state;
        public String url;
        public String variant; // CLASSIC, SLIM
        public String alias;

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(state, "state cannot be null");
            Validation.requireNonNull(url, "url cannot be null");
            Validation.requireNonNull(variant, "variant cannot be null");
        }
    }

    public static class MinecraftProfileResponseCape {

    }

    public static class MinecraftProfileResponse extends MinecraftErrorResponse implements Validation {
        @SerializedName("id")
        UUID id;
        @SerializedName("name")
        String name;
        @SerializedName("skins")
        List<MinecraftProfileResponseSkin> skins;
        @SerializedName("capes")
        List<MinecraftProfileResponseCape> capes;

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(name, "name cannot be null");
            Validation.requireNonNull(skins, "skins cannot be null");
            Validation.requireNonNull(capes, "capes cannot be null");
        }
    }

    private static class MinecraftErrorResponse {
        public String path;
        public String errorType;
        public String error;
        public String errorMessage;
        public String developerMessage;
    }

    public static class NoMinecraftJavaEditionProfileException extends AuthenticationException {
    }
}