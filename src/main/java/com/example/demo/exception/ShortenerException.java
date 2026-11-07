package com.example.demo.exception;

//RuntimeExc- исключение непроверяемое
public abstract class ShortenerException extends RuntimeException {
    public ShortenerException(String message){
        super(message);
    }

    public ShortenerException(String message, Throwable cause){
        super(message, cause);
    }

}
