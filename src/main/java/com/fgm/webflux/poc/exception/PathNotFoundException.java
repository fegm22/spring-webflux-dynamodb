package com.fgm.webflux.poc.exception;

public class PathNotFoundException extends Exception {

    public PathNotFoundException(final String message) {
        super(message);
    }
}
