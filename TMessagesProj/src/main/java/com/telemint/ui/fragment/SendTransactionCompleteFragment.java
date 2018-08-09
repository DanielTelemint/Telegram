package com.telemint.ui.fragment;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import com.telemint.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

public class SendTransactionCompleteFragment extends BaseFragment {

    public SendTransactionCompleteFragment(Bundle args) {
        super(args);
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }
    @Override
    public View createView(Context context) {
        actionBar.setTitle(getArguments().getString("title"));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));



        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundColor(0xff004d40);
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

        ImageView completeImageview = new ImageView(context);
        completeImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        completeImageview.setImageResource(R.drawable.tx_complete_check);
        mainLayout.addView(completeImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView resultTextview = new TextView(context);
        resultTextview.setTextColor(Color.WHITE);
        resultTextview.setTextSize(0, AndroidUtilities.dp(20));
        resultTextview.setText(getArguments().getString("message"));
        mainLayout.addView(resultTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0,36,0,54));

        LinearLayout okayButton = new LinearLayout(context);
        okayButton.setGravity(Gravity.CENTER);
        mainLayout.addView(okayButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        Drawable drawable = context.getResources().getDrawable(R.drawable.btn_white);
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            drawable = combinedDrawable;
        }
        okayButton.setBackground(drawable);

        ImageView okayIconImageview = new ImageView(context);
        okayIconImageview.setImageResource(R.drawable.icon_plus);
        okayIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        okayButton.setOnClickListener(onClickListener);
        okayButton.addView(okayIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,5,0));


        TextView okayTextview = new TextView(context);
        okayTextview.setText("OKAY");
        okayTextview.setTextColor(context.getResources().getColor(R.color.btn_text_green));
        okayTextview.setTextSize(0, AndroidUtilities.dp(12));
        okayButton.addView(okayTextview);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presentDialogsFragment();
        }
    };

    private void presentDialogsFragment(){
        LaunchActivity launchActivity = (LaunchActivity) getParentActivity();

        DialogsActivity dialogsActivity = new DialogsActivity(arguments);
        launchActivity.presentFragment(dialogsActivity, false, true);
    }
}
