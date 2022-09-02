package com.projects.vinmax;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.projects.vinmax.build.api.ApiClient;
import com.projects.vinmax.build.api.ApiInterface;
import com.projects.vinmax.build.configure.Configuration;
import com.projects.vinmax.models.SubscriptionResponse;

import java.util.HashMap;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseNotification {
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private static final String TAG = "VinmaxFirebaseNotification";
    public void initialize(Context context, int icon, final Class<? extends Activity> mainActivity, String notificationChannelId){

        FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                .setApplicationId(Configuration.FIREBASE_APPLICATION_ID)
                .setApiKey(Configuration.FIREBASE_API_KEY)
                .setDatabaseUrl(Configuration.FIREBASE_DB_URL);
                //.setStorageBucket(Configuration.STORAGE_BUCKET);
        FirebaseApp.initializeApp(context, builder.build());

        Log.d(TAG, "Initialize Called: ");

        Settings.notificationIcon = icon;
        Settings.mainActivity = mainActivity;
        Settings.context = context;
        Settings.notificationChannelId = notificationChannelId;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        //sendRegistrationToServer(token);
                    }
                });
    }


    private void sendRegistrationToServer(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", "" + token);
        map.put("platformType", "android");
        Call<SubscriptionResponse> call = apiInterface.postSubscription(map);

        call.enqueue(new Callback<SubscriptionResponse>() {
            @Override
            public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                if(response.isSuccessful()){}
                else{}
            }

            @Override
            public void onFailure(Call<SubscriptionResponse> call, Throwable t) {}
        });
    }
}
