package com.telemint.ui.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telemint.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class StartStakingFragment extends BaseFragment {

    public StartStakingFragment(Bundle args) {
        super(args);
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }
    @Override
    public View createView(Context context) {
        actionBar.setTitle("Stake");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView = mainLayout;
        makePrototypeLayout(context, mainLayout);
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

    private void makePrototypeLayout(Context context, LinearLayout mainLayout){

        LinearLayout profileLayout = new LinearLayout(context);
        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setPadding(AndroidUtilities.dp(10),AndroidUtilities.dp(10),AndroidUtilities.dp(10),AndroidUtilities.dp(10));
        mainLayout.addView(profileLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView titleTextview = new TextView(context);
        titleTextview.setText("Validator");
        titleTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        profileLayout.addView(titleTextview);

        TextView nameTextview = new TextView(context);
        nameTextview.setText("LUNAMINT");
        nameTextview.setTextColor(context.getResources().getColor(R.color.text_value_default));
        nameTextview.setTextSize(0, AndroidUtilities.dp(14));
        profileLayout.addView(nameTextview);

        ImageView line = new ImageView(context);
        line.setBackgroundColor(0xff000000);
        line.setAlpha(0.6f);
        mainLayout.addView(line, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

        TextView balanceTextview = new TextView(context);
        balanceTextview.setGravity(Gravity.CENTER);
        balanceTextview.setText("Wallet Balance: 250,000 ATOM");
        balanceTextview.setTextColor(context.getResources().getColor(R.color.text_value_default));
        balanceTextview.setTextSize(0, AndroidUtilities.dp(16));
        mainLayout.addView(balanceTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,16,0,16));

        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextInputLayout amountLayout = (TextInputLayout) inflater.inflate(R.layout.text_input_layout, null);
        amountLayout.setHintTextAppearance(R.style.TextInputAppearance);
        mainLayout.addView(amountLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,10,0,10,0));

        EditText amountEdittext = new EditText(context);
        amountEdittext.setHint("Amount to Stake");
        amountEdittext.setTextColor(context.getResources().getColor(R.color.text_title_default));
        amountEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            amountEdittext.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.tab_text_selected)));
        }
        amountLayout.addView(amountEdittext, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16));
        mainLayout.addView(buttonLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

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
        stakeButton.setOnClickListener(onClickListener);
        stakeButton.addView(stakeIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,5,0));

        TextView stakeTextview = new TextView(context);
        stakeTextview.setText("STAKE");
        stakeTextview.setTextColor(context.getResources().getColor(R.color.btn_text_default));
        stakeTextview.setTextSize(0, AndroidUtilities.dp(12));
        stakeButton.addView(stakeTextview);

        LinearLayout cancelButton = new LinearLayout(context);
        cancelButton.setBackgroundResource(R.drawable.btn_black);
        cancelButton.setGravity(Gravity.CENTER);
        buttonLayout.addView(cancelButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        ImageView cancelIconImageview = new ImageView(context);
        cancelIconImageview.setImageResource(R.drawable.icon_x);
        cancelIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        cancelButton.addView(cancelIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,5,0));

        TextView unbondTextview = new TextView(context);
        unbondTextview.setText("CANCEL");
        unbondTextview.setTextColor(context.getResources().getColor(R.color.btn_text_default));
        unbondTextview.setTextSize(0, AndroidUtilities.dp(12));
        cancelButton.addView(unbondTextview);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presentCompleteFragment();
        }
    };

    private void presentCompleteFragment(){
        Bundle args = new Bundle();
        args.putString("title", "Stake");
        args.putString("message", "Staking Complete!");
        presentFragment(new SendTransactionCompleteFragment(args));
    }
}
