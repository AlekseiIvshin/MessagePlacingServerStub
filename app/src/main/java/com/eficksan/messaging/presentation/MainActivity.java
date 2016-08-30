package com.eficksan.messaging.presentation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eficksan.messaging.R;
import com.eficksan.messaging.data.StubMessagingService;

public class MainActivity extends AppCompatActivity {
    private static final int ACTION_SHOW_ACTIVITY = 100;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(StubMessagingService.startForeground(this));
    }

    @Override
    protected void onStop() {
        startService(StubMessagingService.startForeground(this));
        super.onStop();
    }
}
