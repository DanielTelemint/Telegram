package com.lunamint.wallet.model.tx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultTx {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private DefaultTxMsg value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DefaultTxMsg getValue() {
        return value;
    }

    public void setValue(DefaultTxMsg value) {
        this.value = value;
    }
}
