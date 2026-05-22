package com.juaracoding.pcmspringboot31.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/** Response Body untuk masalah JWT / Security */
@Component("customAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        /** Response Header */
        System.out.println("Thread 401  : "+Thread.currentThread().getName());
        response.setHeader("Content-Type","application/json");
        response.setStatus(401);
        /** Response Body */
        Map<String,Object> data = new HashMap<>();
        data.put("status",401);
        data.put("success",false);
        data.put("error_code","X02002");
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("error", ConstantMessage.ERROR_AUTH);
        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }
}