package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    /**
     * not sure
     */
    public final static String EXTRA_MESSAGE = "com.aause2s15.gruppe3.cheatingandroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Test Commit Bernhard
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * called when the user clicks the create match button
     */
    public void createMatch(View view) {
        Intent intent = new Intent(this, CreateMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinMatch(View view) {
        Intent intent = new Intent(this, JoinMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }

    public void rules(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

}
