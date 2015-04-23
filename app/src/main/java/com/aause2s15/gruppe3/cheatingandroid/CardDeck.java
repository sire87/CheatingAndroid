package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;

import java.util.Random;

/**
 * Created by Simon on 20.04.2015.
 */
public class CardDeck {

    private Card[] cardDeck;
    private int currentIndex;

    public CardDeck(Context context) {

        cardDeck = new Card[52];
        currentIndex = 51;

        cardDeck[0] = new Card(context, "Kreuz","Ass",R.drawable.card_01,13);
        cardDeck[1] = new Card(context, "Kreuz","2",R.drawable.card_02,1);
        cardDeck[2] = new Card(context, "Kreuz","3",R.drawable.card_03,2);
        cardDeck[3] = new Card(context, "Kreuz","4",R.drawable.card_04,3);
        cardDeck[4] = new Card(context, "Kreuz","5",R.drawable.card_05,4);
        cardDeck[5] = new Card(context, "Kreuz","6",R.drawable.card_06,5);
        cardDeck[6] = new Card(context, "Kreuz","7",R.drawable.card_07,6);
        cardDeck[7] = new Card(context, "Kreuz","8",R.drawable.card_08,7);
        cardDeck[8] = new Card(context, "Kreuz","9",R.drawable.card_09,8);
        cardDeck[9] = new Card(context, "Kreuz","10",R.drawable.card_10,9);
        cardDeck[10] = new Card(context, "Kreuz","Bube",R.drawable.card_11,10);
        cardDeck[11] = new Card(context, "Kreuz","Dame",R.drawable.card_12,11);
        cardDeck[12] = new Card(context, "Kreuz","König",R.drawable.card_13,12);

        cardDeck[13] = new Card(context, "Karo","Ass",R.drawable.card_14,26);
        cardDeck[14] = new Card(context, "Karo","2",R.drawable.card_15,14);
        cardDeck[15] = new Card(context, "Karo","3",R.drawable.card_16,15);
        cardDeck[16] = new Card(context, "Karo","4",R.drawable.card_17,16);
        cardDeck[17] = new Card(context, "Karo","5",R.drawable.card_18,17);
        cardDeck[18] = new Card(context, "Karo","6",R.drawable.card_19,18);
        cardDeck[19] = new Card(context, "Karo","7",R.drawable.card_20,19);
        cardDeck[20] = new Card(context, "Karo","8",R.drawable.card_21,20);
        cardDeck[21] = new Card(context, "Karo","9",R.drawable.card_22,21);
        cardDeck[22] = new Card(context, "Karo","10",R.drawable.card_23,22);
        cardDeck[23] = new Card(context, "Karo","Bube",R.drawable.card_24,23);
        cardDeck[24] = new Card(context, "Karo","Dame",R.drawable.card_25,24);
        cardDeck[25] = new Card(context, "Karo","König",R.drawable.card_26,25);

        cardDeck[26] = new Card(context, "Herz","Ass",R.drawable.card_27,52);
        cardDeck[27] = new Card(context, "Herz","2",R.drawable.card_28,40);
        cardDeck[28] = new Card(context, "Herz","3",R.drawable.card_29,41);
        cardDeck[29] = new Card(context, "Herz","4",R.drawable.card_30,42);
        cardDeck[30] = new Card(context, "Herz","5",R.drawable.card_31,43);
        cardDeck[31] = new Card(context, "Herz","6",R.drawable.card_32,44);
        cardDeck[32] = new Card(context, "Herz","7",R.drawable.card_33,45);
        cardDeck[33] = new Card(context, "Herz","8",R.drawable.card_34,46);
        cardDeck[34] = new Card(context, "Herz","9",R.drawable.card_35,47);
        cardDeck[35] = new Card(context, "Herz","10",R.drawable.card_36,48);
        cardDeck[36] = new Card(context, "Herz","Bube",R.drawable.card_37,49);
        cardDeck[37] = new Card(context, "Herz","Dame",R.drawable.card_38,50);
        cardDeck[38] = new Card(context, "Herz","König",R.drawable.card_39,51);

        cardDeck[39] = new Card(context, "Pik","Ass",R.drawable.card_40,39);
        cardDeck[40] = new Card(context, "Pik","2",R.drawable.card_41,27);
        cardDeck[41] = new Card(context, "Pik","3",R.drawable.card_42,28);
        cardDeck[42] = new Card(context, "Pik","4",R.drawable.card_43,29);
        cardDeck[43] = new Card(context, "Pik","5",R.drawable.card_44,30);
        cardDeck[44] = new Card(context, "Pik","6",R.drawable.card_45,31);
        cardDeck[45] = new Card(context, "Pik","7",R.drawable.card_46,32);
        cardDeck[46] = new Card(context, "Pik","8",R.drawable.card_47,33);
        cardDeck[47] = new Card(context, "Pik","9",R.drawable.card_48,34);
        cardDeck[48] = new Card(context, "Pik","10",R.drawable.card_49,35);
        cardDeck[49] = new Card(context, "Pik","Bube",R.drawable.card_50,36);
        cardDeck[50] = new Card(context, "Pik","Dame",R.drawable.card_51,37);
        cardDeck[51] = new Card(context, "Pik","König",R.drawable.card_52,38);
    }

    public Card[] getCardDeck() {

        return this.cardDeck;
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

            Card topCard = this.getCardDeck()[currentIndex];
            this.currentIndex--;
            return topCard;
    }

}
