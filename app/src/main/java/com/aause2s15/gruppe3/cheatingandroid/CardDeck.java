package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;

import java.util.Random;

/**
 * Created by Simon on 20.04.2015.
 */
public class CardDeck {

    private Card[] cardDeck;
    private int currentIndex;
    private int deckID;

    public CardDeck(Context context, int deckID) {

        this.deckID = deckID;
        cardDeck = new Card[52];
        this.currentIndex = this.cardDeck.length-1;

        cardDeck[0] = new Card(context, "Kreuz","Ass",R.drawable.card_01,13,this.deckID);
        cardDeck[1] = new Card(context, "Kreuz","2",R.drawable.card_02,1,this.deckID);
        cardDeck[2] = new Card(context, "Kreuz","3",R.drawable.card_03,2,this.deckID);
        cardDeck[3] = new Card(context, "Kreuz","4",R.drawable.card_04,3,this.deckID);
        cardDeck[4] = new Card(context, "Kreuz","5",R.drawable.card_05,4,this.deckID);
        cardDeck[5] = new Card(context, "Kreuz","6",R.drawable.card_06,5,this.deckID);
        cardDeck[6] = new Card(context, "Kreuz","7",R.drawable.card_07,6,this.deckID);
        cardDeck[7] = new Card(context, "Kreuz","8",R.drawable.card_08,7,this.deckID);
        cardDeck[8] = new Card(context, "Kreuz","9",R.drawable.card_09,8,this.deckID);
        cardDeck[9] = new Card(context, "Kreuz","10",R.drawable.card_10,9,this.deckID);
        cardDeck[10] = new Card(context, "Kreuz","Bube",R.drawable.card_11,10,this.deckID);
        cardDeck[11] = new Card(context, "Kreuz","Dame",R.drawable.card_12,11,this.deckID);
        cardDeck[12] = new Card(context, "Kreuz","König",R.drawable.card_13,12,this.deckID);

        cardDeck[13] = new Card(context, "Karo","Ass",R.drawable.card_14,26,this.deckID);
        cardDeck[14] = new Card(context, "Karo","2",R.drawable.card_15,14,this.deckID);
        cardDeck[15] = new Card(context, "Karo","3",R.drawable.card_16,15,this.deckID);
        cardDeck[16] = new Card(context, "Karo","4",R.drawable.card_17,16,this.deckID);
        cardDeck[17] = new Card(context, "Karo","5",R.drawable.card_18,17,this.deckID);
        cardDeck[18] = new Card(context, "Karo","6",R.drawable.card_19,18,this.deckID);
        cardDeck[19] = new Card(context, "Karo","7",R.drawable.card_20,19,this.deckID);
        cardDeck[20] = new Card(context, "Karo","8",R.drawable.card_21,20,this.deckID);
        cardDeck[21] = new Card(context, "Karo","9",R.drawable.card_22,21,this.deckID);
        cardDeck[22] = new Card(context, "Karo","10",R.drawable.card_23,22,this.deckID);
        cardDeck[23] = new Card(context, "Karo","Bube",R.drawable.card_24,23,this.deckID);
        cardDeck[24] = new Card(context, "Karo","Dame",R.drawable.card_25,24,this.deckID);
        cardDeck[25] = new Card(context, "Karo","König",R.drawable.card_26,25,this.deckID);

        cardDeck[26] = new Card(context, "Herz","Ass",R.drawable.card_27,52,this.deckID);
        cardDeck[27] = new Card(context, "Herz","2",R.drawable.card_28,40,this.deckID);
        cardDeck[28] = new Card(context, "Herz","3",R.drawable.card_29,41,this.deckID);
        cardDeck[29] = new Card(context, "Herz","4",R.drawable.card_30,42,this.deckID);
        cardDeck[30] = new Card(context, "Herz","5",R.drawable.card_31,43,this.deckID);
        cardDeck[31] = new Card(context, "Herz","6",R.drawable.card_32,44,this.deckID);
        cardDeck[32] = new Card(context, "Herz","7",R.drawable.card_33,45,this.deckID);
        cardDeck[33] = new Card(context, "Herz","8",R.drawable.card_34,46,this.deckID);
        cardDeck[34] = new Card(context, "Herz","9",R.drawable.card_35,47,this.deckID);
        cardDeck[35] = new Card(context, "Herz","10",R.drawable.card_36,48,this.deckID);
        cardDeck[36] = new Card(context, "Herz","Bube",R.drawable.card_37,49,this.deckID);
        cardDeck[37] = new Card(context, "Herz","Dame",R.drawable.card_38,50,this.deckID);
        cardDeck[38] = new Card(context, "Herz","König",R.drawable.card_39,51,this.deckID);

        cardDeck[39] = new Card(context, "Pik","Ass",R.drawable.card_40,39,this.deckID);
        cardDeck[40] = new Card(context, "Pik","2",R.drawable.card_41,27,this.deckID);
        cardDeck[41] = new Card(context, "Pik","3",R.drawable.card_42,28,this.deckID);
        cardDeck[42] = new Card(context, "Pik","4",R.drawable.card_43,29,this.deckID);
        cardDeck[43] = new Card(context, "Pik","5",R.drawable.card_44,30,this.deckID);
        cardDeck[44] = new Card(context, "Pik","6",R.drawable.card_45,31,this.deckID);
        cardDeck[45] = new Card(context, "Pik","7",R.drawable.card_46,32,this.deckID);
        cardDeck[46] = new Card(context, "Pik","8",R.drawable.card_47,33,this.deckID);
        cardDeck[47] = new Card(context, "Pik","9",R.drawable.card_48,34,this.deckID);
        cardDeck[48] = new Card(context, "Pik","10",R.drawable.card_49,35,this.deckID);
        cardDeck[49] = new Card(context, "Pik","Bube",R.drawable.card_50,36,this.deckID);
        cardDeck[50] = new Card(context, "Pik","Dame",R.drawable.card_51,37,this.deckID);
        cardDeck[51] = new Card(context, "Pik","König",R.drawable.card_52,38,this.deckID);
    }

    public int getCurrentIndex() {

        return this.currentIndex;
    }

    public void shuffle(int count) {
        int index;
        Card temp;
        Random random = new Random();

        for (int c = count; c>0; c--) {
            for (int i = this.cardDeck.length-1; i>0; i--) {
                index = random.nextInt(i+1);
                temp = this.cardDeck[index];
                this.cardDeck[index] = this.cardDeck[i];
                this.cardDeck[i] = temp;
            }
        }
    }

    public Card drawTopCard() {

            Card topCard = this.cardDeck[this.currentIndex];
            this.currentIndex--;
            return topCard;
    }

    public Card[] getCardDeck() {
        return this.cardDeck;
    }

}
