package com.telemint.wallet.model;

import org.json.JSONObject;

import java.io.Serializable;

public class CmdResult implements Serializable {

    private int result;
    private String err_msg;
    private JSONObject data;

    public CmdResult(int result, String err_msg, JSONObject data){
        this.result = result;
        this.err_msg = err_msg;
        this.data = data;
    }

    public int getResult(){
        return result;
    }

    public String getErrMsg(){
        return err_msg;
    }

    public JSONObject getData(){
        return data;
    }
}
