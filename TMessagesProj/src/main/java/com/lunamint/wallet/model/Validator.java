package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lunamint.wallet.utils.BigDecimalUtil;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validator implements Serializable {
    @SerializedName("operator_address")
    @Expose
    private String operatorAddress;
    @SerializedName("consensus_pubkey")
    @Expose
    private String consensusPubkey;
    @SerializedName("jailed")
    @Expose
    private Boolean jailed;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("tokens")
    @Expose
    private String tokens;
    @SerializedName("delegator_shares")
    @Expose
    private String delegatorShares;
    @SerializedName("description")
    @Expose
    private Description description;
    @SerializedName("bond_height")
    @Expose
    private String bondHeight;
    @SerializedName("bond_intra_tx_counter")
    @Expose
    private Integer bondIntraTxCounter;
    @SerializedName("unbonding_height")
    @Expose
    private String unbondingHeight;
    @SerializedName("unbonding_time")
    @Expose
    private String unbondingTime;
    @SerializedName("commission")
    @Expose
    private Commission commission;

    private double delegatedAmount = 0;
    private int rank = 0;
    private String unstakingCompletionTime;

    public String getOperatorAddress() {
        return operatorAddress;
    }

    public void setOperatorAddress(String operatorAddress) {
        this.operatorAddress = operatorAddress;
    }

    public String getConsensusPubkey() {
        return consensusPubkey;
    }

    public void setConsensusPubkey(String consensusPubkey) {
        this.consensusPubkey = consensusPubkey;
    }

    public Boolean getJailed() {
        return jailed;
    }

    public void setJailed(Boolean jailed) {
        this.jailed = jailed;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    //Todo parse type
    public float getDelegatorShares() {
        return Float.parseFloat(delegatorShares);
    }

    public void setDelegatorShares(String delegatorShares) {
        this.delegatorShares = delegatorShares;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getBondHeight() {
        return bondHeight;
    }

    public void setBondHeight(String bondHeight) {
        this.bondHeight = bondHeight;
    }

    public Integer getBondIntraTxCounter() {
        return bondIntraTxCounter;
    }

    public void setBondIntraTxCounter(Integer bondIntraTxCounter) {
        this.bondIntraTxCounter = bondIntraTxCounter;
    }

    public String getUnbondingHeight() {
        return unbondingHeight;
    }

    public void setUnbondingHeight(String unbondingHeight) {
        this.unbondingHeight = unbondingHeight;
    }

    public String getUnbondingTime() {
        return unbondingTime;
    }

    public void setUnbondingTime(String unbondingTime) {
        this.unbondingTime = unbondingTime;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public double getDelegatedAmount() {
        return delegatedAmount;
    }

    public String getDelegatedAmountForDisplay() {
        return BigDecimalUtil.getNumberNano(delegatedAmount + "", "4");
    }

    public void setDelegatedAmount(double delegatedAmount) {
        this.delegatedAmount = delegatedAmount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUnstakingCompletionTime() {
        if (unstakingCompletionTime != null) {
            Date date = null;
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                date = fmt.parse(unstakingCompletionTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (date != null) {
                return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setUnstakingCompletionTime(String unstakingCompletionTime) {
        this.unstakingCompletionTime = unstakingCompletionTime;
    }
}
