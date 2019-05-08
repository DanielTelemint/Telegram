package com.lunamint.lunagram.ui.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.Secure;
import com.lunamint.lunagram.ui.SelectNodeActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.lang.ref.WeakReference;

public class LunagramBaseActivity extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate {

    private final static int id_fingerprint_textview = 1000;
    private final static int id_fingerprint_imageview = 1001;

    private boolean pause = false;
    private boolean supportFingerprintUI = false;
    private org.telegram.ui.ActionBar.AlertDialog fingerprintDialog;
    private CancellationSignal cancellationSignal;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private boolean selfCancelled;

    private ProgressDialog mProgressDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable drawable = new ColorDrawable(Theme.getColor(Theme.key_actionBarDefault));
        getSupportActionBar().setBackgroundDrawable(drawable);

        setIsSupportFingerprintUI();

        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.nodeChanged);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setPaused(false);

        if (Secure.isRooted()) {
            showRootedAlert();
            return;
        }

        if (Secure.isAdbEnabled(this)) {
            showAdbAlert();
            return;
        }

        if (!Secure.isValidSignature(this)) {
            showSignatureAlert();
        }
    }

    @Override
    protected void onPause() {
        setPaused(true);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.nodeChanged);
        super.onDestroy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.nodeChanged) {
            finish();
        }
    }

    protected void checkFingerprint() {
        if (isFinishing()) return;
        if (Build.VERSION.SDK_INT >= 23 && SharedConfig.useFingerprint) {
            try {
                if (fingerprintDialog != null && fingerprintDialog.isShowing()) return;
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    if (!supportFingerprintUI) {
                        RelativeLayout relativeLayout = new RelativeLayout(this);
                        relativeLayout.setPadding(AndroidUtilities.dp(24), 0, AndroidUtilities.dp(24), 0);

                        TextView fingerprintTextView = new TextView(this);
                        fingerprintTextView.setId(id_fingerprint_textview);
                        fingerprintTextView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
                        fingerprintTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        fingerprintTextView.setText(LocaleController.getString("FingerprintInfo", R.string.FingerprintInfo));
                        relativeLayout.addView(fingerprintTextView);
                        RelativeLayout.LayoutParams layoutParams = LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                        fingerprintTextView.setLayoutParams(layoutParams);

                        fingerprintImageView = new ImageView(this);
                        fingerprintImageView.setImageResource(R.drawable.ic_fp_40px);
                        fingerprintImageView.setId(id_fingerprint_imageview);
                        relativeLayout.addView(fingerprintImageView, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 20, 0, 0, RelativeLayout.ALIGN_PARENT_START, RelativeLayout.BELOW, id_fingerprint_textview));

                        fingerprintStatusTextView = new TextView(this);
                        fingerprintStatusTextView.setGravity(Gravity.CENTER_VERTICAL);
                        fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", R.string.FingerprintHelp));
                        fingerprintStatusTextView.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                        fingerprintStatusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack) & 0x42ffffff);
                        relativeLayout.addView(fingerprintStatusTextView);
                        layoutParams = LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
                        layoutParams.setMarginStart(AndroidUtilities.dp(16));
                        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id_fingerprint_imageview);
                        layoutParams.addRule(RelativeLayout.ALIGN_TOP, id_fingerprint_imageview);
                        layoutParams.addRule(RelativeLayout.END_OF, id_fingerprint_imageview);
                        fingerprintStatusTextView.setLayoutParams(layoutParams);

                        org.telegram.ui.ActionBar.AlertDialog.Builder builder = new org.telegram.ui.ActionBar.AlertDialog.Builder(this);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setView(relativeLayout);
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (cancellationSignal != null) {
                                    selfCancelled = true;
                                    cancellationSignal.cancel();
                                    cancellationSignal = null;
                                }
                            }
                        });
                        if (fingerprintDialog != null) {
                            try {
                                if (fingerprintDialog.isShowing()) {
                                    fingerprintDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        fingerprintDialog = builder.show();
                    }

                    cancellationSignal = new CancellationSignal();
                    selfCancelled = false;
                    fingerprintManager.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            if (!selfCancelled) {
                                showFingerprintError(errString);
                            }
                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            showFingerprintError(helpString);
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            showFingerprintError(LocaleController.getString("FingerprintNotRecognized", R.string.FingerprintNotRecognized));
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            try {
                                if (fingerprintDialog.isShowing()) {
                                    fingerprintDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            fingerprintDialog = null;
                            onCheckFingerprintDone();
                        }
                    }, null);
                }
            } catch (Throwable e) {
                //ignore
            }
        }
    }

    protected void onCheckFingerprintDone() {
    }

    protected boolean isEnabledFingerprint(String accountName) {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        return pref.getBoolean("W-" + accountName.replaceAll(" ", "") + "_f", true);
    }

    protected void setEnabledFingerprint(String accountName, boolean isEnabled) {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        pref.edit().putBoolean("W-" + accountName.replaceAll(" ", "") + "_f", isEnabled).commit();
    }

    private void setIsSupportFingerprintUI() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if ("samsung".equals(manufacturer)) {
            if (model.contains("SM-G973") || model.contains("SM-G975")) {
                supportFingerprintUI = true;
            }
        }
    }

    private void showFingerprintError(CharSequence error) {
        if (fingerprintImageView == null || supportFingerprintUI) return;
        fingerprintImageView.setImageResource(R.drawable.ic_fingerprint_error);
        fingerprintStatusTextView.setText(error);
        fingerprintStatusTextView.setTextColor(0xfff4511e);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(fingerprintStatusTextView, 2, 0);
    }

    private void showRootedAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("warning", R.string.warning));
        builder.setMessage(LocaleController.getString("deviceRootedError", R.string.deviceRootedError));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    private void showAdbAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("securityWarning", R.string.securityWarning));
        builder.setMessage(LocaleController.getString("developerOptionEnabled", R.string.developerOptionEnabled));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    private void showSignatureAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("securityWarning", R.string.securityWarning));
        builder.setMessage(LocaleController.getString("fileForgeryDetected", R.string.fileForgeryDetected));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    private void showScreenCaptureAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("warning", R.string.warning));
        builder.setMessage(LocaleController.getString("detectedScreenCapture", R.string.detectedScreenCapture));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    protected void showNodeConnectionErrorAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("error", R.string.error));
        builder.setMessage(LocaleController.getString("changeNodeDesc", R.string.changeNodeDesc));
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showSelectNodeActivity();
            }
        });
        builder.show();
    }

    protected void showNetworkErrorAlert() {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("error", R.string.error));
        builder.setMessage(LocaleController.getString("networkError", R.string.networkError));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    protected void showSelectNodeActivity() {
        Intent intent = new Intent(this, SelectNodeActivity.class);
        startActivity(intent);
    }

    private synchronized boolean isPaused() {
        return pause;
    }

    private synchronized void setPaused(boolean isPaused) {
        pause = isPaused;
    }

    protected void showProgress() {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(this, "", LocaleController.getString("sending", R.string.sending), true);
        mProgressDialog.setCancelable(false);
    }

    protected void hideProgress() {
        if (mProgressDialog == null) return;
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void handleMessage(Message msg) {
    }

    protected static class ResultHandler extends Handler {
        private final WeakReference<LunagramBaseActivity> mActivity;

        public ResultHandler(LunagramBaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LunagramBaseActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
