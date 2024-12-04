package com.github.skyfall.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Timestamp {
    private long _seconds;

    public Timestamp() {
    }

    public String getFormattedTimestamp() {
        Date date = new Date(_seconds * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
