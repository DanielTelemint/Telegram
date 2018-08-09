package com.telemint.ui.model;

public class StakeTabMenu {
    private String title;
    private String var;

    public StakeTabMenu(String title, String var){
        this.title = title;
        this.var = var;
    }

    public String getTitle(){
        return title;
    }

    public String getVar(){
        return var;
    }
}
