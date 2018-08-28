package com.telemint.ui.model;

import android.support.annotation.NonNull;

public class AccountInfo {
    private String accountName;
    private String address;
    private String balance;
    private String denom;
    private String stakedBalance;
    private String dailyReward;


    public AccountInfo(@NonNull String accountName, @NonNull String address, @NonNull String balance, @NonNull String denom, @NonNull String stakedBalance, @NonNull String dailyReward){
        this.accountName = accountName;
        this.address = address;
        this.balance = balance;
        this.denom = denom;
        this.stakedBalance = stakedBalance;
        this.dailyReward = dailyReward;
    }

    public String getAccountName(){
        return accountName;
    }

    public String getAddress(){
        return address;
    }

    public String getBalance(){
        return balance;
    }

    public String getDenom(){
        return denom;
    }

    public String getStakedBalance(){
        return stakedBalance;
    }

    public String getDailyReward(){
        return dailyReward;
    }
}
