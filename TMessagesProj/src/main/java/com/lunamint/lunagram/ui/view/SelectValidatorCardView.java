package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class SelectValidatorCardView extends CardView {

    private LinearLayout emptyLayout;
    private LinearLayout mainLayout;
    private TextView nameTextview;

    public SelectValidatorCardView(Context context, String validatorName, OnClickListener onClickListener) {
        super(context);

        setOnClickListener(onClickListener);

        emptyLayout = new LinearLayout(context);
        emptyLayout.setGravity(Gravity.CENTER);
        addView(emptyLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        TextView emptyTextview = new TextView(context);
        emptyTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        emptyTextview.setTextSize(0, AndroidUtilities.dp(16));
        emptyTextview.setText(R.string.selectValidator);
        emptyLayout.addView(emptyTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        mainLayout.setVisibility(INVISIBLE);

        ImageView profileImageview = new ImageView(context);
        profileImageview.setImageResource(R.drawable.validator_profile_small);
        mainLayout.addView(profileImageview, LayoutHelper.createLinear(40, 40, 0, 0, 10, 0));

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(textLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 6, 0));

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        titleTextview.setTextSize(0, AndroidUtilities.dp(12));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("validator", R.string.validator));
        textLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        nameTextview = new TextView(context);

        nameTextview.setTextSize(0, AndroidUtilities.dp(20));
        nameTextview.setTypeface(Typeface.DEFAULT_BOLD);
        textLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        if (onClickListener != null) {
            setUseCompatPadding(true);
            setRadius(AndroidUtilities.dp(8));
            setCardElevation(UiUtil.getDefaultElevation());

            mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20));

            setBackgroundResource(R.drawable.btn_radius8_dark_blue);

            nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));

            ImageView arrowImageView = new ImageView(context);
            arrowImageView.setImageResource(R.drawable.arrow_grey);
            mainLayout.addView(arrowImageView, LayoutHelper.createLinear(7, 11));

        } else {
            setRadius(0);
            setCardElevation(0);

            setBackgroundColor(ActivityCompat.getColor(context, R.color.white));

            nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        }
        update(validatorName);
    }

    public void update(String validatorName) {
        if (nameTextview == null) return;
        if (validatorName == null) {
            emptyLayout.setVisibility(VISIBLE);
            mainLayout.setVisibility(INVISIBLE);
        } else {
            emptyLayout.setVisibility(GONE);
            mainLayout.setVisibility(VISIBLE);
            nameTextview.setText(validatorName);
        }
    }
}
