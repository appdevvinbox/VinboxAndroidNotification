package com.vinbox.vinmax.build.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by tamil@appoets.com on 30-08-2017.
 */

public interface ApiInterface {


    /*-------------USER--------------------*/

    @POST("fcmwebpush/apppush/subscription")
    Call<String> postSubscription(@QueryMap HashMap<String, String> params);

}
