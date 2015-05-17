package com.aause2s15.gruppe3.cheatingandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aause2s15.gruppe3.cheatingandroid.connection.server.ServerConnector;
import com.aause2s15.gruppe3.cheatingandroid.connection.server.ServerException;
import com.google.android.gms.common.api.GoogleApiClient;


public class JoinMatchActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = new TextView(this);
        textView.setTextSize(24);

        textView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        textView.setText("Hallo " + message + ",\n\nleider steht diese Funktion momentan noch nicht zur Verfügung.");
        setContentView(textView);


        ServerConnector.init(this, this);
    }


    @Override
    protected  void onStart() {
        super.onStart();
        ServerConnector.connect();
    }

    @Override
    protected  void onStop() {
        ServerConnector.close();
        super.onStop();
    }

    public void startTestMatch(View view) {
        Intent intent = new Intent(this, TestMatchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "jeeaaah", Toast.LENGTH_LONG).show();
        try {
            ServerConnector.startDiscovery();
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
