package com.telemint.ui.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.test.DummyVars;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class ValidatorInfoFragment extends BaseFragment {

    public ValidatorInfoFragment(Bundle args) {
        super(args);
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }
    @Override
    public View createView(Context context) {
        actionBar.setTitle("Lunamint - Profile");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));

        RelativeLayout validatorLayout = new RelativeLayout(context);
        fragmentView = validatorLayout;
        makePrototypeLayout(context, validatorLayout);
        return fragmentView;
    }

    protected void clearViews() {
        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            fragmentView = null;
        }
        if (actionBar != null) {
            ViewGroup parent = (ViewGroup) actionBar.getParent();
            if (parent != null) {
                try {
                    parent.removeView(actionBar);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            actionBar = null;
        }
        parentLayout = null;
    }

    private void makePrototypeLayout(Context context, RelativeLayout validatorLayout){

        ScrollView scrollView = new ScrollView(context);
        validatorLayout.addView(scrollView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(58));
        scrollView.addView(mainLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        mainLayout.addView(titleLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout nameLayout = new LinearLayout(context);
        nameLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.addView(nameLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1, Gravity.NO_GRAVITY));

        TextView rankTextview = new TextView(context);
        rankTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
        rankTextview.setTextSize(0, AndroidUtilities.dp(34));
        rankTextview.setText("#1");
        titleLayout.addView(rankTextview);

        TextView titleTextview = new TextView(context);
        titleTextview.setText("Validator Profile");
        titleTextview.setTextColor(context.getResources().getColor(R.color.tab_staking_title));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        nameLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,20,0,0));

        TextView nameTextview = new TextView(context);
        nameTextview.setText("LUNAMINT");
        nameTextview.setTextColor(context.getResources().getColor(R.color.text_value_default));
        nameTextview.setTextSize(0, AndroidUtilities.dp(24));
        nameLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,0,30));

        TextView stakingAmountTextview = new TextView(context);
        stakingAmountTextview.setText("Your Stake: 280,000 ATOM");
        stakingAmountTextview.setTextColor(context.getResources().getColor(R.color.text_green));
        stakingAmountTextview.setTextSize(0, AndroidUtilities.dp(12));
        stakingAmountTextview.setGravity(Gravity.CENTER);
        mainLayout.addView(stakingAmountTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,0,0,4));

        TextView unbondPendingAmountTextview = new TextView(context);
        unbondPendingAmountTextview.setText("Unbond Pending: 50 ATOM");
        unbondPendingAmountTextview.setTextColor(context.getResources().getColor(R.color.text_green));
        unbondPendingAmountTextview.setTextSize(0, AndroidUtilities.dp(12));
        unbondPendingAmountTextview.setGravity(Gravity.CENTER);
        mainLayout.addView(unbondPendingAmountTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,0,0,12));

        String titles[] = DummyVars.validatorInfoTitles;
        String values[] = DummyVars.validatorInfoVars;
        for(int i = 0; titles.length > i; i++){
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL);
            rowLayout.setId(i);
            rowLayout.setBackgroundColor(0xff00);
            mainLayout.addView(rowLayout, LayoutHelper.createLinear(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            LinearLayout textLayout = new LinearLayout(context);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            rowLayout.addView(textLayout, LayoutHelper.createLinear(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 1, Gravity.NO_GRAVITY));

            TextView fieldTitleTextview = new TextView(context);
            fieldTitleTextview.setText(titles[i]);
            fieldTitleTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
            fieldTitleTextview.setTextSize(0, AndroidUtilities.dp(14));
            textLayout.addView(fieldTitleTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,8,0,0));

            TextView fieldValueTextview = new TextView(context);
            fieldValueTextview.setText(values[i]);
            fieldValueTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
            fieldValueTextview.setTextSize(0, AndroidUtilities.dp(14));
            textLayout.addView(fieldValueTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,0,0,8));

            ImageView line = new ImageView(context);
            line.setBackgroundColor(0xff000000);
            line.setAlpha(0.6f);
            mainLayout.addView(line, new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1));
        }


        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16));
        validatorLayout.addView(buttonLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_BOTTOM));

        LinearLayout stakeButton = new LinearLayout(context);
        stakeButton.setGravity(Gravity.CENTER);
        buttonLayout.addView(stakeButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,17,0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.btn_green);
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            drawable = combinedDrawable;
        }
        stakeButton.setBackground(drawable);

        ImageView stakeIconImageview = new ImageView(context);
        stakeIconImageview.setImageResource(R.drawable.icon_plus);
        stakeIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        stakeButton.addView(stakeIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,5,0));
        stakeButton.setOnClickListener(onClickListener);

        TextView stakeTextview = new TextView(context);
        stakeTextview.setText("STAKE");
        stakeTextview.setTextColor(context.getResources().getColor(R.color.btn_text_default));
        stakeTextview.setTextSize(0, AndroidUtilities.dp(12));
        stakeButton.addView(stakeTextview);

        LinearLayout unbondButton = new LinearLayout(context);
        unbondButton.setBackgroundResource(R.drawable.btn_white);
        unbondButton.setGravity(Gravity.CENTER);
        buttonLayout.addView(unbondButton);

        ImageView unbondIconImageview = new ImageView(context);
        unbondIconImageview.setImageResource(R.drawable.icon_x_gray);
        unbondIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        unbondButton.addView(unbondIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,5,0));

        TextView unbondTextview = new TextView(context);
        unbondTextview.setText("UNBOND");
        unbondTextview.setTextColor(context.getResources().getColor(R.color.text_value_default));
        unbondTextview.setTextSize(0, AndroidUtilities.dp(12));
        unbondButton.addView(unbondTextview);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presentStartStakingFragment();
        }
    };

    private void presentStartStakingFragment(){
        Bundle args = new Bundle();
        args.putBoolean("test", true);
        presentFragment(new StartStakingFragment(args));
    }
}
