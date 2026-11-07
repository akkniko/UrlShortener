package com.example.demo.exception;
//error 404
public class LinkNotFoundException extends ShortenerException{
    public LinkNotFoundException(String code){
        super("Link with code: " + code + " not found");
    }
}
