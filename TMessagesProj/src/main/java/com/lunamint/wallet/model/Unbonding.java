package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Unbonding {
    @SerializedName("delegator_address")
    @Expose
    private String delegatorAddr;
    @SerializedName("validator_address")
    @Expose
    private String validatorAddr;
    @SerializedName("entries")
    @Expose
    private ArrayList<UnbondingEntries> entries;

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

    public ArrayList<UnbondingEntries> getEntries(){
        return entries;
    }

    public void setEntries(ArrayList<UnbondingEntries> entries) {
        this.entries = entries;
    }
}
