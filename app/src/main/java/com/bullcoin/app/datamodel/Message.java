package com.bullcoin.app.datamodel;

public class Message {
    public static final int FROM_ME = 0;
    public static final int FROM_FRIEND = 1;

    public int source;
    public String text;

    public Message(int source, String text) {
        this.source = source;
        this.text = text;
    }
}
