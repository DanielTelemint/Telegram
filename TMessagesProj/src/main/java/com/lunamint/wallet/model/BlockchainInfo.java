package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BlockchainInfo {
    @SerializedName("chain_id")
    @Expose
    private String chain_id;
    @SerializedName("reserve_denom")
    @Expose
    private ReserveDenom reserve_denom;
    @SerializedName("list")
    @Expose
    private ArrayList<Node> list;

    public String getChainId() {
        return chain_id;
    }

    public void setChainId(String chain_id) {
        this.chain_id = chain_id;
    }

    public ReserveDenom getReserveDenom() {
        return reserve_denom;
    }

    public void setReserveDenom(ReserveDenom reserve_denom) {
        this.reserve_denom = reserve_denom;
    }

    public ArrayList<Node> getList() {
        return list;
    }

    public void setList(ArrayList<Node> list) {
        this.list = list;
    }
}
