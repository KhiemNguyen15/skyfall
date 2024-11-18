package com.github.skyfall.data.model;

import java.util.Date;

public class Timestamp {
    private long _seconds;

    public Timestamp() {}

    public Date getTimestamp() {
        return new Date(_seconds * 1000L);
    }
}
