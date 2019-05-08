package com.lunamint.wallet.model.tx.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMsg {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private SendMsgValue value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SendMsgValue getValue() {
        return value;
    }

    public void setValue(SendMsgValue value) {
        this.value = value;
    }
}
