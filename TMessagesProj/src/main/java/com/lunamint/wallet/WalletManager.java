package com.lunamint.wallet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.AccountStatus;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.utils.FileUtil;

import org.telegram.messenger.LocaleController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class WalletManager {

    private final static String TAG = "WalletManager";

    public static final int GAIA_INIT_SUCCESS = 1;
    public static final int GAIA_INIT_FAIL = 2;
    public final static int GET_ACCOUNT_LIST = 3;
    public final static int CREATE_ACCOUNT = 4;
    public final static int DELETE_ACCOUNT = 5;
    public final static int MAKE_TX_SEND = 6;
    public final static int MAKE_TX_STAKE = 7;
    public final static int MAKE_TX_UNSTAKE = 8;
    public final static int MAKE_TX_REDELEGATE = 9;
    public final static int MAKE_TX_VOTE = 10;
    public final static int MAKE_TX_CLAIM_REWARD = 11;
    public final static int TX_SIGN = 12;

    private final static String GAIA_VERSION = "0.34.3";
    private final static String GAIACLI = "gaiacli_arm";

    private final static String GAIACLI_CHECKSUM = "B3348E857C6C5F978BE0A263A1F167DF";
    private final static String GAIACLI_OLD_HASHES[] = {"A256C17BC2B0B5BB98C42826CEE274E9","64841DDB00386FE3896B7C9D16DE8BAB"};

    private String internalStoragePath = null;

    public boolean activatedSend = false;
    public boolean isLowerMinAppVersion = false;

    private static volatile WalletManager Instance = null;

    public static WalletManager getInstance() {
        WalletManager walletManagerInstance = Instance;
        if (walletManagerInstance == null) {
            synchronized (WalletManager.class) {
                walletManagerInstance = Instance;
                if (walletManagerInstance == null) {
                    Instance = walletManagerInstance = new WalletManager();
                }
            }
        }
        return walletManagerInstance;
    }

    public void init(@NonNull final Context context, @NonNull final Handler handler) {
        internalStoragePath = context.getFilesDir().getAbsolutePath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setupGaia(context, handler);
            }
        }).start();
    }

    /*
    public void getCliVersion(Handler handler) {
        excute(handler, WalletManager.GAIACLI, new String[]{getGaiacli(), GaiaCommand.VERSION, "--output=json"});
    }

    public void getStatus(Handler handler) {
        excute(handler, WalletManager.GAIACLI, new String[]{getGaiacli(), GaiaCommand.STATUS, "--output=json"});
    }*/

    public void getAccountList(Handler handler) {
        excute(GET_ACCOUNT_LIST, handler, WalletManager.GAIACLI, new String[]{getGaiacli(), GaiaCommand.KEYS, GaiaCommand.LIST, "--output=json"});
    }

    public void createAccount(Handler handler, String accountName, String password) {
        excute(CREATE_ACCOUNT, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.KEYS,
                GaiaCommand.ADD,
                accountName,
                "--output=json"
        }, password);
    }

    public void createAccount(Handler handler, String seed, String accountName, String password) {
        excute(CREATE_ACCOUNT, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.KEYS,
                GaiaCommand.ADD,
                accountName,
                "--recover",
                "--output=json"
        }, password, seed);
    }

    public void deleteAccount(Handler handler, String accountName, String password) {
        excute(DELETE_ACCOUNT, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.KEYS,
                GaiaCommand.DELETE,
                accountName,
                "--output=json"
        }, password);
    }

    public void makeTxSend(Handler handler, String fromAddress, String toAddress, String amount, String fee, String denom, String memo) {
        excute(MAKE_TX_SEND, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.SEND,
                toAddress,
                amount + denom,
                "--from=" + fromAddress,
                "--memo=" + memo,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        });
    }

    public void makeTransactionStake(Handler handler, String fromAddress, String password, String validatorAddress, String amount, String fee, String denom) {
        excute(MAKE_TX_STAKE, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.STAKE,
                GaiaCommand.DELEGATE,
                validatorAddress,
                amount + denom,
                "--from=" + fromAddress,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        }, password);
    }

    public void makeTransactionUnstake(Handler handler, String fromAddress, String password, String validatorAddress, String amount, String fee, String denom) {
        excute(MAKE_TX_UNSTAKE, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.STAKE,
                GaiaCommand.UNBOND,
                validatorAddress,
                amount + denom,
                "--from=" + fromAddress,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        }, password);
    }

    public void makeTransactionRedelegate(Handler handler, String fromAddress, String password, String sourceValidatorAddress, String destValidatorAddress, String amount, String fee, String denom) {
        excute(MAKE_TX_REDELEGATE, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.STAKE,
                GaiaCommand.REDELEGATE,
                sourceValidatorAddress,
                destValidatorAddress,
                amount + denom,
                "--from=" + fromAddress,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        }, password);
    }

    public void makeTxVote(Handler handler, String accountName, String password, String proposalId, String vote, String fee) {
        excute(MAKE_TX_VOTE, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.GOV,
                GaiaCommand.VOTE,
                proposalId,
                vote,
                "--from=" + accountName,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        }, password);
    }

    public void makeTxClaimReward(Handler handler, String fromAddress, String password, String fee) {
        excute(MAKE_TX_CLAIM_REWARD, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.DISTR,
                GaiaCommand.WITHDRAW_ALL_REWARDS,
                "--from=" + fromAddress,
                "--fees=" + fee + Blockchain.getInstance().getReserveDenom(),
                "--gas=300000",
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--node=" + Blockchain.getInstance().getNode(),
                "--generate-only=true",
                "--output=json"
        }, password);
    }

    public void sign(Handler handler, String accountName, String password, String txPath, AccountStatus accountStatus) {
        excute(TX_SIGN, handler, WalletManager.GAIACLI, new String[]{
                getGaiacli(),
                GaiaCommand.TX,
                GaiaCommand.SIGN,
                txPath,
                "--from=" + accountName,
                "--account-number=" + accountStatus.getValue().getAccountNumber(),
                "--sequence=" + accountStatus.getValue().getSequence(),
                "--chain-id=" + Blockchain.getInstance().getChainId(),
                "--offline=true",
                "--output=json"
        }, password);
    }


    public boolean isExistGaia() {
        return (FileUtil.isFileExist(internalStoragePath, WalletManager.GAIACLI));
    }

    public boolean checksumGaiacli() {
        return FileUtil.getChecksumMD5(internalStoragePath + "/" + WalletManager.GAIACLI).equals(GAIACLI_CHECKSUM);
    }

    public boolean isOldHash() {
        boolean isOld = false;
        String hash = FileUtil.getChecksumMD5(internalStoragePath + "/" + WalletManager.GAIACLI);
        for (String oldHash : GAIACLI_OLD_HASHES) {
            if (hash.equals(oldHash)) {
                isOld = true;
                break;
            }
        }
        return isOld;
    }

    private void setupGaia(@NonNull Context context, Handler handler) {
        if (isExistGaia()) {
            if (isOldHash()) {
                FileUtil.deleteFile(internalStoragePath, WalletManager.GAIACLI);
                copyGaiaCli(context, handler);
            } else {
                if (handler != null) handler.sendEmptyMessage(GAIA_INIT_SUCCESS);
            }
        } else {
            copyGaiaCli(context, handler);
        }
    }

    private void copyGaiaCli(@NonNull Context context, Handler handler) {
        boolean result = FileUtil.copy(context, internalStoragePath, WalletManager.GAIACLI);
        if (result) {
            if (handler != null) handler.sendEmptyMessage(GAIA_INIT_SUCCESS);
        } else {
            if (handler != null) handler.sendEmptyMessage(GAIA_INIT_FAIL);
        }
    }

    private void excute(final int action, final Handler handler, @NonNull final String exec, final String mainCmd[], final String... cmds) {
        for (String t : mainCmd) {
        }
        if (mainCmd.length == 0) {
            sendResult(action, handler, LocaleController.getString("noHaveCommandError", R.string.noHaveCommandError), null);
            return;
        }

        if (hasBadRequest(mainCmd)) {
            sendResult(action, handler, LocaleController.getString("badRequestError", R.string.badRequestError), null);
            return;
        }

        if (mainCmd.length > 5 && mainCmd[4].contains("--recover")) {
            if (cmds.length > 2) {
                sendResult(action, handler, LocaleController.getString("badRequestError", R.string.badRequestError), null);
                return;
            }
        } else {
            if (cmds.length > 1) {
                sendResult(action, handler, LocaleController.getString("badRequestError", R.string.badRequestError), null);
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!isExistGaia()) {
                    sendResult(action, handler, LocaleController.getString("notBeenInitWalletManagerError", R.string.notBeenInitWalletManagerError), null);
                    return;
                }
                if (!checksumGaiacli()) {
                    sendResult(action, handler, LocaleController.getString("noMatchChecksumError", R.string.noMatchChecksumError), null);
                    return;
                }

                try {
                    File execFile = new File(internalStoragePath, exec);
                    boolean isExecutable = FileUtil.setExecutable(execFile);
                    if (!isExecutable) {
                        sendResult(action, handler, LocaleController.getString("noExecutableError", R.string.noExecutableError), null);
                        return;
                    }

                    Process p = Runtime.getRuntime().exec(mainCmd);

                    if (cmds.length >= 1) {
                        for (String cmd : cmds) {
                            boolean putCmdResult = putCmd(p, cmd);
                            if (!putCmdResult) {
                                sendResult(action, handler, LocaleController.getString("failedExcuteSubCmdError", R.string.failedExcuteSubCmdError), null);
                                return;
                            }
                        }
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    int read;
                    char[] buffer = new char[4096];
                    StringBuffer out = new StringBuffer();
                    while ((read = in.read(buffer)) > 0) {
                        out.append(buffer, 0, read);
                    }
                    in.close();

                    boolean isError = false;
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
                    StringBuffer error = new StringBuffer();
                    while ((read = br.read(buffer)) > 0) {
                        isError = true;
                        error.append(buffer, 0, read);
                    }
                    br.close();

                    if (isError) {
                        if (mainCmd.length >= 3 && mainCmd[1].contains(GaiaCommand.KEYS) && mainCmd[2].contains(GaiaCommand.ADD) && error.substring(0).contains("pubkey")) {
                            sendResult(action, handler, null, error.substring(0));
                        } else {
                            sendResult(action, handler, error.substring(0), null);
                        }
                        return;
                    }

                    p.waitFor();

                    sendResult(action, handler, null, out.substring(0));
                } catch (IOException e) {
                    e.printStackTrace();
                    sendResult(action, handler, e.getMessage(), null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sendResult(action, handler, e.getMessage(), null);
                }
            }
        }).start();
    }

    private void sendResult(int action, Handler handler, String err, String result) {
        if (handler == null) return;

        CmdResult cmdResult;

        if (err != null) {
            cmdResult = new CmdResult(err, null);
        } else {
            cmdResult = new CmdResult(null, result);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("result", cmdResult);

        Message msg = new Message();
        msg.setData(bundle);

        msg.what = action;

        handler.sendMessage(msg);
    }

    private boolean putCmd(Process p, String cmd) {
        boolean result = true;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            bw.write(cmd + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private String getGaiacli() {
        return internalStoragePath + File.separator + WalletManager.GAIACLI;
    }

    private boolean hasBadRequest(String[] cmds) {
        for (String cmd : cmds) {
            if (cmd.contains("&") || cmd.contains("<") || cmd.contains(">") || cmd.contains("\"") || cmd.contains("->")) {
                return true;
            }
        }
        return false;
    }
}
