package com.aause2s15.gruppe3.cheatingandroid;

/**
 * Created by Simon on 20.04.2015.
 */
public class Card {

    private String type;
    private String value;
    private int image;

    public Card(String type, String value, int image) {
        this.type = type;
        this.value = value;
        this.image = image;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public int getImage() {
        return this.image;
    }

}
