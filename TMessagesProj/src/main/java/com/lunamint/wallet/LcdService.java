package com.lunamint.wallet;

import com.google.gson.JsonObject;
import com.lunamint.wallet.model.AccountStatus;
import com.lunamint.wallet.model.BlockInfo;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.Proposal;
import com.lunamint.wallet.model.SendResult;
import com.lunamint.wallet.model.StakingInfo;
import com.lunamint.wallet.model.Unbonding;
import com.lunamint.wallet.model.Validator;
import com.lunamint.wallet.model.Voter;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.model.tx.proposal.ProposalHistory;
import com.lunamint.wallet.model.tx.send.SendHistory;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LcdService {

    @GET("blocks/latest")
    Call<BlockInfo> getLatestBlock();

    @GET("bank/balances/{address}")
    Call<ArrayList<Coin>> getBalances(@Path("address") String address);

    @GET("auth/accounts/{address}")
    Call<AccountStatus> getAccountStatus(@Path("address") String address);

    @GET("staking/validators")
    Call<ArrayList<Validator>> getValidators();

    @GET("staking/delegators/{delegatorAddr}/delegations/{validatorAddr}")
    Call<StakingInfo> getStakingInfo(@Path("delegatorAddr") String delegatorAddr, @Path("validatorAddr") String validatorAddr);

    @GET("staking/delegators/{delegatorAddr}/delegations")
    Call<ArrayList<StakingInfo>> getDelegations(@Path("delegatorAddr") String delegatorAddr);

    @GET("staking/delegators/{delegatorAddr}/unbonding_delegations/{validatorAddr}")
    Call<Unbonding> getUnbondingDelegation(@Path("delegatorAddr") String delegatorAddr, @Path("validatorAddr") String validatorAddr);

    @GET("staking/delegators/{delegatorAddr}/unbonding_delegations")
    Call<ArrayList<Unbonding>> getUnbondingDelegations(@Path("delegatorAddr") String delegatorAddr);

    @Headers({"Content-Type: application/json"})
    @POST("txs")
    Call<SendResult> sendTransaction(@Body JsonObject tx);

    @GET("txs")
    Call<ArrayList<SendHistory>> getSendHistory(@Query("sender") String sender);

    @GET("txs")
    Call<ArrayList<SendHistory>> getRecipientHistory(@Query("recipient") String recipient);

    @GET("txs")
    Call<ArrayList<ProposalHistory>> getProposalHistory(@Query("voter") String voter);

    @GET("txs")
    Call<ArrayList<DefaultHistory>> getDelegationHistory(@Query("delegator") String delegator);

    @GET("txs/{hash}")
    Call<DefaultHistory> getTransactionDetail(@Path("hash") String hash);

    @GET("gov/proposals")
    Call<ArrayList<Proposal>> getProposals(@Query("status") String status);

    @GET("gov/proposals/{proposalId}/votes")
    Call<ArrayList<Voter>> getVoters(@Path("proposalId") String proposalId);

    @GET("distribution/delegators/{delegatorAddr}/rewards")
    Call<ArrayList<Coin>> getRewards(@Path("delegatorAddr") String delegatorAddr);

    @Headers({"Content-Type: application/json"})
    @POST("{endpoint}")
    Call<ResponseBody> sendResultToCallback(@Path("endpoint") String endpoint, @Body JsonObject result);

    @GET("syncing")
    Call<Boolean> getNodeStatus();
}
