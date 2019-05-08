package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.Blockchain;

public class Node {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lcd")
    @Expose
    private String lcd;
    @SerializedName("node")
    @Expose
    private String node;
    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLcd() {
        return lcd;
    }

    public void setLcd(String lcd) {
        this.lcd = lcd;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean getIsSelected() {
        if (lcd == null) return false;
        if (lcd.equals(Blockchain.getInstance().getLcd())) return true;
        return false;
    }
}
