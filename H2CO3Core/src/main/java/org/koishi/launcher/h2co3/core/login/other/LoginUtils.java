package org.koishi.launcher.h2co3.core.login.other;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginUtils {
    private static OkHttpClient client;
    private static final LoginUtils INSTANCE=new LoginUtils();
    private String baseUrl;
    private LoginUtils(){
        client=new OkHttpClient();
    }

    public static LoginUtils getINSTANCE(){
        return INSTANCE;
    }

    public void setBaseUrl(String baseUrl){
        if (baseUrl.endsWith("/")){
            baseUrl=baseUrl.substring(0,baseUrl.length()-1);
        }
        this.baseUrl=baseUrl;
        System.out.println(this.baseUrl);
    }

    public void login(String userName,String password,Listener listener) throws IOException {
        if (Objects.isNull(baseUrl)){
            listener.onFailed("no baseUrl");
            return;
        }
        AuthRequest authRequest=new AuthRequest();
        authRequest.setUsername(userName);
        authRequest.setPassword(password);
        AuthRequest.Agent agent=new AuthRequest.Agent();
        agent.setName("Mio");
        agent.setVersion(1.0);
        authRequest.setAgent(agent);
        authRequest.setRequestUser(true);
        authRequest.setClientToken("Boat_H2CO3");
        System.out.println(new Gson().toJson(authRequest));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(authRequest));
        Request request = new Request.Builder()
                .url(baseUrl+"/authserver/authenticate")
                .post(body)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String res = response.body().string();
        System.out.println(res);
        if (response.code()==200){
            AuthResult result=new Gson().fromJson(res,AuthResult.class);
            listener.onSuccess(result);
        } else {
            listener.onFailed(response.code()+"\n"+res);
        }
    }

    public String getServeInfo(String url) {
        try {
            Request request=new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call=client.newCall(request);
            Response response = call.execute();
            String res = response.body().string();
            System.out.println(res);
            if (response.code()==200){
                return res;
            }
        }catch (Exception ignored){
        }
        return null;
    }

    public interface Listener{
        void onSuccess(AuthResult authResult);
        void onFailed(String error);
    }

}
