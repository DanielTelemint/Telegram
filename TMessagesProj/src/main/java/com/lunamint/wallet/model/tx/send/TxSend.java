package com.lunamint.wallet.model.tx.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxSend {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private TxSendMsg value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TxSendMsg getValue() {
        return value;
    }

    public void setValue(TxSendMsg value) {
        this.value = value;
    }
}
