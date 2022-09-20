package com.vinbox.vinmax;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.vinbox.vinmax.build.api.ApiClient;
import com.vinbox.vinmax.build.api.ApiInterface;
import com.vinbox.vinmax.build.configure.GlobalData;
import com.vinbox.vinmax.build.configure.AppReflection;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Vinmax implements Vinbox {
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private static final String TAG = "com.vinbox.vinmax";

    public Vinmax(Context context, String title, int icon, String channel,
                  final Class<? extends Activity> activity){
        AppReflection.title = title;
        AppReflection.activity = activity;
        AppReflection.context = context;
        AppReflection.notificationIcon = icon;
        AppReflection.notificationChannelId = channel;
    }

    /**
     * Call this method to intilize Vinmax enabled push notification
     */
    public void initialize(){
        if(!NotificationManagerCompat.from(AppReflection.context).areNotificationsEnabled()){
            new AlertDialog.Builder(AppReflection.context)
                    .setTitle(AppReflection.title)
                    .setMessage("Please allow notification permission for "+ AppReflection.title+", to receive notification.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        try{
            FirebaseMessaging.getInstance();
        }
        catch (IllegalStateException e){
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApiKey(GlobalData.FIREBASE_API_KEY)
                    .setApplicationId(GlobalData.FIREBASE_APPLICATION_ID)
                    .setProjectId(GlobalData.FIREBASE_PROJECT_ID)
                    .setDatabaseUrl(GlobalData.FIREBASE_DB_URL);

            FirebaseApp.initializeApp(AppReflection.context, builder.build());
        }

        // FirebaseMessaging.getInstance().getToken()
        //     .addOnCompleteListener(new OnCompleteListener<String>() {
        //         @Override
        //         public void onComplete(@NonNull Task<String> task) {
        //             if (!task.isSuccessful()) {
        //                 Log.w(TAG, "Fetching FCM registration token failed.", task.getException());
        //                 return;
        //             }

        //             //Log.d(TAG, "Vinbox device token: " + task.getResult());
        //             postToken(task.getResult());
        //         }
        //     });
    }

}
