package com.lunamint.wallet.model.tx;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultMsg {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private JsonObject value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getValue() {
        return value;
    }

    public void setValue(JsonObject value) {
        this.value = value;
    }
}
