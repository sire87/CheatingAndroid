package com.aause2s15.gruppe3.cheatingandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * CheatingAndroidService Class
 *
 * @author Simon Reisinger
 * @version 1.0
 * @since 2015-05-18
 */
public class CheatingAndroidService {

    // TODO: prevent devices from connecting multiple times!

    // CA specific
    private static final CheatingAndroidService INSTANCE = new CheatingAndroidService();
    private String lastConnectedDevice = "keine Verbindung";
    private String playerData = "";

    // Name for the SDP record when creating server socket
    private static final String NAME = "CheatingAndroidBluetooth";

    // Member fields
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private ArrayList<String> mDeviceAddresses = new ArrayList<String>();
    private ArrayList<ConnectedThread> mConnectedThreads = new ArrayList<ConnectedThread>();
    private ArrayList<BluetoothSocket> mSockets= new ArrayList<BluetoothSocket>();

    /**
     * This bluetooth piconet can support up to 3 connections. This array holds 3 unique UUIDs.
     * When attempting to make a connection, the UUID on the client must match one that the server
     * is listening for. When accepting incoming connections server listens for all 3 UUIDs.
     * When trying to form an outgoing connection, the client tries each UUID one at a time.
     */
    private static final UUID[] mUUIDs = {
            UUID.fromString("a0ae0db8-fb75-416d-a436-83ef5415c537"),
            UUID.fromString("b992a855-b913-4a22-80db-b6698bff46f0"),
            UUID.fromString("5df33d47-986c-4a35-a349-3878e2d474cd")};

    /**
     * Constructs the CheatingAndroidService object.
     */
    private CheatingAndroidService() {}

    /**
     * Returns the instance of the CheatingAndroidService.
     *
     * @return the instance of the CheatingAndroidService
     */
    public static CheatingAndroidService getInstance() {
        return INSTANCE;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Create a new thread and attempt to connect to each UUID one-by-one.
        for (int i = 0; i < 3; i++) {
            try {
                mConnectThread = new ConnectThread(device, mUUIDs[i]);
                mConnectThread.start();
            } catch (Exception e) {}
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Add each connected thread to an array
        mConnectedThreads.add(mConnectedThread);

        // Send the name of the connected device back to the UI Activity
        this.lastConnectedDevice = device.getName() + "\n" + device.getAddress();
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, lastConnectedDevice);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        resetPlayerData();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {

        // When writing, try to write out to all connected threads
        for (int i = 0; i < mConnectedThreads.size(); i++) {
            try {
                // Create temporary object
                ConnectedThread r;
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    r = mConnectedThreads.get(i);
                }
                // Perform the write unsynchronized
                r.write(out);
            } catch (Exception e) {}
        }
    }

    /**
     * Indicate that connection was lost and notify the UI Activity
     */
    public void connectionLost(){
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_CONNECTION_LOST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CONNECTION_LOST, "Ein Verbindungsfehler trat auf. Das Spiel muss leider beendet werden.");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        resetPlayerData();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread() {
/*            BluetoothServerSocket tmp1 = null;
            try {
                tmp1 = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, mUUIDs[3]);

            } catch (IOException e) {}
            mmServerSocket = tmp1;*/
        }

        public void run() {

            setName("AcceptThread");

            BluetoothSocket socket = null;
            try {
                // Listen for all 3 UUIDs
                for (int i = 0; i < 3; i++) {
                    mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, mUUIDs[i]);
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        String address = socket.getRemoteDevice().getAddress();
                        mSockets.add(socket);
                        mDeviceAddresses.add(address);
                        connected(socket, socket.getRemoteDevice());
                    }
                }

            } catch (Exception e) {}
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UUID tempUUID;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            tempUUID = uuid;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(tempUUID);
            } catch (IOException e) {}
            mmSocket = tmp;
        }

        public void run() {

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {

                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {}

                // Start the service over to restart listening mode
                CheatingAndroidService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (CheatingAndroidService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }

    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[8192];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    connectionLost();
                    break;}
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * Sets a specified handler.
     * @param mHandler the handler to be set.
     */
    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * Returns the last connected device
     *
     * @return a string with the last connected device
     */
    public String getLastConnectedDevice() {
        return lastConnectedDevice;
    }

    /**
     * Adds player data (player name and MAC address)
     *
     * @param playerData a string containing name and MAC address of a player
     */
    public void addPlayerData(String playerData){
        this.playerData= this.playerData+playerData;
    }

    /**
     * Returns data from all players (player names and MAC addresses).
     *
     * @return a string containing all player names and MAC addresses
     */
    public String getPlayerData() {
        return this.playerData;
    }

    public void resetPlayerData() {
        this.playerData = "";
    }

    /**
     * Returns the own MAC address.
     *
     * @return a string containig the own MAC address
     */
    public String getPlayerAddress() {
        return mBluetoothAdapter.getAddress();
    }

}
