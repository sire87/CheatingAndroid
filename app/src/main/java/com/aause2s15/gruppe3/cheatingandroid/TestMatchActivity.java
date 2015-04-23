package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;

public class TestMatchActivity extends ActionBarActivity implements View.OnClickListener, SensorEventListener {

    // CARDDECK, PLAYER, SELECTED CARD, STACKED CARDS
    private CardDeck cardDeck;
    private Player player1;
    private View selectedCard;
    private ArrayList<Card> stackedCards;
    private boolean cardFlipped;
    private Card currentCard;
    private Card flippedCard;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1600;
    private static final int playerCards = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // HIDE STATUS BAR AND ACTION BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_test_match);

        // INITIALISING ACCELEROMETER
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        // INITIALISING CARD DECK
        this.cardDeck = new CardDeck(this);
        this.cardDeck.shuffle(5);

        // INITIALISING STACK
        this.stackedCards = new ArrayList<>(10);
        this.cardFlipped = false;

        // INITIALISING PLAYER
        this.player1 = new Player("Player 1");
        for (int i = 0; i <this.playerCards; i++) {
            this.player1.drawCard(this.cardDeck);
        }

        // RENDERING PLAYER CARDS
        renderCards();
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

    // draw card from card deck
    public void drawCards(View view) {

        int topCardIndex = this.cardDeck.getCurrentIndex();

        if (topCardIndex>=0) {
            Card currentCard = this.cardDeck.drawTopCard();

            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.drawnCardContainer);
            viewGroup.setClipChildren(false);

            viewGroup.addView(currentCard.getImageView());
            this.currentCard = currentCard;
        }
        else {
            ImageView displayCardDeck = (ImageView) findViewById(R.id.cardDeckImage);
            displayCardDeck.setImageResource(0);
            Toast.makeText(this, "Keine Karten im Deck übrig", Toast.LENGTH_SHORT).show();
        }
    }

    // move card from player to stack
    public void playCard(View view)     {

        if (this.selectedCard != null) {

            // this.selectedCard.setY(this.selectedCard.getY()+40);

            String selectedCardTag = this.selectedCard.getTag().toString();
            // Toast.makeText(this, "Du spielst folgende Karte: "+selectedCardTag, Toast.LENGTH_SHORT).show();

            Iterator iterator = this.player1.getPlayerCards().iterator();

            while (iterator.hasNext()) {

                Card currentPlayerCard = (Card) iterator.next();
                if (currentPlayerCard.getTag().equals(selectedCardTag)) {
                    Card chosenCard = currentPlayerCard;
                    this.player1.playCard(chosenCard);
                    this.stackedCards.add(chosenCard);
                    ViewGroup viewGroup = (ViewGroup) findViewById(R.id.playerCardContainer);
                    viewGroup.removeView(this.selectedCard);
                    this.selectedCard = null;
                    break;
                }
            }

            renderCards();

            int index = this.stackedCards.size() - 1;
            Card testcard = this.stackedCards.get(index);
            ImageView imgStackedCard = testcard.getImageView();
            imgStackedCard.setX(0);
            imgStackedCard.setY(0);
            imgStackedCard.setColorFilter(getResources().getColor(R.color.noHighlightColor));
            imgStackedCard.setClickable(false);
            imgStackedCard.setImageResource(R.drawable.card_56);
            this.selectedCard = null;

            // Toast.makeText(this, "Du hast folgende Karte abgelegt: "+testcard.getTag(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "Stack Size: "+this.stackedCards.size(), Toast.LENGTH_SHORT).show();
            ViewGroup stackedCardsContainer = (ViewGroup) findViewById(R.id.cardStackContainer);
            stackedCardsContainer.addView(imgStackedCard);
            Toast.makeText(this, "Schüttle dein Gerät, wenn du glaubst, dass dieser Spielzug nicht korrekt war.", Toast.LENGTH_SHORT).show();
        }
    }

    // check if flippedCard matches drawn card
    public boolean validCard() {

        if (this.currentCard != null) {

            String valueFlippedCard = this.flippedCard.getValue();
            String valueCurrentCard = this.currentCard.getValue();
            String typeFlippedCard = this.flippedCard.getType();
            String typeCurrentCard = this.currentCard.getType();

            if (valueFlippedCard.equals(valueCurrentCard) || typeFlippedCard.equals(typeCurrentCard)) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    // flip top card of stack
    public void flipCard(View view) {

        if (!cardFlipped && this.stackedCards.size()>0) {

            int index = this.stackedCards.size() - 1;
            Card flippedCard = this.stackedCards.get(index);
            this.flippedCard = flippedCard;
            ImageView imgFlippedCard = flippedCard.getImageView();
            // imgFlippedCard.setX(imgFlippedCard.getX()+20);
            imgFlippedCard.setImageResource(flippedCard.getImage());
            if (validCard()) {
                Toast.makeText(this, "Du hast eine korrekte Karte gespielt!", Toast.LENGTH_SHORT).show();
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("LÜGNER!");
                alertDialog.setMessage("Du hast KEINE korrekte Karte gespielt und musst nun alle Karten aufnehmen!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                pickUpCards(null);
            }
        }
    }

    // move all cards from stack to player
    public void pickUpCards(View view) {

        if (this.stackedCards.size()>0) {

            ViewGroup stackedCardsContainer = (ViewGroup) findViewById(R.id.cardStackContainer);
            stackedCardsContainer.removeAllViews();

            Iterator iterator = this.stackedCards.iterator();

            while (iterator.hasNext()) {
                Card currentStackedCard = (Card) iterator.next();
                this.player1.addCard(currentStackedCard);
                currentStackedCard.getImageView().setImageResource(currentStackedCard.getImage());
                currentStackedCard.getImageView().setY(currentStackedCard.getImageView().getY()+40);
                stackedCardsContainer.removeView(currentStackedCard.getImageView());
                iterator.remove();
                renderCards();
            }
        }
    }

    // render player cards
    public void renderCards() {

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.playerCardContainer);
        viewGroup.setClipChildren(false);
        viewGroup.removeAllViews();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int maxWidth = displayMetrics.widthPixels;

        float offset = 0;
        float moveX = 80;

        if (this.player1.getPlayerCards().size()>0) {
            moveX = (maxWidth-160) / this.player1.getPlayerCards().size();
            if (moveX > 80) {
                moveX = 80;
            }
        }

        Iterator iterator = this.player1.getPlayerCards().iterator();

        while (iterator.hasNext()) {

            Card currentPlayerCard = (Card) iterator.next();

            currentPlayerCard.getImageView().setClickable(true);
            currentPlayerCard.getImageView().setOnClickListener(this);
            currentPlayerCard.getImageView().setX(offset);

            offset = offset + moveX;

            viewGroup.addView(currentPlayerCard.getImageView());
        }
    }

    @Override
    // called when card is clicked
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                    flipCard(null);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
