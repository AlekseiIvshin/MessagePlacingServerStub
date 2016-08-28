package com.eficksan.messagingapi.data;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * Service for stubbing calls to location API.
 */
public class StubLocationService extends Service {
    public static final int MSG_SAVE_LOCATION = 1;
    public static final int MSG_GET_LAST_LOCATION = 2;
    public static final int MSG_REMOVE_ALL_LOCATIONS = 3;

    class IncomingCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_LOCATION: {
                    //TODO: save location to DB
                }
                case MSG_GET_LAST_LOCATION: {
                    //TODO:get last location
                }
                case MSG_REMOVE_ALL_LOCATIONS: {
                    //TODO: remove all locations
                }
                default: super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingCommandHandler());

    public StubLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
