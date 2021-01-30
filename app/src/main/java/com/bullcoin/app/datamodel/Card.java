package com.bullcoin.app.datamodel;

public class Card {
    public static final int CARD_NEW = 0;
    public static final int CARD_BULLTRADE = 1;
    public static final int CARD_BULLBANK = 2;

    public int type;

    public Card(int type) {
        this.type = type;
    }
}