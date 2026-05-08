package com.juaracoding.pcmspringboot31.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CustomHttpFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inisialisasi filter jika diperlukan
        System.out.println("Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println("Content-Type : "+request.getContentType());
        String contentType = request.getContentType()==null?"":request.getContentType();
        if(!contentType.contains("multipart/form-data")){
            request = new MyHttpServletRequestWrapper(httpRequest);
        }
        // Logika sebelum memproses request
//        System.out.println("Request URI: " + httpRequest.getRequestURI());
//        System.out.println("Request Method: " + httpRequest.getMethod());

        // Melanjutkan ke filter chain
        chain.doFilter(request, response);

        // Logika setelah memproses request
        System.out.println("Response sent");
    }

    @Override
    public void destroy() {
        // Logika cleanup jika diperlukan
        System.out.println("Filter destroyed");
    }
}
