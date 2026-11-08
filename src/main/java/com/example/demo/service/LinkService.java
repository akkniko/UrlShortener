package com.example.demo.service;

import com.example.demo.dto.CreateLinkRequest;
import com.example.demo.dto.LinkView;
import com.example.demo.exception.AliasTakenException;
import com.example.demo.exception.CodeGenerationException;
import com.example.demo.exception.InvalidUrlException;
import com.example.demo.exception.LinkExpiredException;
import com.example.demo.exception.LinkNotFoundException;
import com.example.demo.model.Click;
import com.example.demo.model.Link;
import com.example.demo.repository.ClickRepository;
import com.example.demo.repository.LinkRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class LinkService {
    private static final int MAX_URL_LENGTH = 2048;
    private static final int MAX_CODE_LENGTH = 32;

    private final LinkRepository linkRepository;
    private final ClickRepository clickRepository;
    private final CodeGenerator codeGenerator;
    private final Duration linkTtl;
    private final int maxCodeAttempts;

    public LinkService(LinkRepository linkRepository, ClickRepository clickRepository,
                       CodeGenerator codeGenerator, Duration linkTtl) {
        this(linkRepository, clickRepository, codeGenerator, linkTtl, 100);
    }

    public LinkService(LinkRepository linkRepository, ClickRepository clickRepository,
                       CodeGenerator codeGenerator, Duration linkTtl, int maxCodeAttempts) {
        this.linkRepository = Objects.requireNonNull(linkRepository);
        this.clickRepository = Objects.requireNonNull(clickRepository);
        this.codeGenerator = Objects.requireNonNull(codeGenerator);
        this.linkTtl = Objects.requireNonNull(linkTtl);
        if (linkTtl.isZero() || linkTtl.isNegative()) {
            throw new IllegalArgumentException("Link TTL must be positive");
        }
        if (maxCodeAttempts <= 0) {
            throw new IllegalArgumentException("Maximum code attempts must be positive");
        }
        this.maxCodeAttempts = maxCodeAttempts;
    }

    public LinkView create(CreateLinkRequest request) {
        if (request == null) {
            throw new InvalidUrlException("Request must not be null");
        }
        String originalUrl = validateUrl(request.originalUrl());
        String alias = normalizeAlias(request.alias());

        Link link = new Link(originalUrl);
        link.setExpireAt(link.getCreatedAt().plus(linkTtl));

        if (alias != null) {
            if (linkRepository.findByCode(alias).isPresent()) {
                throw new AliasTakenException(alias);
            }
            link.setCode(alias);
            return toView(linkRepository.save(link));
        }

        for (int attempt = 0; attempt < maxCodeAttempts; attempt++) {
            String code = codeGenerator.generate();
            if (linkRepository.findByCode(code).isPresent()) {
                continue;
            }
            link.setCode(code);
            try {
                return toView(linkRepository.save(link));
            } catch (AliasTakenException collision) {
                // A concurrent request may insert the same code after findByCode.
            }
        }
        throw new CodeGenerationException("Could not generate a unique code after " + maxCodeAttempts + " attempts");
    }

    public String followToCode(String code) {
        return followToCode(code, null, null);
    }

    public String followToCode(String code, String referrer, String userAgent) {
        Link link = findLink(code);
        Instant now = Instant.now();
        if (link.getExpireAt() != null && !now.isBefore(link.getExpireAt())) {
            throw new LinkExpiredException(code);
        }
        clickRepository.save(new Click(null, link.getId(), now, referrer, userAgent));
        return link.getOriginalUrl();
    }

    public Link getLink(String code) {
        return findLink(code);
    }

    public LinkView getInfoAboutLink(String code) {
        return toView(findLink(code));
    }

    private Link findLink(String code) {
        if (code == null || code.isBlank()) {
            throw new LinkNotFoundException(String.valueOf(code));
        }
        return linkRepository.findByCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));
    }

    private String validateUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new InvalidUrlException("URL must not be empty");
        }
        String value = rawUrl.trim();
        if (value.length() > MAX_URL_LENGTH) {
            throw new InvalidUrlException("URL must not exceed " + MAX_URL_LENGTH + " characters");
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            if (!uri.isAbsolute() || scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null || uri.getHost().isBlank()) {
                throw new InvalidUrlException("Only absolute HTTP and HTTPS URLs are accepted");
            }
            return uri.toASCIIString();
        } catch (URISyntaxException exception) {
            throw new InvalidUrlException("URL has invalid syntax", exception);
        }
    }

    private String normalizeAlias(String rawAlias) {
        if (rawAlias == null || rawAlias.isBlank()) {
            return null;
        }
        String alias = rawAlias.trim();
        if (alias.length() > MAX_CODE_LENGTH || !alias.matches("[A-Za-z0-9_-]{1,32}")) {
            throw new IllegalArgumentException("Alias must contain 1-32 Latin letters, digits, '_' or '-'");
        }
        return alias;
    }

    private LinkView toView(Link link) {
        return new LinkView(link.getCode(), link.getOriginalUrl(), link.getCreatedAt(), link.getExpireAt());
    }
}
