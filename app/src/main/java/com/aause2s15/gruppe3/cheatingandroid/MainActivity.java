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

    private void startAdvertising() {
        if (!isConnectedToNetwork()) {
            // Implement logic when device is not connected to a network
        }

        // Identify that this device is the host
        mIsHost = true;

        // Advertising with an AppIdentifer lets other devices on the
        // network discover this application and prompt the user to
        // install the application.
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // The advertising timeout is set to run indefinitely
        // Positive values represent timeout in milliseconds
        long NO_TIMEOUT = 0L;

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, NO_TIMEOUT,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    // Advertising failed - see statusCode for more details
                }
            }
        });
    }

    private void startDiscovery() {
        if (!isConnectedToNetwork()) {
            // Implement logic when device is not connected to a network
        }
        String serviceId = getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, DISCOVER_TIMEOUT, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Device is discovering
                        } else {
                            int statusCode = result.getStatus().getStatusCode();
                            // Advertising failed - see statusCode for more details
                        }
                    }
                });
    }

    @Override
    public void onEndpointFound(final String endpointId, String deviceId, String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser.
        // Write your logic to initiate a connection with the device at
        // the endpoint ID
    }

    private void connectTo(String endpointId, final String endpointName) {

        // Send a connection request to a remote endpoint. By passing 'null' for the name,
        // the Nearby Connections API will construct a default name based on device model
        // such as 'LGE Nexus 5'.
        String myName = null;
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName, remoteEndpointId, myPayload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            // Successful connection
                        } else {
                            // Failed connection
                        }
                    }
                }, this);
    }

    @Override
    public void onConnectionRequest(final String remoteEndpointId, String remoteDeviceId,
                                    String remoteEndpointName, byte[] payload) {
        if (mIsHost) {
            byte[] myPayload = null;
            // Automatically accept all requests
            Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, remoteEndpointId,
                    myPayload, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Toast.makeText(mContext, "Connected to " + remoteEndpointName,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed to connect to: " + remoteEndpointName,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Clients should not be advertising and will reject all connection requests.
            Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, remoteEndpointId);
        }
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        // Implement parsing logic to process message
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Test Commit Bernhard
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
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
