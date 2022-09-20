package com.vinbox.vinmax;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vinbox.vinmax.build.configure.AppReflection;
import com.vinbox.vinmax.build.api.ApiClient;
import com.vinbox.vinmax.build.api.ApiInterface;
import com.vinbox.vinmax.build.configure.GlobalData;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "com.vinbox.vinmax";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        postToken(token);
    }

    /**
     * This method received notification data from fcm
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
            sendNotification(remoteMessage);
        }
    }

    /**
     * This method used to send notification
     * @param remoteMessage
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Bitmap notificationBigPicture;
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, AppReflection.activity);
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
                .setSmallIcon(AppReflection.notificationIcon)
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
                    AppReflection.notificationChannelId,
                    AppReflection.title,
                    NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(AppReflection.notificationChannelId);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * This method would send device token to vinmax api
     * @param token
     */
    private void postToken(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", "" + token);
        map.put("platform", "Android");
        Call<String> call = apiInterface.postSubscription(map);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){                    
                    if(response.body() == "200"){
                        Log.w(TAG, "postToken: success");
                    }
                    else{
                        Log.w(TAG, "postToken: failure");
                    }
                }
                else{
                    Log.w(TAG, "postToken: failure");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(TAG, "postToken: failure");
            }
        });
    }    
}