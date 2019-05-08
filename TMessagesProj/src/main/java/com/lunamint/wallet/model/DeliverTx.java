package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeliverTx {
    @SerializedName("log")
    @Expose
    private String log;
    @SerializedName("gasWanted")
    @Expose
    private String gasWanted;
    @SerializedName("gasUsed")
    @Expose
    private String gasUsed;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getGasWanted() {
        return gasWanted;
    }

    public void setGasWanted(String gasWanted) {
        this.gasWanted = gasWanted;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
