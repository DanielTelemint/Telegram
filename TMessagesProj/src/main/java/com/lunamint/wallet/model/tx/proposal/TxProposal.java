package com.lunamint.wallet.model.tx.proposal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxProposal {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private TxProposalMsg value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TxProposalMsg getValue() {
        return value;
    }

    public void setValue(TxProposalMsg value) {
        this.value = value;
    }
}
