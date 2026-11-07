package com.example.demo.exception;
//error:409
public class AliasTakenException extends ShortenerException{
    public AliasTakenException(String alias){
        super("Alias " + alias + " already taken");
    }
}
