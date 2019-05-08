package com.lunamint.wallet.model;

import android.support.annotation.NonNull;

public class AccountInfo {
    private String name;
    private String type;
    private String address;
    private String pub_key;

    public AccountInfo(@NonNull String name, @NonNull String type, @NonNull String address, @NonNull String pub_key){
        this.name = name;
        this.type = type;
        this.address = address;
        this.pub_key = pub_key;
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
}
