package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lunamint.lunagram.BuildVars;
import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.AmountWithTokenFormView;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.lunagram.ui.view.FeeFormView;
import com.lunamint.lunagram.ui.view.TxConfirmView;
import com.lunamint.lunagram.ui.view.TxProgressView;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.AccountStatus;
import com.lunamint.wallet.model.BlockInfo;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.LMessage;
import com.lunamint.wallet.model.SendResult;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.utils.AnimUtil;
import com.lunamint.wallet.utils.CmdResultChecker;
import com.lunamint.wallet.utils.FileUtil;
import com.lunamint.wallet.utils.NetworkUtil;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TimeUtil;
import com.lunamint.wallet.utils.TokenUtil;
import com.lunamint.wallet.utils.VarifyUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendCoinActivity extends LunagramBaseActivity {

    private final int GET_TRANSACTION_RESULT_MAX_TRY_COUNT = 7;
    private final int GET_TRANSACTION_RESULT_DELAY = 3000;

    private int step = 0;
    private boolean isSending = false;
    private long currentTimeMillis = 0;
    private boolean needToGoNext = false;

    private boolean isEditable = true;

    private boolean isRequestSelectToken = false;
    private ArrayList<Coin> coins;

    private LMessage lm;

    private JsonObject tx;

    private String tokenName = "";
    private String accountName = "";
    private String fromAddress = "";
    private String toAddress = "";
    private AccountStatus accountStatus;
    private String memo = "";

    private String hash = "";
    private int requestBlockHeight = -1;

    private int tAccount = -1;
    private int tUserId = -1;

    private FrameLayout contentsLayout;
    private ScrollView scrollView;
    private AmountWithTokenFormView amountWithTokenFormView;
    private DefaultFormView addressFormView;
    private FeeFormView feeFormView;
    private DefaultFormView memoFormView;
    private DefaultFormView passwordFormView;
    private TxConfirmView txConfirmView;
    private TxProgressView txProgressView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("send", R.string.send));

        Serializable serializable = getIntent().getSerializableExtra("lm");
        if (serializable != null) {
            lm = (LMessage) serializable;
            tokenName = lm.getDenom();
            toAddress = lm.getTo();
        } else {
            tokenName = getIntent().getStringExtra("token_name");
            accountName = getIntent().getStringExtra("account_name");
            fromAddress = getIntent().getStringExtra("address");
            toAddress = getIntent().getStringExtra("to_address");
            tAccount = getIntent().getIntExtra("t_account", -1);
            tUserId = getIntent().getIntExtra("t_user_id", -1);
        }

        RelativeLayout mainLayout = new RelativeLayout(this);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        contentsLayout = new FrameLayout(this);
        contentsLayout.setPadding(0, 0, 0, AndroidUtilities.dp(70));
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        boolean isNeedScanner = false;
        String amount = "";
        if (toAddress == null) isNeedScanner = true;
        if (lm != null) {
            amount = lm.getAmount();
            memo = lm.getMemo();
            isEditable = false;
        }

        if (tokenName == null || tokenName.length() < 3) {
            tokenName = Blockchain.getInstance().getReserveDenom();
        }
        Coin coin = new Coin();
        coin.setDenom(tokenName);
        coin.setAmount("");

        if (WalletManager.getInstance().activatedSend) {
            nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        } else {
            nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_grey, onClickNextListener);
        }

        nextButton.setOnClickListener(onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20, RelativeLayout.ALIGN_PARENT_BOTTOM));
        setContentView(mainLayout);

        showAmountFormView(false, coin, amount);
        getBalance();

        if (!WalletManager.getInstance().activatedSend) showErrorDialog();

        setupKeyboardListener();
    }

    @Override
    protected void onPause() {
        clearPasswordForm();
        super.onPause();
        if (step == 5) {
            step = 4;
            showPasswordFormView(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (currentTimeMillis > 0) FileUtil.deleteTx(SendCoinActivity.this, currentTimeMillis);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveToBack();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        moveToBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SelectTokenActivity.REQUEST_CODE_SELECT_TOKEN) {
            try {
                Coin coin = (Coin) data.getSerializableExtra("coin");
                if (coin != null) {
                    tokenName = coin.getDenom();
                    updateBalance(coin);
                }
            } catch (Exception e) {
                // ignore
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String address = result.getContents();
                    if (VarifyUtil.isValidCosmosAddress(address) && addressFormView != null) {
                        addressFormView.setValue(address);
                    } else {
                        Toast.makeText(SendCoinActivity.this, LocaleController.getString("cosmosAddressInvalidError", R.string.cosmosAddressInvalidError), Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onCheckFingerprintDone() {
        super.onCheckFingerprintDone();
        if (passwordFormView == null) return;

        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = SecureKeyStore.getInstance().decrypt(getApplicationContext(), pref.getString("W-" + accountName.replaceAll(" ", ""), ""));
        passwordFormView.setValue(pwd);
        passwordFormView.clearFocus();

        if (pwd.length() > 0) varifyPassword(pwd);
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        CmdResult cmdResult = ((CmdResult) msg.getData().getSerializable("result"));
        switch (msg.what) {
            case WalletManager.GET_ACCOUNT_LIST:
                onGetAccountListResult(cmdResult);
                break;
            case WalletManager.MAKE_TX_SEND:
                onTransactionCreatedResult(cmdResult);
                break;
            case WalletManager.TX_SIGN:
                onTransactionSignedResult(cmdResult);
                break;
        }
    }

    private void setupKeyboardListener() {
        if (scrollView == null) return;
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (scrollView == null) return;
                Rect r = new Rect();
                scrollView.getWindowVisibleDisplayFrame(r);

                if (Math.abs(scrollView.getRootView().getHeight() - (r.bottom - r.top)) > 100) {
                    onKeyboardShow();
                }
            }
        });
    }

    private void onKeyboardShow() {
        if (scrollView == null || amountWithTokenFormView == null || step > 0) return;
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.smoothScrollTo(0, amountWithTokenFormView.getCardPositionY());
            }
        });
    }

    private void loadAccountInfo() {
        WalletManager.getInstance().getAccountList(new ResultHandler(this));
    }

    private void sendTelegramMessage(String txHash) {
        if (amountWithTokenFormView == null) return;
        if (tAccount >= 0 && tUserId >= 0) {
            TLRPC.User user = UserConfig.getInstance(tAccount).getCurrentUser();
            String username;
            if (user != null && !TextUtils.isEmpty(user.username)) {
                username = "@" + user.username;
            } else {
                username = LocaleController.getString("anonymousUser", R.string.anonymousUser);
            }

            String msg = "[" + LocaleController.getString("send", R.string.send) + "]\n\n";
            msg = msg + LocaleController.getString("from", R.string.from) + " : " + username + " (" + Parser.getShotAddressForDisplay(fromAddress) + ")\n\n";
            msg = msg + LocaleController.getString("to", R.string.to) + " : " + Parser.getShotAddressForDisplay(addressFormView.getValue()) + "\n\n";
            msg = msg + LocaleController.getString("amount", R.string.amount) + " : " + NumberFormatter.getNumber(amountWithTokenFormView.getValueOrigin()) + TokenUtil.getTokenDisplayName(tokenName) + "\n\n";
            msg = msg + LocaleController.getString("chain", R.string.chain) + " : " + Blockchain.getInstance().getChainId();
            msg = msg + "\n\n" + LocaleController.getString("cosmosTxHash", R.string.cosmosTxHash) + " : " + txHash;
            msg = msg + "\n\n" + BuildVars.getLunagramSupportedMessage();

            SendMessagesHelper.getInstance(tAccount).sendMessage(msg, tUserId, null, null, false, null, null, null);
        }
    }

    private void signTransaction() {
        if (passwordFormView == null || accountStatus == null) return;

        String txPath = getFilesDir() + "/" + FileUtil.getTxFileName(currentTimeMillis);

        WalletManager.getInstance().sign(new ResultHandler(this), fromAddress, passwordFormView.getValue(), txPath, accountStatus);
    }

    private void send() {
        if (tx != null) {
            LcdService lcdService = ApiUtils.getLcdService();
            lcdService.sendTransaction(tx).enqueue(new retrofit2.Callback<SendResult>() {
                @Override
                public void onResponse(Call<SendResult> call, Response<SendResult> response) {
                    if (response.code() == 200 && response.body() != null) {
                        hash = response.body().getHash();
                        getLatestBlock();
                    } else {
                        String err;
                        try {
                            if (response.errorBody() != null) {
                                err = LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string();
                            } else {
                                err = LocaleController.getString("internalServerError", R.string.internalServerError);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            err = LocaleController.getString("internalServerError", R.string.internalServerError);
                        }

                        if (txProgressView != null)
                            txProgressView.showError(err);
                        onSendFinished();
                    }
                }

                @Override
                public void onFailure(Call<SendResult> call, Throwable t) {
                    if (txProgressView == null) return;
                    if (NetworkUtil.isNetworkAvailable(SendCoinActivity.this)) {
                        showNodeConnectionErrorAlert();
                    } else {
                        txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                    }
                    onSendFinished();
                }
            });
        } else {
            if (txProgressView != null)
                txProgressView.showError(LocaleController.getString("nullPointerTx", R.string.nullPointerTx));
            onSendFinished();
        }
    }

    private void getLatestBlock() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LcdService lcdService = ApiUtils.getLcdService();
                lcdService.getLatestBlock().enqueue(new Callback<BlockInfo>() {
                    @Override
                    public void onResponse(Call<BlockInfo> call, Response<BlockInfo> response) {
                        if (response.code() == 200 && response.body() != null && response.body().getBlock() != null && response.body().getBlock().getHeader() != null) {
                            if (requestBlockHeight < 0)
                                requestBlockHeight = response.body().getBlock().getHeader().getHeight();

                            if (requestBlockHeight + GET_TRANSACTION_RESULT_MAX_TRY_COUNT <= response.body().getBlock().getHeader().getHeight()) {
                                txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend) + "\n\nRequest block height : " + requestBlockHeight + "\nLast checked block height : " + response.body().getBlock().getHeader().getHeight());
                                onSendFinished();
                            } else {
                                getTransactionResult();
                            }

                        } else {
                            txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                            onSendFinished();
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockInfo> call, Throwable t) {
                        if (txProgressView == null) return;
                        if (NetworkUtil.isNetworkAvailable(SendCoinActivity.this)) {
                            txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                        } else {
                            txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                        }
                        onSendFinished();
                    }
                });
            }
        }, GET_TRANSACTION_RESULT_DELAY);
    }

    private void getTransactionResult() {
        if (hash == null || hash.equals("")) {
            txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
            return;
        }

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getTransactionDetail(hash).enqueue(new Callback<DefaultHistory>() {
            @Override
            public void onResponse(Call<DefaultHistory> call, Response<DefaultHistory> response) {
                if (response.errorBody() != null) {
                    try {
                        if (response.errorBody().string().contains("not found")) {
                            getLatestBlock();
                        } else {
                            txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                            onSendFinished();
                        }
                    } catch (Exception e) {
                        txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                        onSendFinished();
                    }
                } else {
                    if (response.code() == 200 && response.body() != null) {
                        if (txProgressView != null)
                            txProgressView.update(TxProgressView.TYPE_SEND, response.body().getResult(), response.body().getHash(), fromAddress, addressFormView.getValue(), tokenName, NumberFormatter.getNumber(amountWithTokenFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), memoFormView.getValue(), fromAddress);

                        sendTelegramMessage(response.body().getHash());
                        sendResultToCallback();
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.transactionCreated, response.body().getHash());
                        FileUtil.deleteTx(SendCoinActivity.this, currentTimeMillis);
                    } else {
                        txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                    }
                    onSendFinished();
                }
            }

            @Override
            public void onFailure(Call<DefaultHistory> call, Throwable t) {
                if (txProgressView == null) return;
                if (NetworkUtil.isNetworkAvailable(SendCoinActivity.this)) {
                    txProgressView.showError(LocaleController.getString("failedSend", R.string.failedSend));
                } else {
                    txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                }
                onSendFinished();
            }
        });
    }

    private void getBalance() {
        if (accountName == null || accountName.equals("")) {
            loadAccountInfo();
            return;
        }
        if (!VarifyUtil.isValidCosmosAddress(fromAddress)) return;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getBalances(fromAddress).enqueue(new Callback<ArrayList<Coin>>() {
            @Override
            public void onResponse(Call<ArrayList<Coin>> call, Response<ArrayList<Coin>> response) {
                if (response.code() == 200) {
                    updateBalance(response);
                    coins = response.body();
                    if (isRequestSelectToken) showSelectTokenActivity();
                    if (needToGoNext) {
                        needToGoNext = false;
                        hideProgress();
                        if (amountWithTokenFormView != null)
                            varifyAmount(amountWithTokenFormView.getValueOrigin());
                    }

                    if (response.body() != null)
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.balanceChanged, response);
                } else {
                    showNodeConnectionErrorAlert();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<ArrayList<Coin>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(SendCoinActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(SendCoinActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void getAccountStatus() {
        isSending = true;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getAccountStatus(fromAddress).enqueue(new Callback<AccountStatus>() {
            @Override
            public void onResponse(Call<AccountStatus> call, Response<AccountStatus> response) {
                if (response.code() == 200) {
                    accountStatus = response.body();
                    makeTransactionSend();
                } else {
                    step = 4;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                    isSending = false;
                }
            }

            @Override
            public void onFailure(Call<AccountStatus> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(SendCoinActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    step = 4;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                }
                isSending = false;
            }
        });
    }

    private void updateBalance(Response<ArrayList<Coin>> response) {
        if (amountWithTokenFormView != null)
            amountWithTokenFormView.update(Parser.getCoinFromName(response, tokenName));
    }

    private void updateBalance(Coin coin) {
        if (amountWithTokenFormView != null)
            amountWithTokenFormView.update(coin);
    }

    private void makeTransactionSend() {
        if (tx != null && currentTimeMillis > 0)
            FileUtil.deleteTx(SendCoinActivity.this, currentTimeMillis);
        if (accountName.equals("")) {
            Toast.makeText(SendCoinActivity.this, LocaleController.getString("loadingAccountInfoError", R.string.loadingAccountInfoError), Toast.LENGTH_LONG).show();
            return;
        }
        if (amountWithTokenFormView == null) return;

        WalletManager.getInstance().makeTxSend(new ResultHandler(this), fromAddress, addressFormView.getValue(), amountWithTokenFormView.getValue(), feeFormView.getValue(), tokenName, memoFormView.getValue());
    }

    //Todo: toast message remove
    private void sendResultToCallback() {
        if (lm == null) return;

        JsonObject callbackBody = lm.getCallbackBody();
        if (callbackBody == null) {
            Toast.makeText(SendCoinActivity.this, "can not get custom_field. please check custom_field is corrected.", Toast.LENGTH_LONG).show();
            return;
        }

        LcdService callbackService = ApiUtils.getCallbackService(lm.getCallbackUrl());
        callbackService.sendResultToCallback(lm.getCallbackEndpoint(), lm.getCallbackBody()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(SendCoinActivity.this, "sendResultToCallback()\n" + lm.getCallbackUrl() + lm.getCallbackEndpoint() + "\nresult code = " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SendCoinActivity.this, "sendResultToCallback() result onFailure\n" + lm.getCallbackUrl() + lm.getCallbackEndpoint(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onGetAccountListResult(CmdResult cmdResult) {
        if (cmdResult == null) {
            Toast.makeText(SendCoinActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
        } else if (cmdResult.getErrMsg() != null) {
            Toast.makeText(SendCoinActivity.this, cmdResult.getErrMsg(), Toast.LENGTH_LONG).show();
        } else {
            ArrayList<AccountInfo> accountList = Parser.getAccountList(cmdResult.getData());
            if (accountList != null && accountList.size() != 0) {
                int accountIdx = 0;
                String currentAccountName = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).getString("currentAccountName", "");
                for (int i = 0; accountList.size() > i; i++) {
                    if (accountList.get(i).getName().equals(currentAccountName)) {
                        accountIdx = i;
                        break;
                    }
                }
                accountName = accountList.get(accountIdx).getName();
                fromAddress = accountList.get(accountIdx).getAddress();
                getBalance();
            }
        }
    }

    private void onTransactionCreatedResult(CmdResult cmdResult) {
        String errMsg = CmdResultChecker.checkCmdResult(cmdResult);

        if (errMsg == null) {
            setCurrentTimeMillis();
            if (FileUtil.writeTx(SendCoinActivity.this, currentTimeMillis, cmdResult.getData())) {
                signTransaction();
            } else {
                step = 4;
                showPasswordFormView(true);
                if (passwordFormView != null)
                    passwordFormView.showError(LocaleController.getString("failedCreateTx", R.string.failedCreateTx));
                isSending = false;
            }
        } else {
            step = 4;
            showPasswordFormView(true);
            if (passwordFormView != null) passwordFormView.showError(errMsg);
            isSending = false;
        }
    }

    private void onTransactionSignedResult(CmdResult cmdResult) {
        String err = CmdResultChecker.checkCmdResult(cmdResult);

        if (err == null) {
            tx = Parser.getRawTransaction(cmdResult.getData());
            if (tx != null) {
                send();
                clearPasswordForm();
                return;
            } else {
                err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
            }
        }

        step = 4;
        showPasswordFormView(true);
        if (passwordFormView != null) passwordFormView.showError(err);
        isSending = false;
    }

    private void clearPasswordForm() {
        if (passwordFormView == null) return;
        passwordFormView.clear();
    }

    private void varifyAmount(String amount) {
        if (amountWithTokenFormView == null) return;
        if (coins == null) {
            needToGoNext = true;
            showProgress();
            return;
        }
        if (amount.length() != 0) {
            try {
                BigDecimal bamount = new BigDecimal(amount);
                if (bamount.compareTo(BigDecimal.ZERO) < 1) {
                    amountWithTokenFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
                } else {
                    if (bamount.compareTo(new BigDecimal(TokenUtil.getCoin(coins, tokenName).getAmount())) < 1) {
                        scrollView.smoothScrollTo(0, 0);
                        step = 1;
                        showAddressFormView(false);
                    } else {
                        amountWithTokenFormView.showError(LocaleController.getString("notEnoughBalance", R.string.notEnoughBalance));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                amountWithTokenFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
            }
        } else {
            amountWithTokenFormView.showError(LocaleController.getString("amountEmptyError", R.string.amountEmptyError));
        }
    }

    private void varifyAddress(String address) {
        if (addressFormView == null) return;
        if (address.length() == 0) {
            addressFormView.showError(LocaleController.getString("addressEmptyError", R.string.addressEmptyError));
        } else if (!VarifyUtil.isValidCosmosAddress(address)) {
            addressFormView.showError(LocaleController.getString("addressInvalidError", R.string.addressInvalidError));
        } else {
            step = 2;
            showMemoFormView(false);
        }
    }

    private void varifyMemo(String memo) {
        if (memo.length() > 30) {
            memoFormView.showError(LocaleController.getString("invalidMemoError", R.string.invalidMemoError));
        } else {
            step = 3;
            showFeeFormView(false);
        }
    }

    private void varifyFee(String fee) {
        if (amountWithTokenFormView == null || feeFormView == null) return;
        if (fee.length() != 0) {
            try {
                BigDecimal bamount = new BigDecimal(fee);
                if (bamount.compareTo(new BigDecimal("0.0005")) < 0) {
                    feeFormView.showError(LocaleController.getString("feeNotEnoughError", R.string.feeNotEnoughError));
                } else if (bamount.compareTo(BigDecimal.ONE) == 1) {
                    feeFormView.showError(LocaleController.getString("feeInvalidError", R.string.feeInvalidError));
                } else {
                    bamount = bamount.add(new BigDecimal(amountWithTokenFormView.getValueOrigin()));
                    if (bamount.compareTo(new BigDecimal(TokenUtil.getCoin(coins, tokenName).getAmount())) < 1) {
                        scrollView.smoothScrollTo(0, 0);
                        hideKeyboard();
                        step = 4;
                        showPasswordFormView(false);
                        if (hasPassword() && isEnabledFingerprint(accountName)) checkFingerprint();
                    } else {
                        feeFormView.showError(LocaleController.getString("notEnoughBalance", R.string.notEnoughBalance));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                feeFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
            }
        } else {
            feeFormView.showError(LocaleController.getString("amountEmptyError", R.string.amountEmptyError));
        }
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            step = 5;
            showTxConfirmView();
            if (txConfirmView != null)
                txConfirmView.update(TxProgressView.TYPE_SEND, null, fromAddress, addressFormView.getValue(), tokenName, NumberFormatter.getNumber(amountWithTokenFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), memoFormView.getValue(), fromAddress);
        }
    }

    private void showAmountFormView(boolean isMoveToBack, Coin coin, String amount) {
        hideKeyboard();
        if (amountWithTokenFormView == null) {
            amountWithTokenFormView = new AmountWithTokenFormView(this, isEditable, coin, amount, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectTokenActivity();
                }
            });
            contentsLayout.addView(amountWithTokenFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(amountWithTokenFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, amountWithTokenFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = amountWithTokenFormView;
    }

    private void showAddressFormView(boolean isMoveToBack) {
        if (addressFormView == null) {
            addressFormView = new DefaultFormView(this, isEditable, false, false, LocaleController.getString("address", R.string.address), LocaleController.getString("doubleCheckAddress", R.string.doubleCheckAddress), toAddress, null);
            contentsLayout.addView(addressFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(addressFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, addressFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        if (isEditable) addressFormView.setFocus();
        currentView = addressFormView;
    }

    private void showMemoFormView(boolean isMoveToBack) {
        if (memoFormView == null) {
            memoFormView = new DefaultFormView(this, isEditable, false, false, LocaleController.getString("memo", R.string.memo), LocaleController.getString("ifNeedMemo", R.string.ifNeedMemo), memo, "Optional");
            contentsLayout.addView(memoFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(memoFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, memoFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        if (isEditable) memoFormView.setFocus();
        currentView = memoFormView;
    }

    private void showFeeFormView(boolean isMoveToBack) {
        hideKeyboard();
        if (feeFormView == null) {
            feeFormView = new FeeFormView(this);
            contentsLayout.addView(feeFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(feeFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, feeFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = feeFormView;
    }

    private void showPasswordFormView(boolean isMoveToBack) {
        if (passwordFormView == null) {
            passwordFormView = new DefaultFormView(this, true, true, false, LocaleController.getString("password", R.string.password), LocaleController.getString("differentWalletsDifferentPasswords", R.string.differentWalletsDifferentPasswords), null, null);
            contentsLayout.addView(passwordFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(passwordFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, passwordFormView, false);
        }
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        if (!isEnabledFingerprint(accountName)) passwordFormView.setFocus();
        currentView = passwordFormView;
    }

    private void showTxConfirmView() {
        hideKeyboard();
        if (txConfirmView != null) contentsLayout.removeView(txConfirmView);

        txConfirmView = new TxConfirmView(this, LocaleController.getString("confirmSend", R.string.confirmSend));
        contentsLayout.addView(txConfirmView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        AnimUtil.changeView(currentView, txConfirmView, false);
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = txConfirmView;
    }

    private void showTxProgressView() {
        hideKeyboard();
        txProgressView = new TxProgressView(this);
        contentsLayout.addView(txProgressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        AnimUtil.changeView(currentView, txProgressView, false);
        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_grey);
        currentView = txProgressView;
    }

    private void showSelectTokenActivity() {
        if (coins == null) {
            showProgress();
            isRequestSelectToken = true;
            return;
        }
        isRequestSelectToken = false;

        Intent intent = new Intent(SendCoinActivity.this, SelectTokenActivity.class);
        intent.putExtra("coins", coins);
        startActivityForResult(intent, SelectTokenActivity.REQUEST_CODE_SELECT_TOKEN);
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("sendDisabled", R.string.sendDisabled));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private void onSendFinished() {
        isSending = false;
        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_radius4_blue);
    }

    private boolean hasPassword() {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = pref.getString("W-" + accountName.replaceAll(" ", ""), "");
        return !pwd.equals("");
    }

    private void setCurrentTimeMillis() {
        currentTimeMillis = TimeUtil.getCurrentTimeMillis();
    }

    private void moveToBack() {
        switch (step) {
            case 0:
                finish();
                break;
            case 1:
                step = 0;
                showAmountFormView(true, null, null);
                break;
            case 2:
                step = 1;
                showAddressFormView(true);
                break;
            case 3:
                step = 2;
                showMemoFormView(true);
                break;
            case 4:
                step = 3;
                showFeeFormView(true);
                break;
            case 5:
                step = 4;
                showPasswordFormView(true);
                break;
        }
    }

    View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (step) {
                case 0:
                    if (amountWithTokenFormView != null)
                        varifyAmount(amountWithTokenFormView.getValueOrigin());
                    break;
                case 1:
                    if (addressFormView != null) varifyAddress(addressFormView.getValue());
                    break;
                case 2:
                    if (memoFormView != null) varifyMemo(memoFormView.getValue());
                    break;
                case 3:
                    if (feeFormView != null) varifyFee(feeFormView.getValueOrigin());
                    break;
                case 4:
                    if (passwordFormView != null) varifyPassword(passwordFormView.getValue());
                    break;
                case 5:
                    step = 6;
                    showTxProgressView();
                    getAccountStatus();
                    break;
                case 6:
                    if (!isSending) finish();
                    break;
            }
        }
    };
}
