package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class JoinMatchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_join_match);

        // get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(24);

        textView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        textView.setText("Hallo " + message + ",\n\nleider steht diese Funktion momentan noch nicht zur Verfügung.");

        // Set the text view as the activity layout
        setContentView(textView);

        // code below does not work as expected
        // ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(200,200);
        // addContentView(textView, params);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_match, menu);
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
}
