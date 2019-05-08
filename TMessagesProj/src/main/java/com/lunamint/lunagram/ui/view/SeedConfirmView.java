package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;

public class SeedConfirmView extends LinearLayout {

    private EditText seedTextview;

    public SeedConfirmView(Context context, String seed) {
        super(context);

        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.coral_red));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("writeThisDown", R.string.writeThisDown));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("seedDesc", R.string.seedDesc));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 26));

        seedTextview = new EditText(context);
        seedTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        seedTextview.setTextSize(0, AndroidUtilities.dp(16));
        seedTextview.setGravity(Gravity.CENTER);
        seedTextview.setBackgroundResource(R.drawable.bg_seed);
        seedTextview.setLineSpacing(AndroidUtilities.dp(16), 1.3f);
        seedTextview.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        seedTextview.setText(seed);
        seedTextview.setOnClickListener(onClickListener);
        seedTextview.setFocusableInTouchMode(false);
        addView(seedTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        TextView warningTextview = new TextView(context);
        warningTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        warningTextview.setTextSize(0, AndroidUtilities.dp(14));
        warningTextview.setGravity(Gravity.CENTER);
        warningTextview.setText(LocaleController.getString("cantRecoverSeed", R.string.cantRecoverSeed));
        addView(warningTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 26));
    }

    public void setDesc(String seed) {
        if (seedTextview == null) return;
        if (seed != null) seedTextview.setText(seed);
    }

    public void clear() {
        if (seedTextview != null) seedTextview.setText("");
    }

    private void showCopyAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("warning", R.string.securityWarning));
        builder.setMessage(LocaleController.getString("warningCopySeed", R.string.warningCopySeed));
        builder.setNegativeButton(LocaleController.getString("cancel", R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copy();
            }
        });
        builder.create().show();
    }

    private void copy() {
        if (seedTextview == null) return;
        String seed = seedTextview.getText().toString();
        if (seed.length() == 0) return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(LocaleController.getString("copyMySeedTitle", R.string.copyMySeedTitle), seed);
        if(clipboard != null){
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), LocaleController.getString("seedCopied", R.string.seedCopied), Toast.LENGTH_LONG).show();
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showCopyAlert();
        }
    };
}
