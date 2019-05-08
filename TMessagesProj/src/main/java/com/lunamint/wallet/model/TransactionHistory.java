package com.lunamint.wallet.model;

public class TransactionHistory {
    private String hash;
    private String type;
    private int block;
    private String from;
    private String to;
    private String amount;
    private String denom;

    public TransactionHistory(String hash, String type, int block, String from, String to, String amount, String denom){
        this.hash = hash;
        this.type = type;
        this.block = block;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.denom = denom;
    }

    public String getHash(){
        return hash;
    }

    public String getType(){
        return type;
    }

    public int getBlock(){
        return block;
    }

    public String getFrom(){
        return from;
    }

    public String getTo(){
        return to;
    }

    public String getAmount(){
        return amount;
    }

    public String getDenom(){
        return denom;
    }
}
