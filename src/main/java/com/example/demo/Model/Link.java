package com.example.demo.Model;

import java.sql.Timestamp;
import java.time.Instant;

public class Link {
    private String originalUrl;
    private Long id;
    private String code;
    private Instant createdAt;
    private Instant expireAt;

    Link(){}
    Link(String url){
        this.originalUrl = url;
    }

    void setOriginalUrl(String s) {
        this.originalUrl = s;
    }

    void setId(Long id) {
        this.id = id;
    }

    void setCode(String code) {
        this.code = code;
    }

    void setCreatedAt(Instant crA) {
        this.createdAt = crA;
    }

    void setExpireAt(Instant expA) {
        this.expireAt = expA;
    }

    String getOriginalUrl(){
        return this.originalUrl;
    }

    String getCode(){
        return this.code;
    }

    Long getId(){
        return this.id;
    }

    Instant getCreatedAt(){
        return this.createdAt;
    }

    Instant getExpireAt(){
        return this.expireAt;
    }


}
