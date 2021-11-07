package com.example.parentapp.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.parentapp.MainMenuActivity;
import com.example.parentapp.R;
import com.example.parentapp.Timer;

//the code below was adapted from the Youtube video linked:
// https://www.youtube.com/watch?v=ub4_f6ksxL0
public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createChanel();
    }

    public void createChanel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.design_default_color_primary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String msg) {
        Intent resultIntent = Timer.makeLaunchIntent(NotificationHelper.this);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        startActivity(resultIntent);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }
}
