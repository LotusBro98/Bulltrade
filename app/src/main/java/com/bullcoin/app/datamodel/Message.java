package com.bullcoin.app.datamodel;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Message {
    public static final int FROM_ME = 0;
    public static final int FROM_FRIEND = 1;

    public int source;
    public String text;
    private Date time;
    public int seq;

    public String getTime() {
        if (time == null) {
            return "";
        } else {
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            format.setTimeZone(TimeZone.getDefault());
            return format.format(time);
        }
    }

    public Message(int source, String text, String time) {
        this.source = source;
        this.text = text;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            this.time = format.parse(time);
        } catch (Exception e) {
            this.time = null;
        }
    }
}
