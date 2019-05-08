package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TallyResult implements Serializable {

    @SerializedName("yes")
    @Expose
    private String yes;
    @SerializedName("abstain")
    @Expose
    private String abstain;
    @SerializedName("no")
    @Expose
    private String no;
    @SerializedName("no_with_veto")
    @Expose
    private String noWithVeto;

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getAbstain() {
        return abstain;
    }

    public void setAbstain(String abstain) {
        this.abstain = abstain;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNoWithVeto() {
        return noWithVeto;
    }

    public void setNoWithVeto(String noWithVeto) {
        this.noWithVeto = noWithVeto;
    }
}
