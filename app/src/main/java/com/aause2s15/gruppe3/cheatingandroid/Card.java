package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Simon on 20.04.2015.
 */
public class Card {

    private String type;
    private String value;
    private String tag;
    private int image;
    private ImageView imgView;

    public Card(Context context, String type, String value, int image) {
        this.type = type;
        this.value = value;
        this.tag = type+" "+value;
        this.image = image;
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

    public String getTag() {
        return this.tag;
    }

    public void setX(float x) {
        this.imgView.setX(x);
    }

}
