package com.lunamint.lunagram.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.CreateAccountActivity;
import com.lunamint.lunagram.ui.QrCodeActivity;
import com.lunamint.lunagram.ui.SendCoinActivity;
import com.lunamint.lunagram.ui.ManageWalletActivity;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.StakingInfo;
import com.lunamint.wallet.utils.NetworkUtil;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TokenUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class WalletView extends RelativeLayout {

    private boolean maintenance = false;
    private boolean isReadyWalletManager = false;
    private boolean isLoadingWalletManager = false;

    private PullToRefreshLayout pullToRefreshLayout;
    private AccountView accountView;
    private MainCardView mainCardView;
    private CosmosToolsView cosmosToolsView;
    private TokensCardView tokensCardView;

    private AccountInfo accountInfo;
    private ArrayList<Coin> coins;

    boolean isLoadingBalance = false;

    public WalletView(Context context) {
        super(context);

        initWalletManager();
        initSecureKeyStore();

        setBackgroundColor(ActivityCompat.getColor(context, R.color.bg_default));
        setClipToPadding(false);

        pullToRefreshLayout = new PullToRefreshLayout(context);
        addView(pullToRefreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(context);
        pullToRefreshLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout walletLayout = new LinearLayout(context);
        walletLayout.setOrientation(LinearLayout.VERTICAL);
        walletLayout.setClipToPadding(false);
        walletLayout.setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), AndroidUtilities.dp(40));
        scrollView.addView(walletLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ActionBarPullToRefresh.from((Activity) context)
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        getBalance();
                    }
                })
                .setup(pullToRefreshLayout);

        accountView = new AccountView(context, new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case AccountView.BUTTON_SETTING_WALLET:
                        if (!maintenance && !WalletManager.getInstance().isLowerMinAppVersion)
                            showSettingWalletActivity();
                        break;
                    case AccountView.BUTTON_CHANGE_CHAIN:
                        break;
                }
            }
        });

        walletLayout.addView(accountView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        mainCardView = new MainCardView(context, new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case MainCardView.BUTTON_UPDATE:
                        getBalance();
                        break;
                    case MainCardView.BUTTON_CREATE_WALLET:
                        showCreateAccountActivity();
                        break;
                    case MainCardView.BUTTON_SEND:
                        showSendCoinActivity(Blockchain.getInstance().getReserveDenom());
                        break;
                    case MainCardView.BUTTON_ACTION_ADDRESS:
                        copyAddress();
                        //showQrCodeActivity();
                        break;
                }
            }
        });

        walletLayout.addView(mainCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        cosmosToolsView = new CosmosToolsView(context);
        walletLayout.addView(cosmosToolsView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        tokensCardView = new TokensCardView(context, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coins == null) return;
                showSendCoinActivity(coins.get(v.getId()).getDenom());
            }
        });
        walletLayout.addView(tokensCardView);
    }

    public void onResume() {
        if (pullToRefreshLayout != null)
            ActionBarPullToRefresh.from((Activity) getContext())
                    .allChildrenArePullable()
                    .listener(new OnRefreshListener() {
                        @Override
                        public void onRefreshStarted(View view) {
                            getBalance();
                        }
                    })
                    .setup(pullToRefreshLayout);
    }

    public void initWalletManager() {
        if (isLoadingWalletManager) return;
        isLoadingWalletManager = true;
        WalletManager.getInstance().init(getContext(), new ResultHandler(this));
    }

    public void initSecureKeyStore() {
        if (SecureKeyStore.getInstance().isSupported()) return;
        SecureKeyStore.getInstance().init(ApplicationLoader.applicationContext);
    }

    public void update(AccountInfo data) {
        if (data == null) {
            showEmptyAccountLayout();
        } else {
            accountInfo = data;
            showAccountLayout();

            getBalance();
        }
    }

    private void getBalance() {
        if (accountInfo == null) {
            if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            return;
        }
        if (maintenance || WalletManager.getInstance().isLowerMinAppVersion) {
            if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            return;
        }

        if (!isReadyWalletManager) {
            if (!isLoadingWalletManager) {
                initWalletManager();
                return;
            }
        }

        if (isLoadingBalance) return;
        if (!NetworkUtil.isNetworkAvailable(getContext())) {
            showError(LocaleController.getString("unableNetworkError", R.string.unableNetworkError));
            if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            return;
        }
        isLoadingBalance = true;

        mainCardView.showLoading();
        cosmosToolsView.setEnable(false, accountInfo);
        tokensCardView.showLoading();

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getBalances(accountInfo.getAddress()).enqueue(new Callback<ArrayList<Coin>>() {
            @Override
            public void onResponse(Call<ArrayList<Coin>> call, Response<ArrayList<Coin>> response) {
                updateBalance(response);
                isLoadingBalance = false;
                if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            }

            @Override
            public void onFailure(Call<ArrayList<Coin>> call, Throwable t) {
                if (t.getMessage().contains("End of input at")) {
                    updateBalanceEmpty();
                } else {
                    updateBalance(null);
                }

                isLoadingBalance = false;
                if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            }
        });

        getStaking();
    }

    public void getStaking() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getDelegations(accountInfo.getAddress()).enqueue(new Callback<ArrayList<StakingInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<StakingInfo>> call, Response<ArrayList<StakingInfo>> response) {
                if (response.code() == 200) {
                    ArrayList<StakingInfo> stakingInfos = response.body();
                    updateStaking(stakingInfos);
                } else {
                    updateStaking(null);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<StakingInfo>> call, Throwable t) {
                updateStaking(null);
            }
        });
    }

    public void updateBalance(Response<ArrayList<Coin>> response) {
        if (response != null) {
            switch (response.code()) {
                case 200:
                    coins = response.body();
                    mainCardView.updateBalance(accountInfo, TokenUtil.getCoin(coins, Blockchain.getInstance().getReserveDenom()));
                    cosmosToolsView.setEnable(true, accountInfo);
                    break;
                case 204:
                    coins = response.body();
                    mainCardView.updateBalance(accountInfo, TokenUtil.getCoin(coins, Blockchain.getInstance().getReserveDenom()));
                    cosmosToolsView.setEnable(true, accountInfo);
                    break;
                case 500:
                    try {
                        if (response.errorBody() != null) {
                            if (response.errorBody().string().contains("failed to prove merkle proof")) {
                                mainCardView.updateBalance(accountInfo, TokenUtil.getCoin(coins, Blockchain.getInstance().getReserveDenom()));
                                cosmosToolsView.setEnable(true, accountInfo);
                            } else {
                                showError("Server internal error" + response.errorBody().string());
                            }
                        } else {
                            showError("Server internal error");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Server internal error");
                    }
                    break;
                default:
                    showError(LocaleController.getString("cantConnectNodeError", R.string.cantConnectNodeError));
                    break;
            }
        } else {
            if (!NetworkUtil.isNetworkAvailable(getContext())) {
                showError(LocaleController.getString("unableNetworkError", R.string.unableNetworkError));
            } else {
                showError(LocaleController.getString("cantConnectNodeError", R.string.cantConnectNodeError));
            }
        }

        tokensCardView.update(coins);
    }

    public void updateBalanceEmpty() {
        mainCardView.updateBalance(accountInfo, TokenUtil.getCoin(null, Blockchain.getInstance().getReserveDenom()));
        cosmosToolsView.setEnable(true, accountInfo);
    }

    private void updateStaking(ArrayList<StakingInfo> stakingInfos) {
        double stakingAmount = 0;

        if (stakingInfos != null) {
            for (int i = 0; stakingInfos.size() > i; i++) {
                stakingAmount = stakingAmount + stakingInfos.get(i).getShares();
            }
        }

        Coin stakingCoin = new Coin();
        stakingCoin.setAmount(stakingAmount + "");
        stakingCoin.setDenom(Blockchain.getInstance().getReserveDenom());

        mainCardView.updateStaking(stakingCoin);
    }

    public void updateWallet() {
        if (maintenance || WalletManager.getInstance().isLowerMinAppVersion) {
            if (pullToRefreshLayout != null) pullToRefreshLayout.setRefreshComplete();
            return;
        }

        if (!isReadyWalletManager) {
            if (!isLoadingWalletManager) {
                initWalletManager();
                return;
            }
        }

        accountInfo = null;
        coins = null;
        WalletManager.getInstance().getAccountList(new ResultHandler(this));
    }

    // Todo: Implement checking transaction to background Service
    public void onTransactionCreated(String tx) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateWallet();
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(LocaleController.getString("AppName", R.string.AppName))
                        .setContentText(LocaleController.getString("transactionCreate", R.string.transactionCreate))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                mBuilder.setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                notificationManager.notify(777, mBuilder.build());
            }
        }, 1000 * 5);
    }

    private void onGetAccountListResult(CmdResult cmdResult) {
        if (cmdResult == null) {
            showError(LocaleController.getString("unknownError", R.string.unknownError));
        } else if (cmdResult.getErrMsg() != null) {
            showError(cmdResult.getErrMsg());
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
                update(accountList.get(accountIdx));
            } else {
                update(null);
            }
        }
    }

    public void onAccountChanged() {
        updateWallet();
    }

    private void copyAddress() {
        String address = "";
        if (accountInfo != null) address = accountInfo.getAddress();
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(LocaleController.getString("copyMyAddressTitle", R.string.copyMyAddressTitle), address);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), LocaleController.getString("addressCopied", R.string.addressCopied), Toast.LENGTH_LONG).show();
        }
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    private void showEmptyAccountLayout() {
        accountView.update(null);
        mainCardView.showEmptyAccountView();
        cosmosToolsView.setEnable(false, null);
        tokensCardView.showLoading();
    }

    public void showError(String msg) {
        mainCardView.showError(msg);
        cosmosToolsView.setEnable(false, accountInfo);
        tokensCardView.showLoading();
    }

    private void showAccountLayout() {
        accountView.update(accountInfo);
    }

    private void showCreateAccountActivity() {
        Intent intent = new Intent(getContext(), CreateAccountActivity.class);
        intent.putExtra("isFirstAccount", true);
        getContext().startActivity(intent);
    }

    private void showSendCoinActivity(String tokenName) {
        if (coins == null || coins.size() == 0) {
            Toast.makeText(getContext(), LocaleController.getString("noHaveTokens", R.string.noHaveTokens), Toast.LENGTH_LONG).show();
        } else if (WalletManager.getInstance().isLowerMinAppVersion) {
            Toast.makeText(getContext(), LocaleController.getString("lowerAppVersionError", R.string.lowerAppVersionError), Toast.LENGTH_LONG).show();
        } else {
            if (accountInfo == null || accountInfo.getAddress() == null) {
                Toast.makeText(getContext(), LocaleController.getString("noSearchAddressAlert", R.string.noSearchAddressAlert), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getContext(), SendCoinActivity.class);
                intent.putExtra("account_name", accountInfo.getName());
                intent.putExtra("address", accountInfo.getAddress());
                intent.putExtra("token_name", tokenName);
                getContext().startActivity(intent);
            }
        }
    }

    private void showSettingWalletActivity() {
        Intent intent = new Intent(getContext(), ManageWalletActivity.class);
        getContext().startActivity(intent);
    }

    private void showQrCodeActivity() {
        Intent intent = new Intent(getContext(), QrCodeActivity.class);
        intent.putExtra("address", accountInfo.getAddress());
        getContext().startActivity(intent);
    }

    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case WalletManager.GAIA_INIT_SUCCESS:
                isLoadingWalletManager = false;
                isReadyWalletManager = true;
                updateWallet();
                break;
            case WalletManager.GAIA_INIT_FAIL:
                isLoadingWalletManager = false;
                showError(LocaleController.getString("failedInitWalletManager", R.string.failedInitWalletManager));
                break;
            case WalletManager.GET_ACCOUNT_LIST:
                onGetAccountListResult(((CmdResult) msg.getData().getSerializable("result")));
                break;
        }
    }

    protected static class ResultHandler extends Handler {
        private final WeakReference<WalletView> mWalletView;

        private ResultHandler(WalletView walletView) {
            mWalletView = new WeakReference<>(walletView);
        }

        @Override
        public void handleMessage(Message msg) {
            WalletView walletView = mWalletView.get();
            if (walletView != null) walletView.handleMessage(msg);
        }
    }
}
