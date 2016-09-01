package com.eficksan.messaging.data;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.eficksan.messaging.R;
import com.eficksan.messaging.presentation.MainActivity;
import com.eficksan.placingmessages.IPlaceMessageRepository;
import com.eficksan.placingmessages.PlaceMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;

/**
 * Stub for messaging service on device.
 */
public class StubMessagingService extends Service {
    private static final String ACTION_STOP_FOREGROUND = "ACTION_STOP_FOREGROUND";
    private static final String ACTION_START_FOREGROUND = "ACTION_START_FOREGROUND";
    private static final int NOTIFICATION_ID = 42;
    private static final String TAG = StubMessagingService.class.getSimpleName();

    private final ReentrantLock lock = new ReentrantLock();
    private List<PlaceMessage> mMessages;


    private final IPlaceMessageRepository.Stub mBinder = new IPlaceMessageRepository.Stub() {

        @Override
        public PlaceMessage addMessage(double latitude, double longitude, String message, String userId) throws RemoteException {
            try {
                lock.lock();
                PlaceMessage placeMessage = new PlaceMessage();
                placeMessage.id = String.valueOf(mMessages.size());
                placeMessage.latitude = latitude;
                placeMessage.longitude = longitude;
                placeMessage.message = message;
                placeMessage.userId = userId;
                placeMessage.timeStamp = System.currentTimeMillis();
                mMessages.add(placeMessage);
                return placeMessage;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public List<PlaceMessage> getMessagesByUser(String userId) throws RemoteException {
            Log.v(TAG, String.format("Request messages for userId = %s, messages count = %d", userId, mMessages.size()));
            try {
                lock.lock();
                return mMessages;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void removeAllMessages() throws RemoteException {
            try {
                lock.lock();
                mMessages.clear();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Uri saveMessagesToCsv() throws RemoteException {
            try {
                lock.lock();
                ArrayList<PlaceMessage> messageList = new ArrayList<>(mMessages);
                new MessageToCsvTask().execute(messageList);
            } finally {
                lock.unlock();
            }
            return null;
        }
    };

    /**
     * Create intent for starting service.
     *
     * @param context context
     * @return starting intent
     */
    public static Intent startService(Context context) {
        return new Intent(context, StubMessagingService.class);
    }

    /**
     * Create intent for starting service foreground.
     *
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
     *
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

    private class MessageToCsvTask extends AsyncTask<List<PlaceMessage>, Integer, Uri> {

        @Override
        protected Uri doInBackground(List<PlaceMessage>... messages) {
            if (messages.length > 0) {
                return saveMessagesToCsv(messages[0]);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                Toast.makeText(StubMessagingService.this, getString(R.string.messages_saved_in, "Downloads", uri.getLastPathSegment()), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Save message to file using CSV encoding.
         * @param messageList messages list
         * @return return Uri to saved file or null if some error was occerred
         */
        private Uri saveMessagesToCsv(List<PlaceMessage> messageList) {
            if (isExternalStorageWritable()) {
                File documentsRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File messagesFile = new File(documentsRoot, String.format("messages_%d.csv", System.currentTimeMillis()));
                if (messagesFile.exists()) {
                    messagesFile.delete();
                }
                BufferedWriter messagesWriter = null;
                try {
                    messagesWriter = new BufferedWriter(new FileWriter(messagesFile));
                    for (PlaceMessage message : messageList) {
                        if (isCancelled()) {
                            return null;
                        }
                        messagesWriter.write(getMessageAsCsvLine(message));
                        messagesWriter.newLine();
                    }
                    return Uri.parse(messagesFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (messagesWriter != null) {
                        try {
                            messagesWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Toast.makeText(StubMessagingService.this, R.string.storage_is_not_available, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        /**
         * Get Csv representation of message.
         * @param message message
         * @return csv line
         */
        private String getMessageAsCsvLine(PlaceMessage message) {
            return String.format("\"%s\",\"%f\",\"%f\",\"%s\",\"%s\",%d", message.id, message.latitude, message.longitude, message.message, message.userId, message.timeStamp);
        }

        /**
         * Checks on external storage available.
         * @return true is available
         */
        private boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state);
        }
    }

}
