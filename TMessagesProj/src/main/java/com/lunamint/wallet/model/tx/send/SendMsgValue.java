package com.lunamint.wallet.model.tx.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.model.Coin;

import java.util.ArrayList;

public class SendMsgValue {
    @SerializedName("from_address")
    @Expose
    private String from_address;
    @SerializedName("to_address")
    @Expose
    private String to_address;
    @SerializedName("amount")
    @Expose
    private ArrayList<Coin> coins = null;

    public String getFromAddress() {
        return from_address;
    }

    public void setFromAddress(String from_address) {
        this.from_address = from_address;
    }

    public String getToAddress() {
        return to_address;
    }

    public void setToAddress(String to_address) {
        this.to_address = to_address;
    }

    public Coin getCoin() {
        return coins.get(0);
    }

    public void setCoin(Coin coin) {
        if (this.coins != null)
            this.coins.add(coin);
    }

    public void setCoins(ArrayList<Coin> coins) {
        this.coins = coins;
    }
}
