package com.eficksan.messaging.presentation;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eficksan.messaging.R;
import com.eficksan.messaging.data.StubMessagingService;
import com.eficksan.placingmessages.IPlaceMessageRepository;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ACTION_SHOW_ACTIVITY = 100;
    private boolean mIsMessageServiceConnected = false;
    private IPlaceMessageRepository placeMessageRepository;
    private ServiceConnection mMessagesServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            placeMessageRepository = IPlaceMessageRepository.Stub.asInterface(iBinder);
            mIsMessageServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsMessageServiceConnected = false;
            placeMessageRepository = null;
        }
    };

    /**
     * Creates pending intent for show main screen.
     *
     * @param context some kind of context
     * @return pending intent
     */
    public static PendingIntent showActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivities(context, ACTION_SHOW_ACTIVITY, new Intent[]{intent}, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(StubMessagingService.startService(this));
        findViewById(R.id.save_messages_to_csv).setOnClickListener(this);
        bindService(new Intent(this, StubMessagingService.class),mMessagesServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(StubMessagingService.stopForeground(this));
    }

    @Override
    protected void onStop() {
        startService(StubMessagingService.startForeground(this));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mIsMessageServiceConnected = false;
        unbindService(mMessagesServiceConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_messages_to_csv: {
                if (mIsMessageServiceConnected) {
                    try {
                        placeMessageRepository.saveMessagesToCvs();
                    } catch (RemoteException e) {
                        Toast.makeText(this, R.string.message_service_is_unavaible, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
