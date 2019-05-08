package com.lunamint.wallet.utils;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.CmdResult;

import org.telegram.messenger.LocaleController;

public class CmdResultChecker {

    public static final String checkCmdResult(CmdResult cmdResult){

        String msg = null;

        if(cmdResult == null){
            msg = LocaleController.getString("notBeenInitWalletManagerError", R.string.notBeenInitWalletManagerError);
        } else if (cmdResult.getErrMsg() != null){
            if(cmdResult.getErrMsg().contains("invalid account password")) {
                msg = LocaleController.getString("incorrectPassword", R.string.incorrectPassword);
            } else {
                msg = cmdResult.getErrMsg();
            }
        } else if (cmdResult.getData() == null || cmdResult.getData().equals("")){
            msg = LocaleController.getString("walletmanagerNotResponding", R.string.walletmanagerNotResponding);
        }

        return msg;

    }
}
