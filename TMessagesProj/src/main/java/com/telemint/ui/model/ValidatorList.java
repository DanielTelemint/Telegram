package com.telemint.ui.model;

public class ValidatorList {
    private String name;
    private String fee;
    private String voting_power;

    public ValidatorList(String name, String fee, String voting_power){
        this.name = name;
        this.fee = fee;
        this.voting_power = voting_power;
    }

    public String getName(){
        return name;
    }

    public String getFee(){
        return fee;
    }

    public String getVotingPower(){
        return voting_power;
    }
}
