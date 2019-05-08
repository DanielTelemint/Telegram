package com.lunamint.wallet.model.tx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.model.Log;

import org.json.JSONArray;

import java.util.ArrayList;

public class DefaultHistory {
    @SerializedName("txhash")
    @Expose
    private String hash;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("tx")
    @Expose
    private DefaultTx tx;
    @SerializedName("gas_wanted")
    @Expose
    private String gas_wanted;
    @SerializedName("gas_used")
    @Expose
    private String gas_used;
    @SerializedName("logs")
    @Expose
    private ArrayList<Log> logs;
    @SerializedName("error")
    @Expose
    private String error;

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

    public DefaultTx getTx() {
        return tx;
    }

    public void setTx(DefaultTx tx) {
        this.tx = tx;
    }

    public String getGasWanted(){
        return gas_wanted;
    }

    public void setGasWanted(String gas_wanted){
        this.gas_wanted = gas_wanted;
    }

    public String getGasUsed(){
        return gas_used;
    }

    public void setGasUsed(String gas_used){
        this.gas_used = gas_used;
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

    public String getError(){
        return error;
    }

    public void setError(String error){
        this.error = error;
    }
}
