package com.lunamint.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Status {
    @SerializedName("is_maintenance")
    @Expose
    private Boolean isMaintenance;
    @SerializedName("completion_time")
    @Expose
    private String completionTime;
    @SerializedName("activated_send")
    @Expose
    private Boolean activatedSend;

    public Boolean getIsMaintenance() {
        return isMaintenance;
    }

    public void setIsMaintenance(Boolean isMaintenance) {
        this.isMaintenance = isMaintenance;
    }

    public String getCompletionTime() {
        if (completionTime != null) {
            Date date = null;
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                date = fmt.parse(completionTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (date != null) {
                return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public Boolean getActivatedSend() {
        return activatedSend;
    }

    public void setActivatedSend(Boolean activatedSend) {
        this.activatedSend = activatedSend;
    }
}
