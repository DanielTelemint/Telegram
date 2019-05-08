package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Fee {
    @SerializedName("amount")
    @Expose
    private List<Amount> amount = null;
    @SerializedName("gas")
    @Expose
    private String gas;

    public List<Amount> getAmount() {
        return amount;
    }

    public void setAmount(List<Amount> amount) {
        this.amount = amount;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }
}
