package com.example.demo.service;

import java.security.SecureRandom;

public class CodeGenerator {
    private final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; //62 symbol max
    int default_length = 7;
    SecureRandom random = new SecureRandom();

    String generate(){
        StringBuilder sb = new StringBuilder(default_length);
        for(int i = 0; i < default_length; i++){
            int randomIndex = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(randomIndex));
        }
        return sb.toString();
    }


    String generate(int length){
        if(length <=0 ){
            throw new IllegalArgumentException("String length must be >= 0");
        }
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++){
            int randomIndex = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(randomIndex));
        }
        return sb.toString();
    }

}
