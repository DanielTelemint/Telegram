package com.telemint.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.adapter.MainTabStakeAdapter;
import com.telemint.ui.fragment.ValidatorListFragment;
import com.telemint.ui.test.TestData;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;

public class StakingView extends LinearLayout {

    private DialogsActivity mActivity;

    private TextView addressTextview;

    private MainTabStakeAdapter adapter;

    public StakingView(Context context, final DialogsActivity activity) {
        super(context);

        mActivity = activity;

        setOrientation(LinearLayout.VERTICAL);

        TextView titleTextview = new TextView(context);
        titleTextview.setText("Hello, Cosmonaut");
        titleTextview.setTextColor(getResources().getColor(R.color.tab_staking_title));
        titleTextview.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(0));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 20, 0, 0));

        addressTextview = new TextView(context);
        addressTextview.setText("cosmosaccaddr1rvm0em6w3qkzcwnzf9hkqvksujl895dfww4ecn");
        addressTextview.setTextColor(getResources().getColor(R.color.text_value_default));
        addressTextview.setTextSize(0, AndroidUtilities.dp(10));
        addressTextview.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(0));
        addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16));
        addView(buttonLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout stakeButton = new LinearLayout(context);
        stakeButton.setGravity(Gravity.CENTER);
        stakeButton.setPadding(AndroidUtilities.dp(10),0,AndroidUtilities.dp(10),0);
        buttonLayout.addView(stakeButton);

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
        stakeButton.addView(stakeIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0,0,5,0));
        stakeButton.setOnClickListener(onClickListener);

        TextView stakeTextview = new TextView(context);
        stakeTextview.setText("STAKE / UNBOND");
        stakeTextview.setTextColor(getResources().getColor(R.color.btn_text_default));
        stakeTextview.setTextSize(0, AndroidUtilities.dp(12));
        stakeButton.addView(stakeTextview);

        ListView listView = new ListView(context);
        listView.setDividerHeight(1);
        listView.setPadding(AndroidUtilities.dp(10),0, AndroidUtilities.dp(10),0);
        addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        adapter = new MainTabStakeAdapter(context, 0, TestData.getInstance().getStakeTabMenu());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presentValidatorListFragment();
        }
    };

    private void presentValidatorListFragment(){
        Bundle args = new Bundle();
        args.putBoolean("test", true);
        mActivity.presentFragment(new ValidatorListFragment(args));
    }


}
