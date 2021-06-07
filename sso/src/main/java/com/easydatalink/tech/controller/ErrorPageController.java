package com.easydatalink.tech.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 错误页面
 * @author terry
 *
 */
@Controller
public class ErrorPageController implements ErrorController {
	 private static final String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping(value = ERROR_PATH, produces = {MediaType.TEXT_HTML_VALUE})
    public String handleError(HttpServletRequest request) {
        //获取statusCode:403,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode.intValue() == 403) {
            return "/error/403";
        } else if (statusCode.intValue() == 404) {
            return "/error/404";
        } else {
            return "/error/500";
        }
    }

    @GetMapping("/to500")
    public String to500(HttpServletRequest request) {
        throw new RuntimeException();
    }

}

