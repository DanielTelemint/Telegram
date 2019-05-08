package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class UnbondingEntries {
    @SerializedName("creation_height")
    @Expose
    private String creationHeight;
    @SerializedName("completion_time")
    @Expose
    private String completionTime;
    @SerializedName("initial_balance")
    @Expose
    private String initialBalance;
    @SerializedName("balance")
    @Expose
    private String balance;

    public String getCreationHeight() {
        return creationHeight;
    }

    public void setCreationHeight(String creationHeight) {
        this.creationHeight = creationHeight;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public String getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(String initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getBalance() {
        return new BigDecimal(balance);
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
