package com.lunamint.wallet.model;

import java.io.Serializable;

public class CmdResult implements Serializable {

    private String err_msg;
    private String data;

    public CmdResult( String err_msg, String data){
        this.err_msg = err_msg;
        this.data = data;
    }

    public String getErrMsg(){
        return err_msg;
    }

    public String getData(){
        return data;
    }

    public void clear(){
        data = null;
    }
}
