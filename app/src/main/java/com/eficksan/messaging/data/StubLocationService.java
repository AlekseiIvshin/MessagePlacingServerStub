package com.eficksan.messaging.data;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

    private static final String TAG = StubLocationService.class.getSimpleName();

    private Location mLastLocation;

    class IncomingCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_LOCATION: {
                    Message message = new Message();
                    message.what = msg.what;
                    mLastLocation = msg.getData().getParcelable(EXTRA_DATA);
                    if (mLastLocation != null) {
                        message.arg1 = MSG_RESULT_OK;
                        Log.v(TAG, "Location saved: " + mLastLocation.toString());
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            Log.v(TAG, e.getMessage(), e);
                        }
                    } else {
                        message.arg1 = MSG_RESULT_ERROR;
                        Log.v(TAG, "Location was not received");
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            Log.v(TAG, e.getMessage(), e);
                        }
                    }
                }
                case MSG_GET_LAST_LOCATION: {
                    Message message = new Message();
                    message.what = msg.what;
                    if (mLastLocation != null) {
                        message.arg1 = MSG_RESULT_OK;
                        Bundle data = new Bundle();
                        data.putParcelable(EXTRA_DATA, mLastLocation);
                        message.setData(data);
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            Log.v(TAG, e.getMessage(), e);
                        }
                    } else {
                        message.arg1 = MSG_RESULT_ERROR;
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            Log.v(TAG, e.getMessage(), e);
                        }
                    }
                }
                default:
                    super.handleMessage(msg);
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
