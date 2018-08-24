package com.telemint.wallet;

public interface WalletListener {
    void onInitFinished(boolean result);
    void onGetAccountList(String result);
    void onCreateAccount(String result);
    void onAccountBalance(String result);
    void onSendTxFinished(String tx);
    void onDelegateTxFinished(String tx);
    void onFailedCommand(String msg);

}
