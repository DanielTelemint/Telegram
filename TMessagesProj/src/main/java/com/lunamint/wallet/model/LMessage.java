package com.lunamint.wallet.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;

public class LMessage implements Serializable {
    private int version;
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public String getAction() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getString("action");
        } catch (Exception e) {
            return null;
        }
    }

    public String getRequesterId() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getString("requester_t_id");
        } catch (Exception e) {
            return null;
        }
    }

    public String getFrom() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("tx").getString("from");
        } catch (Exception e) {
            return null;
        }
    }

    public String getTo() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("tx").getString("to");
        } catch (Exception e) {
            return null;
        }
    }

    public String getAmount() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("tx").getString("amount");
        } catch (Exception e) {
            return "";
        }
    }

    public String getDenom() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("tx").getString("denom");
        } catch (Exception e) {
            return null;
        }
    }

    public String getMemo() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("tx").getString("memo");
        } catch (Exception e) {
            return null;
        }
    }

    public String getCallbackUrl() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("callback").getString("url");
        } catch (Exception e) {
            return null;
        }
    }

    public String getCallbackEndpoint() {
        try {
            JSONObject jo = new JSONObject(message);
            return jo.getJSONObject("callback").getString("endpoint");
        } catch (Exception e) {
            return "";
        }
    }

    public JsonObject getCallbackBody() {
        JsonObject gsonResult = null;
        try {
            JSONObject jo = new JSONObject(message).getJSONObject("callback").getJSONObject("custom_fields");
            if (jo == null) jo = new JSONObject();
            jo.put("lmi_version", version);
            jo.put("requester_t_id", version);

            Gson gson = new GsonBuilder().create();
            JsonElement element = gson.fromJson(jo.toString(), JsonElement.class);
            gsonResult = element.getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gsonResult;
    }
}
