package com.lunamint.wallet;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

public class Blockchain {

    private String chainId;
    private String nodeName;
    private String node;
    private String lcd;
    private String reserveDenom;
    private String reserveDenomDisplayName;

    private static volatile Blockchain Instance = null;

    public static Blockchain getInstance() {
        Blockchain walletManagerInstance = Instance;
        if (walletManagerInstance == null) {
            synchronized (Blockchain.class) {
                walletManagerInstance = Instance;
                if (walletManagerInstance == null) {
                    Instance = walletManagerInstance = new Blockchain();
                }
            }
        }
        return walletManagerInstance;
    }

    public void init() {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        chainId = "cosmoshub-2";
        nodeName = pref.getString("currentNodeName", "Owdin");
        node = pref.getString("currentNode", "lcd.owdin.network:26657");
        lcd = pref.getString("currentLcd", "https://lcd.owdin.network");
        reserveDenom = pref.getString("currentReserveDenom", "uatom");
        reserveDenomDisplayName = pref.getString("reserveDenomDisplayName", "atom");
    }

    public String getChainId() {
        if (chainId == null) init();
        return chainId;
    }

    public String getNodeName() {
        if (nodeName == null) init();
        return nodeName;
    }

    public String getNode() {
        if (node == null) init();
        return node;
    }

    public String getLcd() {
        if (lcd == null) init();
        return lcd;
    }

    public String getReserveDenom() {
        if (reserveDenom == null) init();
        return reserveDenom;
    }

    public String getReserveDisplayName() {
        if (reserveDenomDisplayName == null) init();
        return reserveDenomDisplayName;
    }

    public void changeNode(String nodeName, String node, String lcd, String reserveDenom, String reserveDenomDisplayName) {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentNodeName", nodeName);
        editor.putString("currentNode", node);
        editor.putString("currentLcd", lcd);
        editor.putString("currentReserveDenom", reserveDenom);
        editor.putString("reserveDenomDisplayName", reserveDenomDisplayName);
        editor.commit();
        init();
    }
}
