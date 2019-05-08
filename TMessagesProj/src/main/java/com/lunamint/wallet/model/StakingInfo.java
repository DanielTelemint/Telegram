package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StakingInfo {
    @SerializedName("delegator_address")
    @Expose
    private String delegatorAddr;
    @SerializedName("validator_address")
    @Expose
    private String validatorAddr;
    @SerializedName("shares")
    @Expose
    private String shares;

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

    public double getShares() {
        return shares != null ? Double.parseDouble(shares) : 0;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }
}
