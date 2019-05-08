package com.lunamint.wallet.model.tx.proposal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.model.Voter;

public class ProposalMsg {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private Voter value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Voter getValue() {
        return value;
    }

    public void setValue(Voter value) {
        this.value = value;
    }
}
