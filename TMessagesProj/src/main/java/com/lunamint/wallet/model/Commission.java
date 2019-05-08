package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Commission implements Serializable {
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("max_rate")
    @Expose
    private String maxRate;
    @SerializedName("max_change_rate")
    @Expose
    private String maxChangeRate;
    @SerializedName("update_time")
    @Expose
    private String updateTime;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(String maxRate) {
        this.maxRate = maxRate;
    }

    public String getMaxChangeRate() {
        return maxChangeRate;
    }

    public void setMaxChangeRate(String maxChangeRate) {
        this.maxChangeRate = maxChangeRate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
