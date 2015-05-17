package com.aause2s15.gruppe3.cheatingandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.aause2s15.gruppe3.cheatingandroid.connection.server.ServerConnector;
import com.aause2s15.gruppe3.cheatingandroid.connection.server.ServerException;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;


public class CreateMatchActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks {

    private static final int REQUEST_ENABLE_BT = 10;


    ArrayList<String> listItems=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        final ListView listview = (ListView) findViewById(R.id.listView);

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
            ServerConnector.startAdvertising();
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
