package com.fgm.webflux.poc.exception;

public class TweetNotFoundException extends RuntimeException {

    public TweetNotFoundException(final String tweetId) {
        super("Tweet not found with id " + tweetId);
    }
}
