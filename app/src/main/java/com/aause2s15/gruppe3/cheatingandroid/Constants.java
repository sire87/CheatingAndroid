package com.aause2s15.gruppe3.cheatingandroid;

/**
 * Created by Simon on 01.06.2015.
 */
public interface Constants {

    // Message types sent from the CheatingAndroidService Handler
    int MESSAGE_DEVICE_NAME = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;

    int HOST = 4;
    int CLIENT = 5;

    int CARD_DECK = 6;
    int PLAYER_MOVE = 7;
    int PLAYER_PICKUP = 8;

    // Key names received from the CheatingAndroidService Handler
    String DEVICE_NAME = "device_name";
}
