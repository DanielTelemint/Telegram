package com.lunamint.wallet.utils;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.Coin;

import java.util.ArrayList;

public class TokenUtil {

    public final static String DEFAULT_TOKENS[] = {"dgms", "ogx", "tepx", "synco", "qual"};
    public final static String DEFAULT_TOKEN_FULLNAMES[] = {"Dogemos", "OGX", "TEPX", "Synco", "Decipher Token"};
    public final static int DEFAULT_TOKEN_IMGS[] = {R.drawable.ic_token_dgms, R.drawable.ic_token_ogx, R.drawable.ic_token_tepx, R.drawable.ic_token_synco, R.drawable.ic_token_qual};

    public static Coin getCoin(ArrayList<Coin> coins, String denom) {
        if (coins != null) {
            for (Coin coin : coins) {
                if (coin.getDenom().equals(denom)) {
                    return coin;
                }
            }
        }

        Coin coin = new Coin();
        coin.setDenom(Blockchain.getInstance().getReserveDenom());
        coin.setAmount("0");

        return coin;
    }

    public static int getTokenIcon(String tokenName) {
        int ic;

        if (tokenName.equals(Blockchain.getInstance().getReserveDenom())) {
            ic = R.drawable.ic_token_cosmos;
        } else if (tokenName.equals("iris")) {
            ic = R.drawable.ic_token_iris;
        } else if (tokenName.equals("dgms")) {
            ic = DEFAULT_TOKEN_IMGS[0];
        } else if (tokenName.equals("ogx")) {
            ic = DEFAULT_TOKEN_IMGS[1];
        } else if (tokenName.equals("tepx")) {
            ic = DEFAULT_TOKEN_IMGS[2];
        } else if (tokenName.equals("synco")) {
            ic = DEFAULT_TOKEN_IMGS[3];
        } else if (tokenName.equals("qual")) {
            ic = DEFAULT_TOKEN_IMGS[4];
        } else {
            ic = R.drawable.profile_frame;
        }
        return ic;
    }

    public static String getTokenFullname(String tokenName) {
        String fullname;
        switch (tokenName) {
            case "dgms":
                fullname = DEFAULT_TOKEN_FULLNAMES[0];
                break;
            case "ogx":
                fullname = DEFAULT_TOKEN_FULLNAMES[1];
                break;
            case "tepx":
                fullname = DEFAULT_TOKEN_FULLNAMES[2];
                break;
            case "synco":
                fullname = DEFAULT_TOKEN_FULLNAMES[3];
                break;
            case "qual":
                fullname = DEFAULT_TOKEN_FULLNAMES[4];
                break;
            default:
                fullname = null;
        }
        return fullname;
    }

    public static String getTokenDisplayName(String tokenName) {
        String name;
        switch (tokenName) {
            case "stake":
                name = "atom";
                break;
            case "uatom":
                name = "atom";
                break;
            default:
                name = tokenName;
        }
        return name;
    }
}
