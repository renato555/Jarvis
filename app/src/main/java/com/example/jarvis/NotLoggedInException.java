package com.example.jarvis;

public class NotLoggedInException extends RuntimeException {

    public NotLoggedInException( String msg){
        super( msg);
    }
}
