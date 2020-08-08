package com.osozos.community.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ApiErrorController implements ErrorController {

    private final String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @GetMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHttp(HttpServletRequest request) {
        return "404";
    }
}
