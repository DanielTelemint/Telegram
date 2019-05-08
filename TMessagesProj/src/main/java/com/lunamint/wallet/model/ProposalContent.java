package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProposalContent implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private ProposalDetail proposalDetail;

    public String getType() {
        if (type == null) return "";
        String[] result = type.split("/");
        return result.length >= 2 ? result[1] : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProposalDetail getProposalDetail() {
        return proposalDetail;
    }

    public void setValue(ProposalDetail proposalDetail) {
        this.proposalDetail = proposalDetail;
    }
}
