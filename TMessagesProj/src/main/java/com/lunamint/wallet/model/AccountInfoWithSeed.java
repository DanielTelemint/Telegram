package com.lunamint.wallet.model;

import android.support.annotation.NonNull;

public class AccountInfoWithSeed {
    private String name;
    private String type;
    private String address;
    private String pub_key;
    private String seed;

    public AccountInfoWithSeed(@NonNull String name, @NonNull String type, @NonNull String address, @NonNull String pub_key, @NonNull String seed){
        this.name = name;
        this.type = type;
        this.address = address;
        this.pub_key = pub_key;
        this.seed = seed;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public String getAddress(){
        return address;
    }

    public String getPublicKey(){
        return pub_key;
    }

    public String getSeed(){
        return seed;
    }

    public void clear(){
        seed = "";
    }
}
