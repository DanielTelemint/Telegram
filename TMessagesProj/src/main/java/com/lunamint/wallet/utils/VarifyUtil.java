package com.lunamint.wallet.utils;

import android.content.Context;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.StakeCoinActivity;

import org.telegram.messenger.LocaleController;

public class VarifyUtil {

    public static final boolean isValidValidator(Context context, int type, String validatorAddress, String redelegateValidatorAddress){
        String err_msg;

        err_msg = varifyValidatorAddress(context, validatorAddress);
        if(err_msg != null) {
            Toast.makeText(context, err_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        if(type == StakeCoinActivity.TYPE_REDELEGATE){
            err_msg = varifyValidatorAddress(context, redelegateValidatorAddress);
            if(err_msg != null) {
                Toast.makeText(context, err_msg, Toast.LENGTH_LONG).show();
                return false;
            }

            if(redelegateValidatorAddress != null){
                if(validatorAddress.equals(redelegateValidatorAddress)){
                    err_msg = LocaleController.getString("sameAsValidatorError", R.string.sameAsValidatorError);
                }
            }

            if(err_msg != null) {
                Toast.makeText(context, err_msg, Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }


    //Todo: Need to verify it properly about validator addr.
    private static final String varifyValidatorAddress(Context context, String address){
        if(address == null){
            return LocaleController.getString("noSelectedValidatorError", R.string.noSelectedValidatorError);
        } else if (address.length() == 0){
            return LocaleController.getString("noSelectedValidatorError", R.string.noSelectedValidatorError);
        } else if (!address.contains("cosmosvaloper") || address.length() != 52){
            return  LocaleController.getString("validatorAddressInvalidError", R.string.validatorAddressInvalidError);
        } else {
            return null;
        }
    }

    //Todo: Need to verify it properly about cosmosaddr.
    public static final boolean isValidCosmosAddress(String address){
        return (address != null && address.contains("cosmos") && address.length() == 45);
    }
}
