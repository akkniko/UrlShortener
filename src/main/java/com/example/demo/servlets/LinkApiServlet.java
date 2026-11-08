package com.example.demo.servlets;

import com.example.demo.dto.CreateLinkRequest;
import com.example.demo.dto.LinkDetails;
import com.example.demo.dto.LinkView;
import com.example.demo.service.LinkService;
import com.example.demo.service.StatsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LinkApiServlet extends HttpServlet {
    private final LinkService linkService;
    private final StatsService statsService;
    private final ObjectMapper objectMapper;

    public LinkApiServlet(LinkService linkService, StatsService statsService, ObjectMapper objectMapper) {
        this.linkService = linkService;
        this.statsService = statsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CreateLinkRequest createRequest;
        try {
            createRequest = objectMapper.readValue(request.getInputStream(), CreateLinkRequest.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Request body must contain valid JSON", exception);
        }

        LinkView created = linkService.create(createRequest);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader("Location", request.getContextPath() + "/r/" + created.code());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), created);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = extractCode(request);
        LinkDetails details = statsService.getStats(code);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), details);
    }

    private String extractCode(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2 || pathInfo.substring(1).contains("/")) {
            throw new IllegalArgumentException("Expected URL: /api/links/{code}");
        }
        return pathInfo.substring(1);
    }
}
