package com.aause2s15.gruppe3.cheatingandroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Iterator;


public class TestMatchActivity extends ActionBarActivity implements View.OnClickListener {

    // CARDDECK, PLAYER, SELECTED CARD
    private CardDeck cardDeck;
    private Player player1;
    private View selectedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // HIDE STATUS BAR AND ACTION BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_test_match);

        // INITIALISING CARD DECK
        this.cardDeck = new CardDeck(this);
        this.cardDeck.shuffle(5);

        // INITIALISING PLAYER
        this.player1 = new Player("Player 1");
        for (int i = 0; i <13; i++) {
            this.player1.drawCard(this.cardDeck);
        }

        // DISPLAYING PLAYER CARDS
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.playerCardContainer);
        viewGroup.setClipChildren(false);
        float offset = 0;

        Iterator iterator = this.player1.getPlayerCards().iterator();

        while (iterator.hasNext()) {

            Card currentPlayerCard = (Card) iterator.next();

            currentPlayerCard.getImageView().setClickable(true);
            currentPlayerCard.getImageView().setOnClickListener(this);
            currentPlayerCard.getImageView().setX(offset);

            offset = offset+40;

            viewGroup.addView(currentPlayerCard.getImageView());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // for testing purposes of various methods
    public void drawCards(View view) {

        int topCardIndex = this.cardDeck.getCurrentIndex();

        if (topCardIndex>=0) {
            Card currentCard = this.cardDeck.drawTopCard();

            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.drawnCardContainer);
            viewGroup.setClipChildren(false);

            currentCard.getImageView().setClickable(true);
            currentCard.getImageView().setOnClickListener(this);

            viewGroup.addView(currentCard.getImageView());
        }
        else {
            ImageView displayCardDeck = (ImageView) findViewById(R.id.cardDeckImage);
            displayCardDeck.setImageResource(0);
            Toast.makeText(this, "Keine Karten im Deck übrig", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        if (this.selectedCard !=null) {
            this.selectedCard.setY(this.selectedCard.getY()+40);
            ((ImageView) this.selectedCard).setColorFilter(getResources().getColor(R.color.noHighlightColor));
            v.setY(v.getY()-40);
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedCard = v;
        }
        else {
            v.setY(v.getY()-40);
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedCard = v;
        }
    }
}
