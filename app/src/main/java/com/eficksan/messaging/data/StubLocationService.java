package com.eficksan.messaging.data;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Service for stubbing calls to location API.
 */
public class StubLocationService extends Service {
    public static final int MSG_RESULT_ERROR = 0;
    public static final int MSG_RESULT_OK = 1;
    public static final String EXTRA_DATA = "EXTRA_DATA";

    public static final int MSG_SAVE_LOCATION = 1;
    public static final int MSG_GET_LAST_LOCATION = 2;
    public static final int MSG_REMOVE_ALL_LOCATIONS = 3;
    private static final String TAG = StubLocationService.class.getSimpleName();

    class IncomingCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_LOCATION: {
                    //TODO: save location to DB
                    Message message = new Message();
                    message.what = MSG_RESULT_OK;
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        Log.v(TAG, e.getMessage(), e);
                    }
                }
                case MSG_GET_LAST_LOCATION: {
                    //TODO:get last location
                    Message message = new Message();
                    message.what = MSG_RESULT_ERROR;
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        Log.v(TAG, e.getMessage(), e);
                    }
                }
                case MSG_REMOVE_ALL_LOCATIONS: {
                    //TODO: remove all locations
                    Message message = new Message();
                    message.what = MSG_RESULT_ERROR;
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        Log.v(TAG, e.getMessage(), e);
                    }
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
