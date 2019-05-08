package com.lunamint.wallet.model.tx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.model.Coin;

public class DefaultMsgValue {
    @SerializedName("delegator_addr")
    @Expose
    private String delegatorAddr;
    @SerializedName("validator_addr")
    @Expose
    private String validatorAddr;
    @SerializedName("value")
    @Expose
    private Coin value;

    public String getDelegatorAddr() {
        return delegatorAddr;
    }

    public void setDelegatorAddr(String delegatorAddr) {
        this.delegatorAddr = delegatorAddr;
    }

    public String getValidatorAddr() {
        return validatorAddr;
    }

    public void setValidatorAddr(String validatorAddr) {
        this.validatorAddr = validatorAddr;
    }

    public Coin getValue() {
        return value;
    }

    public void setValue(Coin delegation) {
        this.value = delegation;
    }
}
