package com.example.demo.exception;

//error: 400
public class InvalidUrlException extends ShortenerException{
    public InvalidUrlException(String url){
        //url не надо подставлять в сообщение, поскольку:
        //если слишком большой url - сообщение раздует
        //или может быть случай url = null
        super("invalid url");
    }
}
