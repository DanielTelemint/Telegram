package com.telemint.wallet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.telemint.wallet.utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class WalletManager {

    private final static String TAG = "WalletManager";

    private final static String GAIA_VERSION = "0.24.1";
    private final static String CHAIN_ID = "gaia-8001";
    private final static String MONIKER = "telemint_test";
    private final static String GAIACLI = "gaiacli_arm";
    private final static String GAIAD = "gaiad_arm";
    private final static String CONFIG_FILE_NAME = "config.toml";
    private final static String GENESIS_FILE_NAME = "genesis.json";

    private Context mContext = null;
    private String internalStoragePath = null;

    private WalletListener mWalletListener = null;


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

    public void init(@NonNull Context context, @NonNull WalletListener walletListener){

        mContext = context;
        mWalletListener = walletListener;

        internalStoragePath = context.getFilesDir().getAbsolutePath();

        setupGaia();

    }

    private Handler excuteResultHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == GaiaResult.GAIAD_INIT_SUCCESS){
                setupConfig();
            } else if(msg.what == GaiaResult.GAIAD_INIT_FAIL){
                mWalletListener.onInitFinished(false);
            } else if (msg.what == GaiaResult.FAILED_EXECUTE){
                mWalletListener.onFailedCommand("Failed execute command");
            } else if (msg.what == GaiaResult.GET_ACCOUNT_LIST){
                mWalletListener.onGetAccountList(msg.getData().getString("result"));
            } else if (msg.what == GaiaResult.CREATE_ACCOUNT){
                mWalletListener.onCreateAccount(msg.getData().getString("result"));
            } else if (msg.what == GaiaResult.GET_ACCOUNT_BALANCE){
                mWalletListener.onAccountBalance(msg.getData().getString("result"));
            } else if (msg.what == GaiaResult.SEND_TX_FINISHED){
                mWalletListener.onSendTxFinished(msg.getData().getString("result"));
            } else if (msg.what == GaiaResult.DELEGATE_TX_FINISHED){
                mWalletListener.onDelegateTxFinished(msg.getData().getString("result"));
            }

        }
    };

    public void getAccountList(String address){

        excute(WalletManager.GAIACLI, GaiaCommand.GET_ACCOUNT_LIST);

    }

    public void createAccount(String accountName, String password){

        excute(WalletManager.GAIACLI, GaiaCommand.CREATE_ACCOUNT + " " + accountName, password);

    }

    public void getBalance(String address){

        excute(WalletManager.GAIACLI, GaiaCommand.GET_ACCOUNT_BALANCE + " " +address);

    }

    public void send(String accountName, String password, String toAddress, float amount, String denom){

        excute(WalletManager.GAIACLI, GaiaCommand.SEND + " --from=" +accountName + " --to="+toAddress + " --amount="+amount+denom + " --chain-id="+WalletManager.CHAIN_ID, password);

    }


    public boolean isExistGaia(){
        // TODO. Coming checksum function
        if(!FileUtil.isFileExist(internalStoragePath, WalletManager.GAIAD)|
                !FileUtil.isFileExist(internalStoragePath, WalletManager.GAIACLI))
            return false;
        return true;
    }

    private void setupGaia(){
        if(isExistGaia()) {
            setupConfig();
            return;
        }
        boolean result = FileUtil.copy(mContext, internalStoragePath, WalletManager.GAIAD) && FileUtil.copy(mContext, internalStoragePath, WalletManager.GAIACLI);

        if(result){
            excute(WalletManager.GAIAD, GaiaCommand.INIT_GAIAD + " " + WalletManager.MONIKER);
        } else {
            excuteResultHandler.sendEmptyMessage(GaiaResult.GAIAD_INIT_FAIL);
        }
    }

    private void setupConfig(){
        boolean result = FileUtil.copy(mContext, internalStoragePath, WalletManager.CONFIG_FILE_NAME) && FileUtil.copy(mContext, internalStoragePath, WalletManager.GENESIS_FILE_NAME);
        if (result){
            mWalletListener.onInitFinished(true);
        }else{
            mWalletListener.onInitFinished(false);
        }
    }

    private void excute(@NonNull final String exec, final String... cmds){

        if(cmds.length == 0) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File execFile = new File(internalStoragePath, exec);
                    boolean isExecutable = FileUtil.setExecutable(execFile);
                    if(!isExecutable) {
                        excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                        return;
                    }

                    Process p = Runtime.getRuntime().exec(internalStoragePath + File.separator + exec+" "+cmds[0]);

                    if (cmds.length >= 2) {
                        boolean putCmdResult = putCmd(p, cmds[1]);
                        if (!putCmdResult){
                            excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                            return;
                        }
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    int read;
                    char[] buffer = new char[4096];
                    StringBuffer out = new StringBuffer();
                    while ((read = in.read(buffer)) > 0) {
                        if (cmds.length >= 2) {
                            boolean putCmdResult = putCmd(p, cmds[1]);
                            if (!putCmdResult){
                                excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                                return;
                            }
                        }
                        out.append(buffer, 0, read);
                    }
                    in.close();

                    boolean isError = false;
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8") );
                    while ((line = br.readLine()) != null) {
                        Log.e("[Error]", line);
                        isError = true;
                    }
                    br.close();

                    if(isError) {
                        excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                        return;
                    }

                    p.waitFor();

                    Log.w(WalletManager.TAG,"runCmd result out :"+out.substring(0));

                    sendResult(cmds[0], out.substring(0));

                } catch (IOException e) {
                    e.printStackTrace();
                    excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    excuteResultHandler.sendEmptyMessage(GaiaResult.FAILED_EXECUTE);
                }
            }
        }).start();

    }

    private boolean putCmd(Process p, String cmd){
        boolean result = true;
        try{
            OutputStream outs;
            outs = p.getOutputStream();
            outs.write((cmd+"\n").getBytes());
            outs.flush();
        }catch (IOException e){
            e.printStackTrace();
            result = false;
        }
        return result;

    }

    private void sendResult(String cmd, String result){

        // TODO. It will be changed bundle data with case by case.
        if(cmd.contains(GaiaCommand.INIT_GAIAD)){
            excuteResultHandler.sendEmptyMessage(GaiaResult.GAIAD_INIT_SUCCESS);
        } else if(cmd.contains(GaiaCommand.GET_ACCOUNT_LIST)) {
            Bundle bundle = new Bundle();
            bundle.putString("result",result);
            Message msg = new Message();
            msg.what = GaiaResult.GET_ACCOUNT_LIST;
            msg.setData(bundle);
            excuteResultHandler.sendMessage(msg);
        } else if(cmd.contains(GaiaCommand.CREATE_ACCOUNT)) {
            Bundle bundle = new Bundle();
            bundle.putString("result",result);
            Message msg = new Message();
            msg.what = GaiaResult.CREATE_ACCOUNT;
            msg.setData(bundle);
            excuteResultHandler.sendMessage(msg);
        } else if(cmd.contains(GaiaCommand.GET_ACCOUNT_BALANCE)) {
            Bundle bundle = new Bundle();
            bundle.putString("result",result);
            Message msg = new Message();
            msg.what = GaiaResult.GET_ACCOUNT_BALANCE;
            msg.setData(bundle);
            excuteResultHandler.sendMessage(msg);
        } else if(cmd.contains(GaiaCommand.SEND)) {
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            Message msg = new Message();
            msg.what = GaiaResult.SEND_TX_FINISHED;
            msg.setData(bundle);
            excuteResultHandler.sendMessage(msg);
        } else if(cmd.contains(GaiaCommand.STAKE)) {
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            Message msg = new Message();
            msg.what = GaiaResult.DELEGATE_TX_FINISHED;
            msg.setData(bundle);
            excuteResultHandler.sendMessage(msg);
        }

    }


}
