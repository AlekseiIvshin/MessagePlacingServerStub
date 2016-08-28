package com.eficksan.messaging.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.eficksan.placingmessages.IPlacingMessages;
import com.eficksan.placingmessages.PlaceMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class StubMessagingService extends Service {
    private final ReentrantLock lock = new ReentrantLock();
    private List<PlaceMessage> mMessages;

    private final IPlacingMessages.Stub mBinder = new IPlacingMessages.Stub() {
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
        public List<PlaceMessage> getNearMessages(double latitude, double longitude) throws RemoteException {
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
    public void onDestroy() {
        mMessages.clear();
        super.onDestroy();
    }
}
