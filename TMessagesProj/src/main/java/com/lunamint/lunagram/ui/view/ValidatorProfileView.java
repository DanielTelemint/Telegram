package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Validator;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ValidatorProfileView extends RelativeLayout {

    public final static int ACTION_SHOW_HISTORY = 0;
    public final static int ACTION_SHOW_WEBSITE = 1;

    private TextView nameTextview;
    private TextView votingPowerTextview;
    private TextView commissionTextview;

    private LinearLayout websiteLayout;
    private TextView websiteTextview;

    public ValidatorProfileView(Context context, OnClickListener onClickListener) {
        super(context);

        setClipToPadding(false);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(30));

        CardView cardView = new CardView(context);
        cardView.setUseCompatPadding(true);
        cardView.setRadius(AndroidUtilities.dp(8));
        cardView.setCardElevation(UiUtil.getDefaultElevation());
        cardView.setBackgroundResource(R.drawable.validator_profile_bg);
        addView(cardView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 45, 0, 0));

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        cardView.addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(12));
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
        titleTextview.setText(LocaleController.getString("validator", R.string.validator));
        mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 14, 0, 0));

        nameTextview = new TextView(context);
        nameTextview.setTextSize(0, AndroidUtilities.dp(24));
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        nameTextview.setTypeface(Typeface.DEFAULT_BOLD);
        mainLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        websiteLayout = new LinearLayout(context);
        websiteLayout.setOrientation(LinearLayout.HORIZONTAL);
        websiteLayout.setId(ValidatorProfileView.ACTION_SHOW_WEBSITE);
        websiteLayout.setGravity(Gravity.CENTER);
        websiteLayout.setOnClickListener(onClickListener);
        websiteLayout.setPadding(0, 0, 0, AndroidUtilities.dp(22));
        mainLayout.addView(websiteLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        websiteLayout.setVisibility(INVISIBLE);

        websiteTextview = new TextView(context);
        websiteTextview.setTextSize(0, AndroidUtilities.dp(12));
        websiteTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        websiteLayout.addView(websiteTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 6, 0));

        ImageView linkImgview = new ImageView(context);
        linkImgview.setImageResource(R.drawable.ic_validator_link);
        websiteLayout.addView(linkImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout bottomLayout = new LinearLayout(context);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setBackgroundResource(R.drawable.bg_card_blue_opacity20);
        bottomLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        mainLayout.addView(bottomLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout powerLayout = new LinearLayout(context);
        powerLayout.setOrientation(LinearLayout.VERTICAL);
        powerLayout.setGravity(Gravity.CENTER_VERTICAL);
        bottomLayout.addView(powerLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        TextView votingPowerTitleTextview = new TextView(context);
        votingPowerTitleTextview.setTextSize(0, AndroidUtilities.dp(10));
        votingPowerTitleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
        votingPowerTitleTextview.setText(LocaleController.getString("votingPower", R.string.votingPower));
        powerLayout.addView(votingPowerTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        votingPowerTextview = new TextView(context);
        votingPowerTextview.setTextSize(0, AndroidUtilities.dp(16));
        votingPowerTextview.setTypeface(Typeface.DEFAULT_BOLD);
        votingPowerTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        powerLayout.addView(votingPowerTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout commissionLayout = new LinearLayout(context);
        commissionLayout.setOrientation(LinearLayout.VERTICAL);
        commissionLayout.setGravity(Gravity.CENTER_VERTICAL);
        commissionLayout.setPadding(AndroidUtilities.dp(20), 0, 0, 0);
        bottomLayout.addView(commissionLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        TextView commissionTitleTextview = new TextView(context);
        commissionTitleTextview.setTextSize(0, AndroidUtilities.dp(10));
        commissionTitleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
        commissionTitleTextview.setText(LocaleController.getString("commission", R.string.commission));
        commissionLayout.addView(commissionTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        commissionTextview = new TextView(context);
        commissionTextview.setTextSize(0, AndroidUtilities.dp(16));
        commissionTextview.setTypeface(Typeface.DEFAULT_BOLD);
        commissionTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        commissionLayout.addView(commissionTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout profileImageLayout = new LinearLayout(context);
        profileImageLayout.setOrientation(LinearLayout.HORIZONTAL);
        profileImageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileImageLayout.setElevation(AndroidUtilities.dp(20));
        }
        addView(profileImageLayout, 0, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ImageView profileImageview = new ImageView(context);
        profileImageview.setImageResource(R.drawable.validator_profile_small);
        profileImageLayout.addView(profileImageview, LayoutHelper.createLinear(64, 64));

        LinearLayout historyLayout = new LinearLayout(context);
        historyLayout.setId(ValidatorProfileView.ACTION_SHOW_HISTORY);
        historyLayout.setOrientation(LinearLayout.HORIZONTAL);
        historyLayout.setGravity(Gravity.CENTER);
        historyLayout.setBackgroundResource(R.drawable.bg_card_white_radius12);
        historyLayout.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(8));
        historyLayout.setOnClickListener(onClickListener);
        addView(historyLayout, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, ALIGN_PARENT_RIGHT));

        ImageView historyImageview = new ImageView(context);
        historyImageview.setImageResource(R.drawable.ic_validator_history);
        historyLayout.addView(historyImageview, LayoutHelper.createLinear(10, 9, 0, 0, 4, 0));

        TextView historyTextview = new TextView(context);
        historyTextview.setTextSize(0, AndroidUtilities.dp(12));
        historyTextview.setTextColor(ActivityCompat.getColor(context, R.color.medium_slate_blue));
        historyTextview.setText(R.string.history);
        historyLayout.addView(historyTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

    }

    public void update(Validator data, double totalPower) {
        nameTextview.setText(data.getDescription().getMoniker());

        try {
            double power = data.getDelegatorShares();
            if (totalPower > 0) {
                power = power / totalPower * 100;
                votingPowerTextview.setText("#" + String.valueOf(data.getRank()) + " (" + String.format("%.2f", power) + "%)");
            } else {
                votingPowerTextview.setText("#" + String.valueOf(data.getRank()) + " (" + String.format("%.2f", data.getDelegatorShares()) + ")");
            }

            float commissionRate = Float.parseFloat(data.getCommission().getRate()) * 100;
            commissionTextview.setText(String.format("%.1f", commissionRate) + "%");
        } catch (Exception e) {
            votingPowerTextview.setText("#" + data.getRank() + " (" + String.format("%.2f", data.getDelegatorShares()) + ")");
            commissionTextview.setText(String.format("%.1f", data.getCommission().getRate()));
        }

        if (data.getDescription().getWebsite().equals("")) {
            websiteLayout.setVisibility(INVISIBLE);
        } else {
            websiteLayout.setVisibility(VISIBLE);
            websiteTextview.setText(data.getDescription().getWebsite());
        }
    }
}
