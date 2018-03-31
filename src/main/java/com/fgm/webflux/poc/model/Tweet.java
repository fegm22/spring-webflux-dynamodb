package com.fgm.webflux.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Tweet {

    @JsonIgnore
    private final String id;
    private final String text;
    @JsonIgnore
    private final String createdAt;

    @JsonCreator
    public Tweet(@JsonProperty("id") final String id,
                 @JsonProperty("text") final String text,
                 @JsonProperty("createdAt") final String createdAt) {
        this.id = id!=null ? id : UUID.randomUUID().toString();
        this.text = text;
        this.createdAt = createdAt!=null ? createdAt : LocalDateTime.now().toString();
    }

    public Tweet(final String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.createdAt = LocalDateTime.now().toString();
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
