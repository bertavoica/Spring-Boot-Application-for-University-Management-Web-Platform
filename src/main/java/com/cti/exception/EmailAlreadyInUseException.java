package com.cti.exception;

public class EmailAlreadyInUseException extends Exception{
    public EmailAlreadyInUseException() {
        super("Error: Email is already in use!");
    }
}
