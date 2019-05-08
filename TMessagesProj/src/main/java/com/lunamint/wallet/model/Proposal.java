package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Proposal implements Serializable {
    @SerializedName("proposal_content")
    @Expose
    private ProposalContent proposalContent;
    @SerializedName("proposal_id")
    @Expose
    private String proposalId;
    @SerializedName("proposal_status")
    @Expose
    private String proposalStatus;
    @SerializedName("final_tally_result")
    @Expose
    private TallyResult finalTallyResult;
    @SerializedName("submit_time")
    @Expose
    private String submitTime;
    @SerializedName("deposit_end_time")
    @Expose
    private String depositEndTime;
    @SerializedName("total_deposit")
    @Expose
    private List<Coin> totalDeposit = null;
    @SerializedName("voting_start_time")
    @Expose
    private String votingStartTime;
    @SerializedName("voting_end_time")
    @Expose
    private String votingEndTime;

    public ProposalContent getProposalContent() {
        return proposalContent;
    }

    public void setProposalContent(ProposalContent proposalContent) {
        this.proposalContent = proposalContent;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getProposalStatus() {
        return proposalStatus;
    }

    public void setProposalStatus(String proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    public TallyResult getFinalTallyResult() {
        return finalTallyResult;
    }

    public void setFinalTallyResult(TallyResult finalTallyResult) {
        this.finalTallyResult = finalTallyResult;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getDepositEndTime() {
        return depositEndTime;
    }

    public void setDepositEndTime(String depositEndTime) {
        this.depositEndTime = depositEndTime;
    }

    public List<Coin> getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(List<Coin> totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public void setVotingStartTime(String votingStartTime) {
        this.votingStartTime = votingStartTime;
    }

    public String getVotingEndTime() {
        Date date = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            date = fmt.parse(votingEndTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(date != null){
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
        }else{
            return votingEndTime;
        }
    }

    public long getVotingEndTimeWithLong() {

        Date date = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            date = fmt.parse(votingEndTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date == null ? 0 : date.getTime();
    }

    public void setVotingEndTime(String votingEndTime) {
        this.votingEndTime = votingEndTime;
    }
}
