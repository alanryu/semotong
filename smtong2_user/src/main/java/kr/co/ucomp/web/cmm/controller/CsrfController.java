package kr.co.ucomp.web.cmm.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Map<String, String> tokenInfo = new HashMap<String, String>();
        tokenInfo.put("token", csrfToken.getToken());
        tokenInfo.put("headerName", csrfToken.getHeaderName());
        return tokenInfo;
    }
}