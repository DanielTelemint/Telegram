package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.utils.BigDecimalUtil;

import java.io.Serializable;

public class Coin implements Serializable {

    @SerializedName("denom")
    @Expose
    private String denom;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getDenom() {
        return denom;
    }

    public String getDenomDisplayName() {
        return denom.equals(Blockchain.getInstance().getReserveDenom()) ? Blockchain.getInstance().getReserveDisplayName() : denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }

    public String getAmount() {
        if (amount == null) {
            return "0";
        } else if (amount.length() == 0) {
            return "";
        } else {
            return BigDecimalUtil.getNumberNano(amount, "4");
        }
    }

    public String getAmountOrigin() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
