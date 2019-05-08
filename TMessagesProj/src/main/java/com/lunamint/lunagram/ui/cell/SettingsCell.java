package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Setting;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class SettingsCell extends LinearLayout {

    private boolean canUseFingerprint;

    private TextView nameTextview;
    private TextView valueTextview;
    private ImageView arrowImgview;

    private String titles[] = {
            LocaleController.getString("signWithFingerprint", R.string.signWithFingerprint),
            LocaleController.getString("selectedNode", R.string.selectedNode),
            LocaleController.getString("followTwitter", R.string.followTwitter),
            LocaleController.getString("termsOfService", R.string.termsOfService),
            LocaleController.getString("feedback", R.string.feedback),
    };

    public SettingsCell(Context context, boolean canUseFingerprint) {
        super(context);
        this.canUseFingerprint = canUseFingerprint;

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(ActivityCompat.getColor(context, R.color.white));
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(26), AndroidUtilities.dp(16), AndroidUtilities.dp(26));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        nameTextview.setTextSize(0, AndroidUtilities.dp(14));
        addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 12, 0));

        valueTextview = new TextView(context);
        valueTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        valueTextview.setTextSize(0, AndroidUtilities.dp(14));
        valueTextview.setGravity(Gravity.RIGHT);
        addView(valueTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 14, 0));

        arrowImgview = new ImageView(getContext());
        arrowImgview.setImageResource(R.drawable.ic_arrow_default);
        arrowImgview.setAlpha(0.6f);
        addView(arrowImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void update(int position, Setting setting) {
        if (nameTextview == null) return;
        int i = position;
        if (!canUseFingerprint) i++;

        nameTextview.setText(titles[i]);

        if (i >= 2) {
            arrowImgview.setVisibility(GONE);
        } else {
            arrowImgview.setVisibility(VISIBLE);
        }

        if (setting != null) {
            if (i == 0) {
                String text = setting.isEnabledFingerprint() ? "ON" : "OFF";
                valueTextview.setText(text);
            } else if (i == 1) {
                valueTextview.setText(setting.getNode());
            }
        }
    }
}
