package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.AmountEditText;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.utils.BigDecimalUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.math.BigDecimal;

public class FeeFormView extends LinearLayout implements View.OnClickListener {

    private boolean isAdvanced = false;

    private int selected = 1;

    private String feeTitles[] = {"Low", "Average", "High"};
    private String fees[] = {"0.0005", "0.0050", "0.0075"};

    private LinearLayout feeLayout;
    private FeeCardView feeCardViews[];
    private AmountEditText editText;
    private TextView modeTextview;

    public FeeFormView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("transactionFee", R.string.transactionFee));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 10, 20, 0));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("transactionFeeDesc", R.string.transactionFeeDesc));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 24));

        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        feeLayout = new LinearLayout(context);
        feeLayout.setOrientation(HORIZONTAL);
        feeLayout.setClipToPadding(false);
        feeLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        frameLayout.addView(feeLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int rightMargin = 10;
        feeCardViews = new FeeCardView[fees.length];
        for (int i = 0; feeCardViews.length > i; i++) {
            boolean isSelected = (selected == i);
            feeCardViews[i] = new FeeCardView(context, isSelected, feeTitles[i], fees[i], Blockchain.getInstance().getReserveDisplayName());
            feeCardViews[i].setId(i);
            feeCardViews[i].setOnClickListener(this);
            if (i == feeCardViews.length - 1) rightMargin = 0;
            feeLayout.addView(feeCardViews[i], LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, rightMargin, 0));
        }

        editText = new AmountEditText(context, true, "", null);
        frameLayout.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 0));
        editText.setVisibility(GONE);

        LinearLayout modeLayout = new LinearLayout(context);
        modeLayout.setOrientation(HORIZONTAL);
        modeLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        modeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInputMode();
            }
        });
        addView(modeLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 20));

        modeTextview = new TextView(context);
        modeTextview.setTextSize(0, AndroidUtilities.dp(16));
        modeTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
        modeTextview.setText(LocaleController.getString("advanced", R.string.advanced));
        modeLayout.addView(modeTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 4, 9, 4));

        ImageView arrowImageView = new ImageView(context);
        arrowImageView.setImageResource(R.drawable.arrow_grey);
        modeLayout.addView(arrowImageView, LayoutHelper.createLinear(7, 11));

    }

    public void showError(String msg) {
        if (isAdvanced && editText != null) {
            editText.showError(msg);
        } else {
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    public String getValue() {
        if (isAdvanced) {
            return editText.getText();
        } else {
            return BigDecimalUtil.getNumberOrigin(fees[selected], "0");
        }
    }

    public String getValueOrigin() {
        if (isAdvanced) {
            return editText.getTextOrigin();
        } else {
            BigDecimal fee = new BigDecimal(fees[selected]);
            return String.format("%.4f", fee);
        }
    }

    private void changeInputMode() {
        if (isAdvanced) {
            isAdvanced = false;
            editText.setVisibility(GONE);
            feeLayout.setVisibility(VISIBLE);
            modeTextview.setText(LocaleController.getString("advanced", R.string.advanced));
        } else {
            isAdvanced = true;
            editText.setVisibility(VISIBLE);
            feeLayout.setVisibility(GONE);
            modeTextview.setText(LocaleController.getString("Default", R.string.Default));
        }
    }

    @Override
    public void onClick(View v) {
        selected = v.getId();
        for (int i = 0; feeCardViews.length > i; i++) {
            boolean isSelected = (selected == i);
            feeCardViews[i].update(isSelected);
        }
    }
}