package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountStatus {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private AccountStatusValue value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AccountStatusValue getValue() {
        return value;
    }

    public void setValue(AccountStatusValue value) {
        this.value = value;
    }
}
