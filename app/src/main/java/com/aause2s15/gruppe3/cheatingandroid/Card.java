package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;
import android.widget.ImageView;

/**
 * Card Class
 *
 * @author Simon Reisinger
 * @version 1.0
 * @since 2015-04-20
 */
public class Card implements Comparable<Card> {

    private String type;
    private String value;
    private String tag;
    private int image;
    private ImageView imgView;
    private int order;

    /**
     * Constructs a Card object.
     *
     * @param context the current context
     * @param type the type of the card
     * @param value the value of the card
     * @param image the drawable id of the card
     * @param order the order of the card within a card deck
     * @param deckID the id of the card deck the card belongs to
     */
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

    /**
     * Returns the ImageView of the specified card.
     *
     * @return the ImagView of the specified card
     */
    public ImageView getImageView() {
        return this.imgView;
    }

    /**
     * Returns to Drawable of the specified card.
     *
     * @return the Drawable of the specified card.
     */
    public int getImage() {
        return this.image;
    }

    /**
     * Returns the tag containing deck id, card type and card value of the specified card.
     *
     * @return the tag of the specified card.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Returns the order of the specified card within the card deck.
     *
     * @return the order of the specified card within the card deck
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Returns the type of the specified card.
     *
     * @return the string containing the type of the specified card
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the value of the specified card.
     *
     * @return the string containing the value of the specified card.
     */
    public String getValue() {
        return this.value;
    }


    /**
     * Compares a card to another card and returns an integer with a value dependent on the order
     * of the specified card and the order of the other card.
     *
     * @param another the card that the specified card is compared to
     * @return a value smaller then 0 if the order within the card deck of the specified card is
     * less then the card it is compared to, 0 if the order is equal and a value greater 0 if
     * the order is greater
     */
    public int compareTo(Card another) {
        return this.order - another.getOrder();
    }
}