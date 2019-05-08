package com.lunamint.wallet.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.AccountInfoWithSeed;
import com.lunamint.wallet.model.TransactionHistory;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.model.tx.proposal.ProposalHistory;
import com.lunamint.wallet.model.tx.send.SendHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class Parser {

    public static final String TAG = "Parser";

    public static final AccountInfoWithSeed getAccountInfoWithSeed(String data, boolean hasSeed) {
        AccountInfoWithSeed result = null;

        try {
            JSONObject jsonObject = new JSONObject(data);
            String name = jsonObject.getString("name");
            String type = jsonObject.getString("type");
            String address = jsonObject.getString("address");
            String pubkey = jsonObject.getString("pubkey");
            String seed = "";
            if (hasSeed) seed = jsonObject.getString("mnemonic");

            result = new AccountInfoWithSeed(name, type, address, pubkey, seed);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(Parser.TAG, "Result can not be parsed : " + data);
        }

        return result;
    }

    public static final ArrayList<AccountInfo> getAccountList(String data) {
        ArrayList<AccountInfo> result = new ArrayList<>();

        if (data != null) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    String name = jsonObject.getString("name");
                    String type = jsonObject.getString("type");
                    String address = jsonObject.getString("address");
                    String pub_key = jsonObject.getString("pubkey");

                    result.add(new AccountInfo(name, type, address, pub_key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(Parser.TAG, "Result can not be parsed : " + data);
            }
        }
        return result;
    }

    public static final JsonObject getRawTransaction(String source) {
        try {
            JSONObject rs = new JSONObject(source);
            JSONObject tx = new JSONObject();
            tx.put("tx", rs.getJSONObject("value"));
            tx.put("mode", "sync");

            Gson gson = new GsonBuilder().create();
            JsonElement element = gson.fromJson(tx.toString(), JsonElement.class);

            return element.getAsJsonObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    // TODO: need to handling multiple msg
    public static final TransactionHistory getTransactionHistory(SendHistory data) {

        if (data == null) return null;

        try {
            return new TransactionHistory(
                    data.getHash(),
                    data.getTx().getValue().getMsg().get(0).getType(),
                    Integer.parseInt(data.getHeight()),
                    data.getTx().getValue().getMsg().get(0).getValue().getFromAddress(),
                    data.getTx().getValue().getMsg().get(0).getValue().getToAddress(),
                    data.getTx().getValue().getMsg().get(0).getValue().getCoin().getAmount(),
                    data.getTx().getValue().getMsg().get(0).getValue().getCoin().getDenom());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // TODO: need to handling multiple msg
    public static final TransactionHistory getTransactionHistory(DefaultHistory data) {

        if (data == null) return null;

        try {
            switch (data.getTx().getValue().getMsg().get(0).getType()) {
                case "cosmos-sdk/MsgDelegate":
                    return new TransactionHistory(
                            data.getHash(),
                            data.getTx().getValue().getMsg().get(0).getType(),
                            Integer.parseInt(data.getHeight()),
                            data.getTx().getValue().getMsg().get(0).getValue().get("delegator_address").getAsString(),
                            data.getTx().getValue().getMsg().get(0).getValue().get("validator_address").getAsString(),
                            BigDecimalUtil.getNumberNano(data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4"),
                            data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString());
                case "cosmos-sdk/MsgUndelegate":
                    return new TransactionHistory(
                            data.getHash(),
                            data.getTx().getValue().getMsg().get(0).getType(),
                            Integer.parseInt(data.getHeight()),
                            data.getTx().getValue().getMsg().get(0).getValue().get("delegator_address").getAsString(),
                            data.getTx().getValue().getMsg().get(0).getValue().get("validator_address").getAsString(),
                            BigDecimalUtil.getNumberNano(data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4"),
                            data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString());
                case "cosmos-sdk/MsgBeginRedelegate":
                    return new TransactionHistory(
                            data.getHash(),
                            data.getTx().getValue().getMsg().get(0).getType(),
                            Integer.parseInt(data.getHeight()),
                            data.getTx().getValue().getMsg().get(0).getValue().get("delegator_address").getAsString(),
                            data.getTx().getValue().getMsg().get(0).getValue().get("validator_dst_address").getAsString(),
                            BigDecimalUtil.getNumberNano(data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4"),
                            data.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString());
                case "cosmos-sdk/MsgWithdrawDelegationReward":
                    return new TransactionHistory(
                            data.getHash(),
                            data.getTx().getValue().getMsg().get(0).getType(),
                            Integer.parseInt(data.getHeight()),
                            data.getTx().getValue().getMsg().get(0).getValue().get("delegator_address").getAsString(),
                            data.getTx().getValue().getMsg().get(0).getValue().get("validator_address").getAsString(),
                            null,
                            null);
            }

            return null;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: need to handling multiple msg
    public static final TransactionHistory getTransactionHistory(ProposalHistory data) {

        if (data == null) return null;

        try {
            return new TransactionHistory(
                    data.getHash(),
                    data.getTx().getValue().getMsg().get(0).getType(),
                    Integer.parseInt(data.getHeight()),
                    data.getTx().getValue().getMsg().get(0).getValue().getVoter(),
                    data.getTx().getValue().getMsg().get(0).getValue().getProposalId(),
                    data.getTx().getValue().getMsg().get(0).getValue().getOption(),
                    "");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String getTxHashFromSend(String data) {
        String result = "";

        try {
            result = data.substring(data.lastIndexOf("tx hash: ") + 9, data.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Todo: Need to verify it properly about Hash.
    @NonNull
    public static final String getTxHashFromTelegramMsg(String data) {
        String result = "";

        if (data == null) return result;

        try {
            if(data.contains("Tx Hash")){
                result = data.substring(data.lastIndexOf("Tx Hash : ") + 10, data.lastIndexOf("Tx Hash : ") + 10 + 64);
            }else{
                result = data.substring(data.lastIndexOf("트랜잭션 해시 : ") + 10, data.lastIndexOf("트랜잭션 해시 : ") + 10 + 64);
            }


        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static final String getShotAddressForDisplay(String data) {
        String result = "";

        try {
            int length = data.length();
            if (data.contains("cosmosvaloper")) {
                result = data.substring(0, 17) + "..." + data.substring(length - 5, length);
            } else {
                result = data.substring(0, 9) + "..." + data.substring(length - 5, length);
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static final Coin getCoinFromName(Response<ArrayList<Coin>> response, String tokenName) {
        if (response != null && response.code() == 200) {
            ArrayList<Coin> coins = response.body();
            if (coins != null && coins.size() > 0) {
                for (int i = 0; coins.size() > i; i++) {
                    if (coins.get(i).getDenom().equals(tokenName)) return (coins.get(i));
                }
            } else {
                return getCoinForEmpty(tokenName);
            }
        }

        return getCoinForEmpty(tokenName);
    }

    private static final Coin getCoinForEmpty(String tokenName) {
        Coin coin = new Coin();
        coin.setDenom(tokenName);
        coin.setAmount("0");
        return coin;
    }

}
