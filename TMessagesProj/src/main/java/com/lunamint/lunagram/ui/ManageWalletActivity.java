package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.AccountListAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.utils.Parser;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class ManageWalletActivity extends LunagramBaseActivity {

    private ArrayList<AccountInfo> accountList;

    private AccountListAdapter accountListAdapter;

    private ListView listView;
    private LinearLayout loadingLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_plus) {
            showCreateAccountActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("wallets", R.string.wallets));

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout frameLayout = new FrameLayout(this);
        mainLayout.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnItemLongClickListener(onItemLongClickListener);
        listView.setDivider(new ColorDrawable(0x88E4E9FE));
        listView.setDividerHeight(AndroidUtilities.dp(1));
        frameLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setVisibility(View.INVISIBLE);

        accountListAdapter = new AccountListAdapter(this, 0, accountList);
        listView.setAdapter(accountListAdapter);

        loadingLayout = new LinearLayout(this);
        loadingLayout.setGravity(Gravity.CENTER);
        frameLayout.addView(loadingLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(this);
        loadingLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccountList();
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        CmdResult cmdResult = ((CmdResult) msg.getData().getSerializable("result"));
        switch (msg.what) {
            case WalletManager.GET_ACCOUNT_LIST:
                onGetAccountListResult(cmdResult);
                break;
        }
    }

    private void getAccountList() {
        if(loadingLayout == null) return;
        loadingLayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        WalletManager.getInstance().getAccountList(new ResultHandler(this));
    }

    private void onGetAccountListResult(CmdResult cmdResult) {
        if (cmdResult == null) {
            Toast.makeText(ManageWalletActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
        } else if (cmdResult.getErrMsg() != null) {
            Toast.makeText(ManageWalletActivity.this, cmdResult.getErrMsg(), Toast.LENGTH_LONG).show();
        } else {
            accountList = Parser.getAccountList(cmdResult.getData());
            update();
        }
    }

    private void update() {
        if (accountListAdapter != null) {
            accountListAdapter.update(accountList);
            accountListAdapter.notifyDataSetChanged();
        }
        if(loadingLayout == null) return;
        loadingLayout.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    private void copyAddress(String address) {
        if (address == null) return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(LocaleController.getString("copyMyAddressTitle", R.string.copyMyAddressTitle), address);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, LocaleController.getString("addressCopied", R.string.addressCopied), Toast.LENGTH_LONG).show();
        }
    }

    private void showAccountSettingDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new CharSequence[]{LocaleController.getString("deleteWallet", R.string.deleteWallet), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (accountList == null) return;
                if (i == 0) {
                    showDeleteAccountActivity(accountList.get(position).getName());
                } else {
                    copyAddress(accountList.get(position).getAddress());
                }
            }
        });
        builder.create().show();
    }

    private void showCreateAccountActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void showDeleteAccountActivity(String accountName) {
        Intent intent = new Intent(this, DeleteAccountActivity.class);
        intent.putExtra("accountName", accountName);
        startActivity(intent);
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            preferences.edit().putString("currentAccountName", accountList.get(position).getName()).commit();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.accountChanged);
            finish();
        }
    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showAccountSettingDialog(position);
            return true;
        }
    };


}
