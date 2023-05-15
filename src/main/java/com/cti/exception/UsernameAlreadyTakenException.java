package com.cti.exception;

public class UsernameAlreadyTakenException extends Exception{
    public UsernameAlreadyTakenException() {
        super("Error: Username is already taken!");
    }
}
