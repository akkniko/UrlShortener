package com.example.demo.servlets;

import com.example.demo.service.LinkService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RedirectServlet extends HttpServlet {
    private final LinkService linkService;

    public RedirectServlet(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2 || pathInfo.substring(1).contains("/")) {
            throw new IllegalArgumentException("Expected URL: /r/{code}");
        }
        String code = pathInfo.substring(1);
        String targetUrl = linkService.followToCode(code,
                request.getHeader("Referer"), request.getHeader("User-Agent"));
        response.sendRedirect(targetUrl);
    }
}
