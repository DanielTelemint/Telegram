package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockHeader {
    @SerializedName("chain_id")
    @Expose
    private String chain_id;
    @SerializedName("height")
    @Expose
    private int height;

    public String getChainId() {
        return chain_id;
    }

    public void setChainId(String chain_id) {
        this.chain_id = chain_id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
