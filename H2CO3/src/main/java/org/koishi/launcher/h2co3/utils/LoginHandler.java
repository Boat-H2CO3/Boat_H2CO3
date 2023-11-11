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

import org.koishi.launcher.h2co3.core.login.H2CO3Auth;
import org.koishi.launcher.h2co3.core.login.Texture.Texture;
import org.koishi.launcher.h2co3.core.login.Texture.TextureType;
import org.koishi.launcher.h2co3.core.login.microsoft.Msa;
import org.koishi.launcher.h2co3.core.utils.Avatar;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class LoginHandler extends Handler {
    private final H2CO3MainActivity activity;

    public LoginHandler(H2CO3MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    public void login(Intent intent) {
        Uri data = null;
        if (intent != null) {
            data = intent.getData();
        }
        if (data != null && Objects.equals(data.getScheme(), "ms-xal-00000000402b5328") && data.getHost().equals("auth")) {
            String error = data.getQueryParameter("error");
            String error_description = data.getQueryParameter("error_description");
            if (error != null) {
                if (error_description != null && !error_description.startsWith("The user has denied access to the scope requested by the client application")) {
                    Toast.makeText(activity, "Error: " + error + ": " + error_description, Toast.LENGTH_SHORT).show();
                }
            } else {
                String code = data.getQueryParameter("code");
                new Thread(() -> {
                    try {
                        Msa msa = new Msa(false, code);
                        if (msa.doesOwnGame) {
                            Msa.MinecraftProfileResponse minecraftProfile = Msa.getMinecraftProfile(msa.tokenType, msa.mcToken);
                            Map<TextureType, Texture> map = Msa.getTextures(minecraftProfile).get();
                            Texture texture = map.get(TextureType.SKIN);
                            Bitmap skin;
                            if (texture == null) {
                                AssetManager manager = activity.getAssets();
                                InputStream inputStream;
                                inputStream = manager.open("img/alex.png");
                                skin = BitmapFactory.decodeStream(inputStream);
                            } else {
                                String u = texture.getUrl();
                                if (u != null && !u.startsWith("https")) {
                                    u = u.replaceFirst("http", "https");
                                }
                                URL url = new URL(u);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.connect();
                                InputStream inputStream = httpURLConnection.getInputStream();
                                skin = BitmapFactory.decodeStream(inputStream);
                            }
                            activity.runOnUiThread(() -> {
                                String skinTexture = Avatar.bitmapToString(skin);
                                H2CO3Auth.addUserToJson(msa.mcName, "", "", "mojang", "https://www.microsoft.com", "0", msa.mcUuid, skinTexture, msa.mcToken, msa.msRefreshToken, "00000000-0000-0000-0000-000000000000", false, false);
                                activity.popView.dismiss();
                                activity.loginDialogAlert.dismiss();
                                activity.microsoftsoftLoginWaitDialogAlert.dismiss();
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