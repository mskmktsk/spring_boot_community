package com.osozos.community.provider;

import com.google.gson.Gson;
import com.osozos.community.dto.AccessTokenDTO;
import com.osozos.community.dto.GithubUserDTO;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GithubProvider {

    private final String USER_INFO_URL = "https://api.github.com/user?access_token=";
    private final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String CLIENT_ID = "4b78273612d5e89f0f0c";
    private final String CLIENT_SECRET = "4fe8db96f3552a4a1c8c518dc2dd3dab5044d0ad";
    private final String REDIRECT_URI = "http://localhost:8000/github/callback";
    private final String TOKEN_REGEXP = "token=(\\S+?)&";

    /**
     * 获取 Github 用户信息
     * 通过 getAccessToken 方法获取 token, 将 token 添加到获取用户信息 url 中, 并通过 okhttp 发送请求
     * @return
     */
    public GithubUserDTO getUserInfo(String code, String state) throws IOException {
        String accessToken = this.getAccessToken(code, state);
        if (accessToken == null) {
            return null;
        }
        String url = USER_INFO_URL + accessToken;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(res, GithubUserDTO.class);
        }
    }

    /**
     * 通过 Github 回调得到的 code, 获取请求用户信息的 token
     * @param code 获取 token 的 code
     * @param state 不可猜测的随机字符串。它用于防止跨站点请求伪造攻击。
     * @return access_token
     */
    private String getAccessToken(String code, String state) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(CLIENT_ID);
        accessTokenDTO.setClient_secret(CLIENT_SECRET);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(REDIRECT_URI);
        accessTokenDTO.setState(state);

        RequestBody body = RequestBody.create(JSON, gson.toJson(accessTokenDTO));
        Request request = new Request.Builder()
                .url(ACCESS_TOKEN_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String accessToken = response.body().string();
            Pattern p = Pattern.compile(TOKEN_REGEXP);
            Matcher m = p.matcher(accessToken);
            return m.find() ? m.group(1) : null;
        }
    }
}
