package com.lunamint.wallet;

import com.lunamint.wallet.model.BlockchainInfo;
import com.lunamint.wallet.model.LunaStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LunaService {
    @GET("app/status")
    Call<LunaStatus> getStatus(@Query("os") String os);
    @GET("blockchain/info")
    Call<ArrayList<BlockchainInfo>> getBlockchainInfo();
}
