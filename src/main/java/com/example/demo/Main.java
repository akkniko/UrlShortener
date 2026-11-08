package com.example.demo;

import com.example.demo.config.Database;
import com.example.demo.filter.RequestFilter;
import com.example.demo.repository.jdbc.JdbcClickRepository;
import com.example.demo.repository.jdbc.JdbcLinkRepository;
import com.example.demo.service.CodeGenerator;
import com.example.demo.service.LinkService;
import com.example.demo.service.StatsService;
import com.example.demo.servlets.HomeServlet;
import com.example.demo.servlets.LinkApiServlet;
import com.example.demo.servlets.RedirectServlet;
import com.example.demo.web.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.DispatcherType;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

public class Main {
    private Main() { }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(env("PORT", "8080"));
        Duration ttl = Duration.ofHours(Long.parseLong(env("LINK_TTL_HOURS", "24")));

        HikariDataSource dataSource = Database.createDataSource();
        Tomcat tomcat = new Tomcat();
        try {
            Database.initSchema(dataSource);

            JdbcLinkRepository linkRepository = new JdbcLinkRepository(dataSource);
            JdbcClickRepository clickRepository = new JdbcClickRepository(dataSource);
            LinkService linkService = new LinkService(linkRepository, clickRepository,
                    new CodeGenerator(), ttl);
            StatsService statsService = new StatsService(linkRepository, clickRepository);

            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            tomcat.setBaseDir(Files.createTempDirectory("url-shortener-tomcat-").toString());
            tomcat.setPort(port);
            Connector connector = tomcat.getConnector();
            connector.setURIEncoding("UTF-8");

            Context context = tomcat.addContext("", new File(".").getAbsolutePath());
            Tomcat.addServlet(context, "home", new HomeServlet());
            Tomcat.addServlet(context, "link-api",
                    new LinkApiServlet(linkService, statsService, objectMapper));
            Tomcat.addServlet(context, "redirect", new RedirectServlet(linkService));
            context.addServletMapping("/", "home");
            context.addServletMapping("/api/links/*", "link-api");
            context.addServletMapping("/r/*", "redirect");

            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName("request-filter");
            filterDef.setFilter(new RequestFilter(new ApiExceptionHandler(objectMapper)));
            context.addFilterDef(filterDef);

            FilterMap filterMap = new FilterMap();
            filterMap.setFilterName("request-filter");
            filterMap.addURLPattern("/*");
            filterMap.setDispatcher(DispatcherType.REQUEST.name());
            context.addFilterMapBefore(filterMap);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    tomcat.stop();
                    tomcat.destroy();
                } catch (LifecycleException exception) {
                    System.err.println("Error while stopping Tomcat: " + exception.getMessage());
                } finally {
                    dataSource.close();
                }
            }, "url-shortener-shutdown"));

            tomcat.start();
            System.out.println("URL Shortener started: http://localhost:" + port);
            tomcat.getServer().await();
        } catch (Exception exception) {
            dataSource.close();
            try {
                tomcat.destroy();
            } catch (LifecycleException ignored) {
                // The server may not have reached the initialized state.
            }
            throw exception;
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
