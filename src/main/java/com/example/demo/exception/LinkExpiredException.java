package com.example.demo.exception;

//error 410
public class LinkExpiredException extends ShortenerException{
    public LinkExpiredException(String code){
        super("Link with code: " + code + " has expired");
    }

}
