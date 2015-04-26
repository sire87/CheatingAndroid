package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Simon on 20.04.2015.
 */
public class Card implements Comparable<Card> {

    private String type;
    private String value;
    private String tag;
    private int image;
    private ImageView imgView;
    private int order;

    public Card(Context context, String type, String value, int image, int order, int deckID) {
        this.type = type;
        this.value = value;
        this.tag = deckID+type+" "+value;
        this.image = image;
        this.order = order;

        this.imgView = new ImageView(context);
        this.imgView.setImageResource(this.image);
        this.imgView.setAdjustViewBounds(true);
        this.imgView.setMaxHeight(150);
        this.imgView.setMaxWidth(150);
        this.imgView.setTag(this.tag);
        this.imgView.setId(this.image+deckID);
    }

    public ImageView getImageView() {
        return this.imgView;
    }

    public int getImage() {
        return this.image;
    }

    public String getTag() {
        return this.tag;
    }

    public int getOrder() {
        return this.order;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Card another) {
        return this.order - another.getOrder();
    }
}