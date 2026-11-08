package com.example.demo.filter;

import com.example.demo.web.ApiExceptionHandler;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RequestFilter implements Filter {
    private final ApiExceptionHandler exceptionHandler;

    public RequestFilter(ApiExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            chain.doFilter(request, response);
        } catch (Exception exception) {
            exceptionHandler.handle(exception, (HttpServletRequest) request,
                    (HttpServletResponse) response);
        }
    }
}
