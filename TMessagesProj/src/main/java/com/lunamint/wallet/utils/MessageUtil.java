package com.lunamint.wallet.utils;

import com.lunamint.lunagram.BuildVars;
import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.LMessage;

import org.telegram.messenger.LocaleController;

public class MessageUtil {

    public final static int supportVersion = 1;

    public final static boolean isLmiMessage(String msg) {
        int msgVersion = getVersion(msg);
        return isSupport(msgVersion);
    }

    public final static String getLmiMessage(String msg) {
        try {
            String[] splitedMsg = msg.split("::");
            int msgVersion = Integer.parseInt(splitedMsg[1]);
            if (isSupport(msgVersion)) {
                String decryptedMsg = AESUtil.decrypt(msgVersion, splitedMsg[2]);

                if (decryptedMsg == null) decryptedMsg = BuildVars.getLunagramUnsupportedMessage();
                return decryptedMsg;
            } else {
                return BuildVars.getLunagramUnsupportedMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BuildVars.getLunagramUnsupportedMessage();
        }
    }

    private final static boolean isSupport(int version) {
        if (version <= -1) {
            return false;
        } else if (version <= supportVersion) {
            return true;
        } else {
            return false;
        }
    }

    public final static int getVersion(String msg) {
        int msgVersion = -1;
        try {
            if (msg != null && msg.contains("lmi::")) {
                String[] splitedMsg = msg.split("::");
                if (splitedMsg.length >= 3)
                    msgVersion = Integer.parseInt(splitedMsg[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgVersion;
    }

    public final static boolean isUnsupportMessage(String msg) {
        boolean isUnsupportMessage = false;
        for (String umsg : BuildVars.lunagramUnsupportedMessages) {
            if (msg.contains(umsg)) {
                isUnsupportMessage = true;
                break;
            }
        }
        return isUnsupportMessage;
    }

    public final static String getUnsupportedMessage() {
        return BuildVars.getLunagramUnsupportedMessage();
    }

    public final static String getRequestMessage(LMessage lMessage) {
        if (lMessage == null) return "";

        String result = "";

        try{
            switch (lMessage.getVersion()) {
                case 1:
                    if (lMessage.getAction().equals("send")) {
                        result = "[" + LocaleController.getString("sendRequest", R.string.sendRequest) + "]\n\n";
                    } else {
                        result = "[" + LocaleController.getString("unknownRequest", R.string.unknownRequest) + "]\n\n";
                    }

                    result = result + LocaleController.getString("requester", R.string.requester) + " : " + lMessage.getRequesterId() + "\n\n";

                    String from = lMessage.getFrom();
                    String memo = lMessage.getMemo();

                    if (from != null)
                        result = result + LocaleController.getString("from", R.string.from) + " : " + from + "\n\n";
                    result = result + LocaleController.getString("requester", R.string.to) + " : " + lMessage.getTo() + "\n\n";
                    result = result + LocaleController.getString("amount", R.string.amount) + " : " + lMessage.getAmount() + lMessage.getDenom() + "\n\n";
                    if (memo != null)
                        result = result + LocaleController.getString("requester", R.string.memo) + " : " + lMessage.getMemo() + "\n";
                    break;
            }
        }catch (Exception e){
            result = BuildVars.getLunagramUnsupportedMessage();
        }

        return result;
    }
}
