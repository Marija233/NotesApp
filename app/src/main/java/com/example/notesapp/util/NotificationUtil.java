package com.example.notesapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.notesapp.ui.MainActivity;
import com.example.notesapp.R;

public final class NotificationUtil {

    private NotificationUtil() {
    }

    private static final String CHANNEL_ID = "notes_channel_id";
    private static final String CHANNEL_NAME = "Notes";

    public static void sendNotification(Context context, int noteId, String notificationDescription) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification(context, notificationDescription);
        manager.notify(noteId, notification);
    }

    private static Notification getNotification(Context context, String description) {
        createNotificationChanel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher_background));
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Notes Reminders");
        builder.setContentText(description);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }

    private static void createNotificationChanel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
