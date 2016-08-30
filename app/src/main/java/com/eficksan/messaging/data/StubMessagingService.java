package com.eficksan.messaging.data;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;

import com.eficksan.messaging.R;
import com.eficksan.messaging.presentation.MainActivity;
import com.eficksan.placingmessages.IPlaceMessageRepository;
import com.eficksan.placingmessages.PlaceMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Stub for messaging service on device.
 */
public class StubMessagingService extends Service {
    private static final String ACTION_STOP_FOREGROUND = "ACTION_STOP_FOREGROUND";
    private static final String ACTION_START_FOREGROUND = "ACTION_START_FOREGROUND";
    private static final int NOTIFICATION_ID = 42;

    private final ReentrantLock lock = new ReentrantLock();
    private List<PlaceMessage> mMessages;

    private final IPlaceMessageRepository.Stub mBinder = new IPlaceMessageRepository.Stub() {
        @Override
        public PlaceMessage addMessage(double latitude, double longitude, String message, String userId) throws RemoteException {
            try {
                lock.lock();
                PlaceMessage placeMessage = new PlaceMessage(String.valueOf(mMessages.size()), latitude, longitude, message, userId, System.currentTimeMillis());
                mMessages.add(placeMessage);
                return placeMessage;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public List<PlaceMessage> getMessagesByUser(String userId) throws RemoteException {
            try {
                return mMessages;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void removeAllMessages() throws RemoteException {
            try {
                mMessages.clear();
            } finally {
                lock.unlock();
            }
        }
    };

    /**
     * Create intent for starting service.
     * @param context context
     * @return starting intent
     */
    public static Intent startService(Context context) {
        return new Intent(context, StubMessagingService.class);
    }

    /**
     * Create intent for starting service foreground.
     * @param context context
     * @return starting intent
     */
    public static Intent startForeground(Context context) {
        Intent intent = new Intent(context, StubMessagingService.class);
        intent.setAction(ACTION_START_FOREGROUND);
        return intent;
    }

    /**
     * Create intent for stopping service foreground.
     * @param context context
     * @return starting intent
     */
    public static Intent stopForeground(Context context) {
        Intent intent = new Intent(context, StubMessagingService.class);
        intent.setAction(ACTION_STOP_FOREGROUND);
        return intent;
    }

    public StubMessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMessages = new ArrayList<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_START_FOREGROUND: {
                    Notification notification = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(getString(R.string.notification_text))
                            .setContentIntent(MainActivity.showActivity(this))
                            .build();
                    startForeground(NOTIFICATION_ID, notification);
                    break;
                }
                case ACTION_STOP_FOREGROUND:
                    stopForeground(true);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mMessages.clear();
        super.onDestroy();
    }
}
