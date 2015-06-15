package com.aause2s15.gruppe3.cheatingandroid;

/**
 * Constants Interface
 *
 * @author Simon Reisinger
 * @version 1.0
 * @since 2015-06-01
 */
public interface Constants {

    // Message types sent from the CheatingAndroidService Handler
    int MESSAGE_CONNECTION_LOST = 0;
    int MESSAGE_DEVICE_NAME = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;

    int HOST = 4;
    int CLIENT = 5;

    int CARD_DECK = 6;
    int PLAYER_MOVE = 7;
    int PLAYER_PICKUP = 8;
    int PLAYER_WON = 9;

    // Key names received from the CheatingAndroidService Handler
    String DEVICE_NAME = "device_name";
    String CONNECTION_LOST = "connection_lost";
}
