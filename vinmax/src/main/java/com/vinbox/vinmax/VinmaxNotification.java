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
import com.vinbox.vinmax.build.api.ApiClient;
import com.vinbox.vinmax.build.api.ApiInterface;
import com.vinbox.vinmax.build.configure.Configuration;
import com.vinbox.vinmax.build.configure.Setting;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VinmaxNotification {
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private static final String TAG = "com.vinbox.vinmax";

    public VinmaxNotification(Context context, String title, int icon, String channel,
                              final Class<? extends Activity> activity){
        Setting.title = title;
        Setting.activity = activity;
        Setting.context = context;
        Setting.notificationIcon = icon;
        Setting.notificationChannelId = channel;
    }
    public void initialize(){
        if(!NotificationManagerCompat.from(Setting.context).areNotificationsEnabled()){
            new AlertDialog.Builder(Setting.context)
                    .setTitle(Setting.title)
                    .setMessage("Please allow notification permission for "+ Setting.title+", to receive notification from vinmax platform.")
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
                    .setApiKey(Configuration.FIREBASE_API_KEY)
                    .setApplicationId(Configuration.FIREBASE_APPLICATION_ID)
                    .setProjectId(Configuration.FIREBASE_PROJECT_ID)
                    .setDatabaseUrl(Configuration.FIREBASE_DB_URL);

            FirebaseApp.initializeApp(Setting.context, builder.build());
        }

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed.", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    sendRegistrationToServer(token);
                }
            });
    }


    private void sendRegistrationToServer(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", "" + token);
        map.put("platform", "Android");
        Call<String> call = apiInterface.postSubscription(map);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body() == "200"){
                        Log.w(TAG, "sendRegistrationToServer: success");
                    }
                    else{
                        Log.w(TAG, "sendRegistrationToServer: failure");
                    }
                }
                else{
                    Log.w(TAG, "sendRegistrationToServer: failure");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(TAG, "sendRegistrationToServer: failure");
            }
        });
    }
}
