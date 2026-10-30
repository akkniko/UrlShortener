package com.example.demo.model;

import java.time.Instant;

public class Link {
    private String originalUrl;
    private Long id;
    private String code;
    private Instant createdAt;
    private Instant expireAt;

//    public Link(){}
    public Link(String url){
        this.originalUrl = url;

    }

    public void setOriginalUrl(String s) {
        this.originalUrl = s;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreatedAt(Instant crA) {
        this.createdAt = crA;
    }

    public void setExpireAt(Instant expA) {
        this.expireAt = expA;
    }

    public String getOriginalUrl(){
        return this.originalUrl;
    }

    public String getCode(){
        return this.code;
    }

    public Long getId(){
        return this.id;
    }

    public Instant getCreatedAt(){
        return this.createdAt;
    }

    public Instant getExpireAt(){
        return this.expireAt;
    }
}
