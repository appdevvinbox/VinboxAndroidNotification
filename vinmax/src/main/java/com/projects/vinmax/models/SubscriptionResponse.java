package com.projects.vinmax.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 10/13/2017.
 */

public class SubscriptionResponse {

    @SerializedName("device_id")
    @Expose
    private String device_id;

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }
}
