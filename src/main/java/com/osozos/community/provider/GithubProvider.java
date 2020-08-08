package com.osozos.community.provider;

import com.google.gson.Gson;
import com.osozos.community.dto.AccessTokenDTO;
import com.osozos.community.dto.GithubUserDTO;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GithubProvider {

    @Value("${github.user}")
    private String url_user;
    @Value("${github.access_token}")
    private String url_access_token;
    @Value("${github.client_id}")
    private String client_id;
    @Value("${github.client_secret}")
    private String client_secret;
    @Value("${github.redirect_uri}")
    private String redirect_uri;
    @Value("${github.token_regexp}")
    private String token_regexp;

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
        String url = url_user + accessToken;

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
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setState(state);

        RequestBody body = RequestBody.create(JSON, gson.toJson(accessTokenDTO));
        Request request = new Request.Builder()
                .url(url_access_token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String accessToken = response.body().string();
            Pattern p = Pattern.compile(token_regexp);
            Matcher m = p.matcher(accessToken);
            return m.find() ? m.group(1) : null;
        }
    }
}
