package com.lunamint.wallet.model.tx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.model.Fee;
import com.lunamint.wallet.model.Signature;

import java.util.List;

public class DefaultTxMsg {
    @SerializedName("msg")
    @Expose
    private List<DefaultMsg> msg = null;
    @SerializedName("fee")
    @Expose
    private Fee fee;
    @SerializedName("signatures")
    @Expose
    private List<Signature> signatures = null;
    @SerializedName("memo")
    @Expose
    private String memo;

    public List<DefaultMsg> getMsg() {
        return msg;
    }

    public void setMsg(List<DefaultMsg> msg) {
        this.msg = msg;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public List<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Signature> signatures) {
        this.signatures = signatures;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
