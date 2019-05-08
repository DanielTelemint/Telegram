package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Log {
    @SerializedName("msg_index")
    @Expose
    private String msgIndex;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("log")
    @Expose
    private String log;

    public String getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(String msgIndex) {
        this.msgIndex = msgIndex;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(String key) {
        this.success = success;
    }
}
