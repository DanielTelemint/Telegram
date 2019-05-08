package com.lunamint.lunagram.ui.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.SendCoinActivity;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TokenUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class TransactionSendDetailView extends LinearLayout {

    public static int TYPE_CONFIRM = 0;
    public static int TYPE_RESULT = 1;

    private boolean enableSend;

    private String txHash;
    private String from;
    private String to;

    private String myAddress;
    private String targetAddress;

    private ProgressDialog mProgressDialog;

    public TransactionSendDetailView(Context context, int type, boolean enableSend, boolean result, String txHash, String from, String to, String denom, String amount, String fee, String memo, String myAddress) {
        super(context);

        this.enableSend = enableSend;
        this.txHash = txHash;
        this.from = from;
        this.to = to;
        this.myAddress = myAddress;


        amount = NumberFormatter.getNumber(amount);
        fee = NumberFormatter.getNumber(fee);

        String resultText;

        if (type == TYPE_CONFIRM) {
            resultText = null;
        } else if (result) {
            resultText = LocaleController.getString("successed", R.string.successed);
        } else {
            resultText = LocaleController.getString("failed", R.string.failed);
        }

        setOrientation(LinearLayout.VERTICAL);
        setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), 0);

        String titles[] = {LocaleController.getString("result", R.string.result), LocaleController.getString("cosmosTxHash", R.string.cosmosTxHash), LocaleController.getString("from", R.string.from), LocaleController.getString("to", R.string.to), LocaleController.getString("amount", R.string.amount), LocaleController.getString("transactionFee", R.string.transactionFee), LocaleController.getString("memo", R.string.memo), LocaleController.getString("chain", R.string.chain)};
        String desc[] = {resultText, txHash, from, to, amount + "\n" + TokenUtil.getTokenDisplayName(denom), fee + "\n" + Blockchain.getInstance().getReserveDisplayName(), memo, Blockchain.getInstance().getChainId()};

        for (int i = 0; titles.length > i; i++) {
            if (desc[i] == null) continue;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(0, AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14));
            if (i > 0 && i < 4) {
                linearLayout.setId(i);
                linearLayout.setOnClickListener(onClickListener);
            }
            addView(linearLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView titleTextview = new TextView(context);
            titleTextview.setTextSize(0, AndroidUtilities.dp(14));
            titleTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
            titleTextview.setText(titles[i]);
            titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
            linearLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 60, 0));

            TextView valueTextview = new TextView(context);
            valueTextview.setTextSize(0, AndroidUtilities.dp(14));
            if (i == 0 && !result) {
                valueTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.coral_red));
            } else {
                valueTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
            }

            valueTextview.setGravity(Gravity.RIGHT);
            valueTextview.setText(desc[i]);
            linearLayout.addView(valueTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

            if (i < titles.length - 1) {
                ImageView line = new ImageView(context);
                line.setBackgroundColor(0x88E4E9FE);
                addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
            }
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 1:
                    copy(LocaleController.getString("copyAddressTitle", R.string.copyTxHashTitle), txHash, LocaleController.getString("txHashCopied", R.string.txHashCopied));
                    break;
                case 2:
                    if (!enableSend) {
                        copy(LocaleController.getString("copyAddressTitle", R.string.copyAddressTitle), from, LocaleController.getString("addressCopied", R.string.addressCopied));
                        return;
                    }
                    if (myAddress != null && from != null && from.equals(myAddress)) {
                        copy(LocaleController.getString("copyMyAddressTitle", R.string.copyMyAddressTitle), from, LocaleController.getString("addressCopied", R.string.addressCopied));
                    } else {
                        showDialog(from);
                    }
                    break;
                case 3:
                    if (!enableSend) {
                        copy(LocaleController.getString("copyAddressTitle", R.string.copyAddressTitle), to, LocaleController.getString("addressCopied", R.string.addressCopied));
                        return;
                    }
                    if (myAddress != null && to != null && to.equals(myAddress)) {
                        copy(LocaleController.getString("copyMyAddressTitle", R.string.copyMyAddressTitle), to, LocaleController.getString("addressCopied", R.string.addressCopied));
                    } else {
                        showDialog(to);
                    }
                    break;
            }
        }
    };

    private void showDialog(final String targetAddress) {
        this.targetAddress = targetAddress;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new CharSequence[]{LocaleController.getString("sendToken", R.string.sendToken), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    getCurrentWalletAccount();
                } else {
                    copy(LocaleController.getString("copyAddressTitle", R.string.copyAddressTitle), targetAddress, LocaleController.getString("addressCopied", R.string.addressCopied));
                }
            }
        });
        builder.create().show();
    }

    private void getCurrentWalletAccount() {
        showProgress();
        WalletManager.getInstance().getAccountList(new ResultHandler(this));
    }

    private void showSendCoinActivity(AccountInfo accountInfo, String toAddress) {
        if (WalletManager.getInstance().isLowerMinAppVersion) {
            Toast.makeText(getContext(), LocaleController.getString("lowerAppVersionError", R.string.lowerAppVersionError), Toast.LENGTH_LONG).show();
            return;
        }

        if (accountInfo == null || accountInfo.getAddress() == null) {
            Toast.makeText(getContext(), LocaleController.getString("noSearchAddressAlert", R.string.noSearchAddressAlert), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), SendCoinActivity.class);
            intent.putExtra("account_name", accountInfo.getName());
            intent.putExtra("address", accountInfo.getAddress());
            intent.putExtra("to_address", toAddress);
            getContext().startActivity(intent);
        }
    }

    private void copy(String title, String text, String successMsg) {
        if (text == null || text.length() == 0) return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(title, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), successMsg, Toast.LENGTH_LONG).show();
        }
    }

    protected void showProgress() {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(getContext(), "", LocaleController.getString("sending", R.string.sending), true);
        mProgressDialog.setCancelable(false);
    }

    protected void hideProgress() {
        if (mProgressDialog == null) return;
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case WalletManager.GET_ACCOUNT_LIST:
                onGetAccountListResult(((CmdResult) msg.getData().getSerializable("result")));
                break;
        }
    }

    private void onGetAccountListResult(CmdResult cmdResult) {
        if (cmdResult == null) {
            Toast.makeText(getContext(), LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
        } else if (cmdResult.getErrMsg() != null) {
            Toast.makeText(getContext(), cmdResult.getErrMsg(), Toast.LENGTH_LONG).show();
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
                showSendCoinActivity(accountList.get(accountIdx), targetAddress);
            } else {
                Toast.makeText(getContext(), LocaleController.getString("noSearchAddressAlert", R.string.noSearchAddressAlert), Toast.LENGTH_LONG).show();
            }
        }
        hideProgress();
    }

    protected static class ResultHandler extends Handler {
        private final WeakReference<TransactionSendDetailView> mTransactionSendDetailView;

        private ResultHandler(TransactionSendDetailView transactionSendDetailView) {
            mTransactionSendDetailView = new WeakReference<>(transactionSendDetailView);
        }

        @Override
        public void handleMessage(Message msg) {
            TransactionSendDetailView transactionSendDetailView = mTransactionSendDetailView.get();
            if (transactionSendDetailView != null) transactionSendDetailView.handleMessage(msg);
        }
    }
}
