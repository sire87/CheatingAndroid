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

    public Card(Context context, String type, String value, int image, int order) {
        this.type = type;
        this.value = value;
        this.tag = type+" "+value;
        this.image = image;
        this.order = order;

        this.imgView = new ImageView(context);
        this.imgView.setImageResource(this.image);
        this.imgView.setX(0);
        this.imgView.setY(0);
        this.imgView.setAdjustViewBounds(true);
        this.imgView.setMaxHeight(200);
        this.imgView.setMaxWidth(200);
        this.imgView.setTag(this.tag);
        this.imgView.setId(this.image);
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
