package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

public class SendResult {
    @SerializedName("txhash")
    @Expose
    private String hash;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("logs")
    @Expose
    private ArrayList<Log> logs;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean getResult() {
        boolean result = false;
        try {
            if (logs.size() > 0) {
                result = logs.get(0).getSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
