package com.aause2s15.gruppe3.cheatingandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;


public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    // Identify if the device is the host
    private boolean mIsHost = false;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    /** not sure */
    public final static String EXTRA_MESSAGE = "com.aause2s15.gruppe3.cheatingandroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the Nearby Connections API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
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

    /** called when the user clicks the create match button */
    public void createMatch(View view) {
        Intent intent = new Intent(this, CreateMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()){
            startActivity(intent);
        }

        else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinMatch(View view) {
        Intent intent = new Intent(this, JoinMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()){
            startActivity(intent);
        }

        else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }
    public void rules (View view){
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }


}
