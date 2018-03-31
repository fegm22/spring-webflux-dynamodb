package com.fgm.webflux.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Leave {

    private final String id;
    private final String startDate;
    private final String dayOff;

    @JsonCreator
    public Leave(final String id, final String startDate, final String dayOff) {
        this.id = id;
        this.startDate = startDate;
        this.dayOff = dayOff;
    }

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDayOff() {
        return dayOff;
    }
}
