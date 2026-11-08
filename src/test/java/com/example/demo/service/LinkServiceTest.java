package com.example.demo.service;

import com.example.demo.dto.CreateLinkRequest;
import com.example.demo.exception.AliasTakenException;
import com.example.demo.exception.CodeGenerationException;
import com.example.demo.exception.InvalidUrlException;
import com.example.demo.exception.LinkExpiredException;
import com.example.demo.exception.LinkNotFoundException;
import com.example.demo.model.Click;
import com.example.demo.model.Link;
import com.example.demo.repository.ClickRepository;
import com.example.demo.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LinkServiceTest {
    private LinkRepository linkRepository;
    private ClickRepository clickRepository;
    private CodeGenerator codeGenerator;
    private LinkService service;

    @BeforeEach
    void setUp() {
        linkRepository = mock(LinkRepository.class);
        clickRepository = mock(ClickRepository.class);
        codeGenerator = mock(CodeGenerator.class);
        service = new LinkService(linkRepository, clickRepository, codeGenerator, Duration.ofHours(24), 3);
    }

    @Test
    void createsLinkWithGeneratedCode() {
        when(codeGenerator.generate()).thenReturn("abc1234");
        when(linkRepository.findByCode("abc1234")).thenReturn(Optional.empty());
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> {
            Link link = invocation.getArgument(0);
            link.setId(10L);
            return link;
        });

        var result = service.create(new CreateLinkRequest("https://example.com/path", null));

        assertEquals("abc1234", result.code());
        assertEquals("https://example.com/path", result.originalUrl());
        assertNotNull(result.expireAt());
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void rejectsInvalidUrls() {
        assertThrows(InvalidUrlException.class,
                () -> service.create(new CreateLinkRequest("ftp://example.com", null)));
        assertThrows(InvalidUrlException.class,
                () -> service.create(new CreateLinkRequest("not a url", null)));
        assertThrows(InvalidUrlException.class,
                () -> service.create(new CreateLinkRequest("https:///missing-host", null)));
    }

    @Test
    void rejectsTakenCustomAlias() {
        when(linkRepository.findByCode("custom")).thenReturn(Optional.of(new Link("https://other.com")));
        assertThrows(AliasTakenException.class,
                () -> service.create(new CreateLinkRequest("https://example.com", "custom")));
        verify(linkRepository, never()).save(any());
    }

    @Test
    void retriesGeneratedCodeWhenCollisionExists() {
        when(codeGenerator.generate()).thenReturn("taken01", "free001");
        when(linkRepository.findByCode("taken01"))
                .thenReturn(Optional.of(new Link("https://existing.com")));
        when(linkRepository.findByCode("free001")).thenReturn(Optional.empty());
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new CreateLinkRequest("https://example.com", null));

        assertEquals("free001", result.code());
        verify(codeGenerator, times(2)).generate();
    }

    @Test
    void retriesWhenDatabaseDetectsConcurrentCollision() {
        when(codeGenerator.generate()).thenReturn("taken01", "free001");
        when(linkRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(linkRepository.save(any(Link.class)))
                .thenThrow(new AliasTakenException("taken01"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new CreateLinkRequest("https://example.com", null));

        assertEquals("free001", result.code());
        verify(linkRepository, times(2)).save(any(Link.class));
    }


    @Test
    void stopsAfterMaximumNumberOfGeneratedCodeCollisions() {
        when(codeGenerator.generate()).thenReturn("taken01");
        when(linkRepository.findByCode("taken01"))
                .thenReturn(Optional.of(new Link("https://existing.com")));

        assertThrows(CodeGenerationException.class,
                () -> service.create(new CreateLinkRequest("https://example.com", null)));
        verify(codeGenerator, times(3)).generate();
        verify(linkRepository, never()).save(any(Link.class));
    }

    @Test
    void followsActiveLinkAndStoresClick() {
        Link link = new Link("https://example.com");
        link.setId(5L);
        link.setCode("abc1234");
        link.setExpireAt(Instant.now().plusSeconds(600));
        when(linkRepository.findByCode("abc1234")).thenReturn(Optional.of(link));

        String target = service.followToCode("abc1234", "https://referrer.com", "JUnit");

        assertEquals("https://example.com", target);
        verify(clickRepository).save(argThat(click -> click.linkId().equals(5L)
                && "https://referrer.com".equals(click.referrer())
                && "JUnit".equals(click.userAgent())));
    }

    @Test
    void rejectsExpiredLinkWithoutSavingClick() {
        Link link = new Link("https://example.com");
        link.setCode("expired1");
        link.setExpireAt(Instant.now().minusSeconds(1));
        when(linkRepository.findByCode("expired1")).thenReturn(Optional.of(link));

        assertThrows(LinkExpiredException.class, () -> service.followToCode("expired1"));
        verify(clickRepository, never()).save(any(Click.class));
    }

    @Test
    void reportsMissingLink() {
        when(linkRepository.findByCode("missing1")).thenReturn(Optional.empty());
        assertThrows(LinkNotFoundException.class, () -> service.followToCode("missing1"));
    }
}
