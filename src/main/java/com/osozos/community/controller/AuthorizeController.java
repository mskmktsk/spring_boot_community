package com.osozos.community.controller;

import com.google.gson.Gson;
import com.osozos.community.common.Result;
import com.osozos.community.dto.GithubUserDTO;
import com.osozos.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    private Gson gson = new Gson();

    @GetMapping("/github/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state) throws IOException {
        GithubUserDTO user = githubProvider.getUserInfo(code, state);
        return getHTML(user);
    }

    private String getHTML(Object data) {
        String html = "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "</head>" +
                "<body>\n" +
                "   <p style=\"text-align: center;\"><h3>登录中....</h3></p>\n" +
                "</body>" +
                "\n" +
                "<script>\n" +
                "  " +
                "    var message =";

        if (data == null) {
            html += gson.toJson(Result.failed("数据获取失败"));
        } else {
            html += gson.toJson(Result.success(data, "数据获取成功"));
        }

        html += ";\n" +
                "    window.opener.parent.postMessage(message, '*');\n" +
                "    parent.window.close();\n" +
                "  \n" +
                "</script>\n";

        return html;
    }
}
