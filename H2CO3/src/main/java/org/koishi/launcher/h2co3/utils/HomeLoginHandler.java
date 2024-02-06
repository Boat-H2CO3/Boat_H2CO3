package org.koishi.launcher.h2co3.utils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.login.Texture.Texture;
import org.koishi.launcher.h2co3.core.login.Texture.TextureType;
import org.koishi.launcher.h2co3.core.login.microsoft.MicrosoftLoginUtils;
import org.koishi.launcher.h2co3.core.utils.Avatar;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class HomeLoginHandler extends Handler {
    private final H2CO3MainActivity activity;

    public HomeLoginHandler(H2CO3MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    public void login(Intent intent) {
        Uri data = intent != null ? intent.getData() : null;
        if (data != null && Objects.equals(data.getScheme(), "ms-xal-00000000402b5328") && Objects.equals(data.getHost(), "auth")) {
            String error = data.getQueryParameter("error");
            String errorDescription = data.getQueryParameter("error_description");
            if (error != null) {
                if (errorDescription != null && !errorDescription.startsWith("The user has denied access to the scope requested by the h2CO3ControlClient application")) {
                    Toast.makeText(activity, "Error: " + error + ": " + errorDescription, Toast.LENGTH_SHORT).show();
                }
            } else {
                String code = data.getQueryParameter("code");
                new Thread(() -> {
                    try {
                        MicrosoftLoginUtils microsoftLoginUtils = new MicrosoftLoginUtils(false, code);
                        if (microsoftLoginUtils.doesOwnGame) {
                            MicrosoftLoginUtils.MinecraftProfileResponse minecraftProfile = MicrosoftLoginUtils.getMinecraftProfile(microsoftLoginUtils.tokenType, microsoftLoginUtils.mcToken);
                            Map<TextureType, Texture> map = MicrosoftLoginUtils.getTextures(minecraftProfile).get();
                            Texture texture = map.get(TextureType.SKIN);
                            Bitmap skin;
                            if (texture == null) {
                                AssetManager manager = activity.getAssets();
                                InputStream inputStream = null;
                                try {
                                    inputStream = manager.open("drawable/alex.png");
                                    skin = BitmapFactory.decodeStream(inputStream);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                } finally {
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                String u = texture.getUrl();
                                if (u != null && !u.startsWith("https")) {
                                    u = u.replaceFirst("http", "https");
                                }
                                URL url = new URL(u);
                                HttpURLConnection httpURLConnection = null;
                                InputStream inputStream = null;
                                try {
                                    httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.connect();
                                    inputStream = httpURLConnection.getInputStream();
                                    skin = BitmapFactory.decodeStream(inputStream);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                } finally {
                                    if (httpURLConnection != null) {
                                        httpURLConnection.disconnect();
                                    }
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            activity.runOnUiThread(() -> {
                                String skinTexture = Avatar.bitmapToString(skin);
                                H2CO3Auth.addUserToJson(microsoftLoginUtils.mcName, "", "", "1", "https://www.microsoft.com", "0", microsoftLoginUtils.mcUuid, skinTexture, microsoftLoginUtils.mcToken, microsoftLoginUtils.msRefreshToken, "00000000-0000-0000-0000-000000000000", false, false);
                                activity.popView.dismiss();
                                activity.loginDialogAlert.dismiss();
                                activity.progressDialog.dismiss();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}