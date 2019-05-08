package com.lunamint.wallet.model;

public class Setting {

    private boolean enabledFingerprint;
    private String node;

    public boolean isEnabledFingerprint() {
        return enabledFingerprint;
    }

    public void setEnabledFingerprint(boolean enabledFingerprint) {
        this.enabledFingerprint = enabledFingerprint;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
