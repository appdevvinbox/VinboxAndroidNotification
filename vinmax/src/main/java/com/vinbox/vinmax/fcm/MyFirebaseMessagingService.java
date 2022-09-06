package com.vinbox.vinmax.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vinbox.vinmax.Settings;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
            sendNotification(remoteMessage);
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(RemoteMessage remoteMessage) {
        Bitmap notificationBigPicture;
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, Settings.activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", notification.getTitle());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "1");

        if(notification.getIcon() != null) {
            FutureTarget<Bitmap> futureTarget =
                    Glide.with(this)
                            .asBitmap()
                            .load(notification.getIcon())
                            .submit();
            try {
                notificationBigPicture = futureTarget.get();
                notificationBuilder.setLargeIcon(notificationBigPicture);
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(notificationBigPicture));
            } catch (Exception e) {}
        }

        notificationBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(Settings.notificationIcon)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    Settings.notificationChannelId, "vinmax_notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(Settings.notificationChannelId);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}