package com.aause2s15.gruppe3.cheatingandroid.connection.server;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.aause2s15.gruppe3.cheatingandroid.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas on 17.05.2015.
 */
public class ServerConnector implements
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {
    private GoogleApiClient mGoogleApiClient;
    private Activity activity;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI};

    private static final ServerConnector serverConnector = new ServerConnector();

    public static void init(Activity activity, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        if(serverConnector.mGoogleApiClient == null) {
            serverConnector.mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(serverConnector)
                    .addApi(Nearby.CONNECTIONS_API)
                    .build();
        }
        serverConnector.activity = activity;
    }

    public static void connect() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionResult result = serverConnector.mGoogleApiClient.blockingConnect();
                System.out.println(result.getErrorCode());
            }
        });
        t1.start();
    }

    public static void close() {
        if (serverConnector.mGoogleApiClient != null && serverConnector.mGoogleApiClient.isConnected()) {
            serverConnector.mGoogleApiClient.disconnect();
        }

    }

    private static boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) serverConnector.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public static void startAdvertising() throws ServerException {
        if (!isConnectedToNetwork()) {
            throw new NoConnectionException();
        }

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(serverConnector.activity.getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        long NO_TIMEOUT = 0L;

        String name = null;
        final Context context = serverConnector.activity;
        Toast.makeText(context, "advertise devices", Toast.LENGTH_LONG).show();
        Nearby.Connections.startAdvertising(serverConnector.mGoogleApiClient, name, appMetadata, NO_TIMEOUT,
                serverConnector).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    Toast.makeText(context, "device found", Toast.LENGTH_LONG).show();
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                }
            }
        });
    }

    public static void startDiscovery()  throws ServerException {
        if (!isConnectedToNetwork()) {
            throw new NoConnectionException();
        }
        String serviceId = serverConnector.activity.getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        final Context context = serverConnector.activity;
        Nearby.Connections.startDiscovery(serverConnector.mGoogleApiClient, serviceId, DISCOVER_TIMEOUT, serverConnector)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Toast.makeText(context, "device found", Toast.LENGTH_LONG).show();
                        } else {
                            int statusCode = status.getStatus().getStatusCode();
                            // Advertising failed - see statusCode for more details
                        }
                    }
                });
    }

    @Override
    public void onConnectionRequest(String s, String s2, String s3, byte[] bytes) {

    }

    @Override
    public void onEndpointFound(String s, String s2, String s3, String s4) {
        Toast.makeText(activity, "endpoint found", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEndpointLost(String s) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
    public void onDisconnected(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
