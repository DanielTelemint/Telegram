package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.SettingsAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.Setting;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.Components.LayoutHelper;

public class SettingsActivity extends LunagramBaseActivity {

    boolean canUseFingerprint = false;

    private String accountName;

    private SettingsAdapter settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("settings", R.string.settings));

        accountName = getIntent().getStringExtra("accountName");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        ListView listView = new ListView(this);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setDivider(new ColorDrawable(0x88E4E9FE));
        listView.setDividerHeight(AndroidUtilities.dp(1));
        mainLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (Build.VERSION.SDK_INT >= 23) {
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
            if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                if (hasPassword()) canUseFingerprint = true;
            }
        }

        settingsAdapter = new SettingsAdapter(this, 0, canUseFingerprint);
        listView.setAdapter(settingsAdapter);

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private boolean hasPassword() {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = pref.getString("W-" + accountName.replaceAll(" ", ""), "");
        return !pwd.equals("");
    }

    private String getCurrentNodeName() {
        return Blockchain.getInstance().getNodeName();
    }

    private void update() {
        if (settingsAdapter != null) {
            Setting setting = new Setting();
            setting.setEnabledFingerprint(isEnabledFingerprint(accountName));
            setting.setNode(getCurrentNodeName());
            settingsAdapter.update(setting);
            settingsAdapter.notifyDataSetChanged();
        }
    }

    private void changeFingerprintSetting() {
        if (isEnabledFingerprint(accountName)) {
            setEnabledFingerprint(accountName, false);
            update();
        } else {
            showEnableFingerprintActivity();
        }
    }

    private void showEnableFingerprintActivity() {
        Intent intent = new Intent(this, EnableFingerprintActivity.class);
        intent.putExtra("accountName", accountName);
        startActivity(intent);
    }

    private void showLunagramTwitter() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/LunagramApp"));
        startActivity(intent);
    }

    private void showTerms() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lunagram.io/policy"));
        startActivity(intent);
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@lunamint.com"});
        startActivity(Intent.createChooser(intent, LocaleController.getString("ChooseEmail", R.string.chooseEmailApp)));
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (canUseFingerprint) {
                switch (position) {
                    case 0:
                        changeFingerprintSetting();
                        break;
                    case 1:
                        showSelectNodeActivity();
                        break;
                    case 2:
                        showLunagramTwitter();
                        break;
                    case 3:
                        showTerms();
                        break;
                    case 4:
                        sendFeedback();
                        break;
                }
            } else {
                switch (position) {
                    case 0:
                        showSelectNodeActivity();
                        break;
                    case 1:
                        showLunagramTwitter();
                        break;
                    case 2:
                        showTerms();
                        break;
                    case 3:
                        sendFeedback();
                        break;
                }
            }
        }
    };
}
