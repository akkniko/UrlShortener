package com.example.demo.service;

import com.example.demo.dto.CreateLinkRequest;
import com.example.demo.dto.LinkView;
import com.example.demo.model.Link;
import com.example.demo.repository.LinkRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class LinkService {
    private LinkRepository linkRepository;
    private CodeGenerator codeGenerator;

    //todo: бизнес логика

    public LinkService(LinkRepository lr, CodeGenerator cg){
        this.linkRepository = lr;
        this.codeGenerator = cg;
    }

    public LinkView create(CreateLinkRequest request){
        if(request == null || request.originalUrl() == null || request.originalUrl().isBlank())
        {
            throw new IllegalArgumentException("Url must be non-empty");
        }

        //создание ссылки
        Link l = new Link(request.originalUrl());

        Instant createAt = l.getCreatedAt();
        l.setExpireAt(createAt.plus(24, ChronoUnit.HOURS));//установка времени жизни

        //генерация кода
        l.setCode(codeGenerator.generate());
        String code = l.getCode();
        Link saved = linkRepository.save(l);

        return new LinkView(saved.getCode(), saved.getOriginalUrl(), saved.getCreatedAt(), saved.getExpireAt());
    }
}



