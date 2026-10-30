package com.example.demo.service;

import com.example.demo.dto.CreateLinkRequest;
import com.example.demo.dto.LinkView;
import com.example.demo.model.Link;
import com.example.demo.repository.ClickRepository;
import com.example.demo.repository.LinkRepository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

public class LinkService {
    private ClickRepository clickRepository;
    private LinkRepository linkRepository;
    private CodeGenerator codeGenerator;

    //todo: бизнес логика

    public LinkService(LinkRepository lr, CodeGenerator cg){
        this.linkRepository = lr;
        this.codeGenerator = cg;
    }

    LinkService create(CreateLinkRequest request){
        if(request == null || request.originalUrl().isBlank())
        {
            throw new IllegalArgumentException("Url must be non-empty");
        }
        Instant createAt = Instant.now();
        Instant expireAt = request.expireAt(); //todo: подумать над сроком жизни ссылки

        Link l = new Link(request.originalUrl());

        l.setCode(codeGenerator.generate());
        String code = l.getCode();

        //в конструктор LinkView надо передать время создания, короткую
        // и длинную ссылку, время жизни
        LinkRepository.save(l);
        //логика такая: создаю все поля, которые нужны в LinkView, все сохраняю в репозиторий и
        //делаю return LinkView lv = new LinkView(...)
        //return new LinkView();
    }

}



