package com.vinbox.vinmax;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vinbox.vinmax.build.api.ApiClient;
import com.vinbox.vinmax.build.api.ApiInterface;
import com.vinbox.vinmax.build.configure.Configuration;

import java.util.HashMap;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseNotification {
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private static final String TAG = "VinmaxFCMNotification";
    public void initialize(Context context, int icon, final Class<? extends Activity> mainActivity, String notificationChannelId){

        Settings.activity = mainActivity;
        Settings.context = context;
        Settings.notificationIcon = icon;
        Settings.notificationChannelId = notificationChannelId;

        try{
            FirebaseMessaging.getInstance();
        }
        catch (IllegalStateException e){
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApiKey(Configuration.FIREBASE_API_KEY)
                    .setApplicationId(Configuration.FIREBASE_APPLICATION_ID)
                    .setProjectId(Configuration.FIREBASE_PROJECT_ID)
                    .setDatabaseUrl(Configuration.FIREBASE_DB_URL);

            FirebaseApp.initializeApp(context, builder.build());
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
